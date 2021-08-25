//
//  UserUtils.swift
//  SmartCalendar
//

import Foundation
import Alamofire

class UserUtils{
    public static func login(username: String, password: String, completion: @escaping (Int) -> Void){
        let server = Config.host + "/user/login"
        let body: [String: Any] = ["username": username, "password": password]
        AF.request(server, method: .post, parameters: body, encoding: JSONEncoding.default, requestModifier: { $0.timeoutInterval = 20}).validate().responseJSON { response in
            if(response.error == nil){
                let result_dict = response.value as? Dictionary<String, Int>
                if(result_dict != nil && result_dict!["status"] != nil){
                    completion(result_dict!["status"]!)
                }
                else{
                    print("Error occurred when phasing response json!")
                    completion(404)
                }
            }
            else{
                print(response.error!)
                print("Error occurred when requesting server!")
                completion(404)
            }
        }
    }
    
    public static func register(username: String, password: String, phone: String, completion: @escaping (Bool) -> Void){
        let body: [String: Any] = ["username": username, "password": password, "phone": phone]
        let body_str = try? JSONSerialization.data(withJSONObject: body)
        let server = Config.host + "/user/register"
        var request = URLRequest(url: URL(string: server)!)
        request.httpMethod = "POST"
        request.httpBody = body_str
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
        } as URLSessionTask
        task.resume()
    }
    
    public static func getInfo(completion: @escaping (Bool, [String: String]?) -> Void){
        let server = Config.host + "/user/info"
        var request = URLRequest(url: URL(string: server)!)
        request.httpMethod = "GET"
        let session = URLSession.shared
        let task = session.dataTask(with: request) {(data, response, error) in
            if error != nil{
                print("Error when connecting to server.")
                print(error!)
            }else{
                let result = try! JSON(data: data!)
                if(result["status"] == 1){
                    let username = result["info"]["username"].stringValue
                    let nickname = result["info"]["nickname"].stringValue
                    let info_dict: [String: String] = ["username": username, "nickname": nickname]
                    completion(true, info_dict)
                }
                else{
                    completion(false, nil)
                }
            }
        } as URLSessionTask
        task.resume()
    }
    
    public static func getOccupation(completion: @escaping (Bool, [(id: Int, name: String)]) -> Void){
        let server = Config.host + "/user/occupation"
        AF.request(server, method: .get, requestModifier: { $0.timeoutInterval = 20}).validate().responseJSON { response in
            if(response.error == nil && response.value != nil){
                let result = JSON(response.value!)
                if(result["status"].intValue == 1){
                    var occupation_list: [(id: Int, name: String)] = []
                    for(_, item): (String, JSON) in result["occupation"] {
                        let id = item["id"].intValue
                        let name = item["name"].stringValue
                        occupation_list.append((id: id, name: name))
                    }
                    completion(true, occupation_list)
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
    
    public static func getMajor(completion: @escaping (Bool, [(id: Int, name: String)]) -> Void){
        let server = Config.host + "/user/major"
        AF.request(server, method: .get, requestModifier: { $0.timeoutInterval = 20}).validate().responseJSON { response in
            if(response.error == nil && response.value != nil){
                let result = JSON(response.value!)
                if(result["status"].intValue == 1){
                    var major_list: [(id: Int, name: String)] = []
                    for(_, item): (String, JSON) in result["major"] {
                        let id = item["id"].intValue
                        let name = item["name"].stringValue
                        major_list.append((id: id, name: name))
                    }
                    completion(true, major_list)
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
    
    public static func getSchool(completion: @escaping (Bool, [(id: Int, name: String)]) -> Void){
        let server = Config.host + "/user/school"
        AF.request(server, method: .get, requestModifier: { $0.timeoutInterval = 20}).validate().responseJSON { response in
            if(response.error == nil && response.value != nil){
                let result = JSON(response.value!)
                if(result["status"].intValue == 1){
                    var school_list: [(id: Int, name: String)] = []
                    for(_, item): (String, JSON) in result["school"] {
                        let id = item["id"].intValue
                        let name = item["name"].stringValue
                        school_list.append((id: id, name: name))
                    }
                    completion(true, school_list)
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
}
