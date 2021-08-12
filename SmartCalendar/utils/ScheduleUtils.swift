//
//  ScheduleUtils.swift
//  SmartCalendar
//

import Foundation

class ScheduleUtils{
    public static func addToServer(schedule: Schedule, completion: @escaping (Int) -> Void){
        let body: [String: Any] = ["uuid": schedule.id.uuidString, "name": schedule.name, "category": schedule.category.id, "start": schedule.start.timeIntervalSince1970, "end": schedule.end.timeIntervalSince1970]
        let body_str = try? JSONSerialization.data(withJSONObject: body)
        let server = Config.host + "/calendar/add"
        var request = URLRequest(url: URL(string: server)!)
        request.httpMethod = "POST"
        request.httpBody = body_str
        request.timeoutInterval = 120
        let session = URLSession.shared
        let task = session.dataTask(with: request) {(data, response, error) in
            if error != nil{
                print("Error when connecting to server.")
                print(error!)
            }else{
                let result = try! JSON(data: data!)
                completion(result["status"].intValue)
            }
        }as URLSessionTask
        task.resume()
    }
    
    public static func removeFromServer(uuid: UUID, completion: @escaping (Bool) -> Void){
        let body: [String: Any] = ["uuid": uuid.uuidString]
        let body_str = try? JSONSerialization.data(withJSONObject: body)
        let server = Config.host + "/calendar/remove"
        var request = URLRequest(url: URL(string: server)!)
        request.httpMethod = "POST"
        request.httpBody = body_str
        request.timeoutInterval = 120
        let session = URLSession.shared
        let task = session.dataTask(with: request) {(data, response, error) in
            if error != nil{
                print("Error when connecting to server.")
                print(error!)
            }else{
                let result = try! JSON(data: data!)
                if(result["status"] == 1){
                    completion(true)
                }
                else{
                    completion(false)
                }
            }
        }as URLSessionTask
        task.resume()
    }
    
    public static func getFromServer(completion: @escaping (Bool, [Schedule]?) -> Void){
        let server = Config.host + "/calendar/fetch"
        var request = URLRequest(url: URL(string: server)!)
        request.httpMethod = "GET"
        request.timeoutInterval = 120
        let session = URLSession.shared
        let task = session.dataTask(with: request) {(data, response, error) in
            if error != nil{
                print("Error when connecting to server.")
                print(error!)
            }else{
                let result = try! JSON(data: data!)
                if(result["status"] == 1){
                    var schedule_list: [Schedule] = []
                    for(_, item): (String, JSON) in result["schedule"] {
                        let uuid_str = item["uuid"].stringValue
                        let name = item["name"].stringValue
                        let category_id = item["category"]["id"].intValue
                        let category_name = item["category"]["name"].stringValue
                        let start = item["start"].floatValue
                        let end = item["end"].floatValue
                        let uuid = UUID(uuidString: uuid_str)
                        if(uuid != nil){
                            let schedule_obj = Schedule(id: uuid!, name: name, categoryId: category_id, categoryName: category_name, start: Date(timeIntervalSince1970: TimeInterval(start)), end: Date(timeIntervalSince1970: TimeInterval(end)))
                            schedule_list.append(schedule_obj)
                        }
                        else{
                            print("Error when phasing uuid of schedule \(name)!")
                        }
                    }
                    completion(true, schedule_list)
                }
                else{
                    completion(false, nil)
                }
            }
        }as URLSessionTask
        task.resume()
    }
    
    public static func getCategory(completion: @escaping (Bool, [(id: Int, name: String)]) -> Void){
        let server = Config.host + "/calendar/category"
        var request = URLRequest(url: URL(string: server)!)
        request.httpMethod = "GET"
        request.timeoutInterval = 120
        let session = URLSession.shared
        let task = session.dataTask(with: request) {(data, response, error) in
            if error != nil{
                print("Error when connecting to server.")
                print(error!)
            }else{
                let result = try! JSON(data: data!)
                if(result["status"] == 1){
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
        }as URLSessionTask
        task.resume()
    }
}
