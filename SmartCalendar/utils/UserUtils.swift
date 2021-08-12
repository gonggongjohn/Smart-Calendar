//
//  UserUtils.swift
//  SmartCalendar
//

import Foundation

class UserUtils{
    public static func login(username: String, password: String, completion: @escaping (Int) -> Void){
        let body: [String: Any] = ["username": username, "password": password]
        let body_str = try? JSONSerialization.data(withJSONObject: body)
        let server = Config.host + "/user/login"
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
}
