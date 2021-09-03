//
//  ScheduleUtils.swift
//  SmartCalendar
//

import Foundation
import Alamofire

class ScheduleUtils{
    public static func addStorage(schedule: Schedule, local_container: ScheduleContainer, completion: @escaping (Bool) -> Void){
        local_container.append(schedule)
        local_container.markDirty(schedule: schedule, type: 1)
        StorageUtils.saveScheduleToLocal(container: local_container)
        ScheduleUtils.addToServer(schedule: schedule, completion: { (status) in
            if(status){
                local_container.eraseDirty(schedule: schedule)
                StorageUtils.saveScheduleToLocal(container: local_container)
                completion(true)
            }
            else{
                completion(false)
            }
        })
    }
    
    public static func addToServer(schedule: Schedule, completion: @escaping (Bool) -> Void){
        let server = Config.host + "/calendar/add"
        let body: [String: Any]
        if(schedule.position == nil){
            body = ["uuid": schedule.id.uuidString, "name": schedule.name, "category": schedule.category.id, "start": schedule.start.timeIntervalSince1970, "end": schedule.end.timeIntervalSince1970]
        }
        else{
            body = ["uuid": schedule.id.uuidString, "name": schedule.name, "category": schedule.category.id, "start": schedule.start.timeIntervalSince1970, "end": schedule.end.timeIntervalSince1970, "position": ["name": schedule.position!.name, "latitude": schedule.position!.coordinate.latitude, "longitude": schedule.position!.coordinate.longitude]]
        }
        AF.request(server, method: .post, parameters: body, encoding: JSONEncoding.default, requestModifier: { $0.timeoutInterval = 20}).validate().responseJSON { response in
            if(response.error == nil){
                let result_dict = response.value as? Dictionary<String, Int>
                if(result_dict != nil && result_dict!["status"] == 1){
                    completion(true)
                }
                else{
                    print("Error occurred when phasing response json!")
                    completion(false)
                }
            }
            else{
                print(response.error!)
                print("Error occurred when requesting server!")
                completion(false)
            }
        }
    }
    
    public static func removeStorage(schedule: Schedule, local_container: ScheduleContainer, completion: @escaping (Bool) -> Void){
        local_container.remove(schedule: schedule)
        StorageUtils.saveScheduleToLocal(container: local_container)
        ScheduleUtils.removeFromServer(schedule: schedule, completion: { (status) in
            if(status){
                local_container.eraseDirty(schedule: schedule)
                StorageUtils.saveScheduleToLocal(container: local_container)
                completion(true)
            }
            else{
                completion(false)
            }
        })
    }
    
    public static func removeFromServer(schedule: Schedule, completion: @escaping (Bool) -> Void){
        let server = Config.host + "/calendar/remove"
        let body: [String: Any] = ["uuid": schedule.id.uuidString]
        AF.request(server, method: .post, parameters: body, encoding: JSONEncoding.default, requestModifier: { $0.timeoutInterval = 20}).validate().responseJSON { response in
            if(response.error == nil){
                let result_dict = response.value as? Dictionary<String, Int>
                if(result_dict != nil && result_dict!["status"] == 1){
                    completion(true)
                }
                else{
                    print("Error occurred when phasing response json!")
                    completion(false)
                }
            }
            else{
                print(response.error!)
                print("Error occurred when requesting server!")
                completion(false)
            }
        }
    }
    
    public static func getStorage(local_container: ScheduleContainer, completion: @escaping (Bool, [Schedule]) -> Void){
        let local_rec = StorageUtils.getScheduleFromLocal()
        if(local_rec == nil){
            ScheduleUtils.fetchFromServer(completion: { (status, schedule_list) in
                if(status){
                    local_container.append(contentsOf: schedule_list)
                }
                StorageUtils.saveScheduleToLocal(container: local_container)
                completion(true, Array(local_container.schedules.values))
            })
        }
        else{
            ScheduleUtils.synchronizeStorage()
            local_container.append(contentsOf: Array(local_rec!.schedules.values))
            completion(true, Array(local_rec!.schedules.values))
        }
    }
    
    public static func fetchFromServer(completion: @escaping (Bool, [Schedule]) -> Void){
        let server = Config.host + "/calendar/fetch"
        AF.request(server, method: .get, requestModifier: { $0.timeoutInterval = 20}).validate().responseJSON { response in
            if(response.error == nil && response.value != nil){
                let result = JSON(response.value!)
                if(result["status"].intValue == 1){
                    var schedule_list: [Schedule] = []
                    for(_, item): (String, JSON) in result["schedule"] {
                        let uuid_str = item["uuid"].stringValue
                        let name = item["name"].stringValue
                        let category_id = item["category"]["id"].intValue
                        let category_name = item["category"]["name"].stringValue
                        let start = item["start"].floatValue
                        let end = item["end"].floatValue
                        var pos: GeoPoint? = nil
                        if(item["position"].exists()){
                            pos = GeoPoint(name: item["position"]["name"].stringValue, latitude: item["position"]["latitude"].doubleValue, longitude: item["position"]["latitude"].doubleValue)
                        }
                        let uuid = UUID(uuidString: uuid_str)
                        if(uuid != nil){
                            let schedule_obj = Schedule(id: uuid!, dirty: 0, name: name, categoryId: category_id, categoryName: category_name, start: Date(timeIntervalSince1970: TimeInterval(start)), end: Date(timeIntervalSince1970: TimeInterval(end)), pos: pos)
                            schedule_list.append(schedule_obj)
                        }
                        else{
                            print("Error when phasing uuid of schedule \(name)!")
                        }
                    }
                    completion(true, schedule_list)
                }
                else{
                    completion(false, [])
                }
            }
            else{
                print(response.error!)
                print("Error occurred when requesting server!")
                completion(false, [])
            }
        }
    }
    
    public static func synchronizeStorage(){
        let local_rec = StorageUtils.getScheduleFromLocal()
        if(local_rec != nil){
            for (_, schedule) in local_rec!.schedules {
                if(schedule.dirty == 1){
                    ScheduleUtils.addToServer(schedule: schedule, completion: { (status) in
                        if(status){
                            local_rec!.eraseDirty(schedule: schedule)
                            StorageUtils.saveScheduleToLocal(container: local_rec!)
                        }
                    })
                }
                else if(schedule.dirty == 2){
                    ScheduleUtils.removeFromServer(schedule: schedule, completion: { (status) in
                        if(status){
                            local_rec!.eraseDirty(schedule: schedule)
                            StorageUtils.saveScheduleToLocal(container: local_rec!)
                        }
                    })
                }
            }
        }
    }
    
    public static func getCategory(completion: @escaping (Bool, [(id: Int, name: String)]) -> Void){
        let server = Config.host + "/calendar/category"
        AF.request(server, method: .get, requestModifier: { $0.timeoutInterval = 20}).validate().responseJSON { response in
            if(response.error == nil && response.value != nil){
                let result = JSON(response.value!)
                if(result["status"].intValue == 1){
                    var category_list: [(id: Int, name: String)] = []
                    for (_, item): (String, JSON) in result["category"] {
                        let id = item["id"].intValue
                        let name = item["name"].stringValue
                        category_list.append((id: id, name: name))
                    }
                    completion(true, category_list)
                }
                else{
                    completion(false, [])
                }
            }
            else{
                print(response.error!)
                completion(false, [])
            }
        }
    }
    
    public static func getOptimalSchedule(feature: DynamicSchedule, completion: @escaping (Bool, [Schedule]) -> Void){
        let server = Config.host + "/calendar/arrange"
        let body: [String: Any] = ["name": feature.name, "category": feature.category.id, "duration": feature.duration, "from": feature.from.timeIntervalSince1970, "to": feature.to.timeIntervalSince1970]
        AF.request(server, method: .post, parameters: body, encoding: JSONEncoding.default, requestModifier: { $0.timeoutInterval = 20}).validate().responseJSON { response in
            if(response.error == nil && response.value != nil){
                let result = JSON(response.value!)
                if(result["status"].intValue == 1){
                    var schedule_list: [Schedule] = []
                    for (_, item): (String, JSON) in result["schedule"]{
                        let name = item["name"].stringValue
                        let category_id = item["category"]["id"].intValue
                        let category_name = item["category"]["name"].stringValue
                        let start = item["start"].floatValue
                        let end = item["end"].floatValue
                        let schedule = Schedule(name: name, categoryId: category_id, categoryName: category_name, start: Date(timeIntervalSince1970: TimeInterval(start)), end: Date(timeIntervalSince1970: TimeInterval(end)), pos: nil)
                        schedule_list.append(schedule)
                    }
                    completion(true, schedule_list)
                }
                else{
                    print("Error occurred when phasing response json!")
                    completion(false, [])
                }
            }
            else{
                print(response.error!)
                print("Error occurred when requesting server!")
                completion(false, [])
            }
        }
    }
    
    public static func getSharePicture(completion: @escaping (Bool, UIImage?) -> Void){
        let server = Config.host + "/calendar/share"
        AF.request(server, method: .get, requestModifier: { $0.timeoutInterval = 30}).validate().responseJSON { response in
            if(response.error == nil && response.value != nil){
                let result = JSON(response.value!)
                if(result["status"].intValue == 1){
                    let picture_base64 = result["picture"].stringValue
                    let picture_data = Data(base64Encoded: picture_base64, options: .ignoreUnknownCharacters)
                    if(picture_data != nil){
                        let picture = UIImage.init(data: picture_data!)
                        if(picture != nil){
                            UIImageWriteToSavedPhotosAlbum(picture!, nil, nil, nil)
                            completion(true, picture)
                        }
                        else{
                            completion(false, nil)
                        }
                    }
                    else{
                        completion(false, nil)
                    }
                }
                else{
                    completion(false, nil)
                }
            }
            else{
                print(response.error!)
                completion(false, nil)
            }
        }
    }
}
