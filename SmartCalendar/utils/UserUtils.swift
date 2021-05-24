//
//  UserUtils.swift
//  SmartCalendar
//

import Foundation

class UserUtils{
    public static func login(username: String, password: String, completion: @escaping (Bool) -> Void){
        var connected = false
        let server = "http://mc.mcgo.pw:40018/login"
        let url_str = server + "?username=" + username + "&password=" + password
        var request = URLRequest(url: URL(string: url_str)!)
        request.httpMethod = "GET"
        request.timeoutInterval = 120
        let session = URLSession.shared
        let task = session.dataTask(with: request) {(data, response, error) in
            if error != nil{
                print("Failed to connect to server.")
            }else{
                let json = try!JSON(data:data!)
                print(json["status"])
                if(json["status"] == "success"){
                    connected = true
                }
            }
            completion(connected)
        }as URLSessionTask
        task.resume()
        print(connected)
    }
    
    public static func register(username: String, password: String, completion: @escaping (Bool) -> Void){
        var connected = false
        let json: [String: Any] = ["username":username,"password":password]
        let jsonData = try? JSONSerialization.data(withJSONObject: json)
        let server = "http://mc.mcgo.pw:40018/register"
        var request = URLRequest(url:URL(string:server)!)
        request.httpMethod = "POST"
        request.httpBody = jsonData
        let session = URLSession.shared
        let task = session.dataTask(with: request) {(data, response, error) in
            do {
                if error != nil{
                    print("Failed to connect to server.")
                }else{
                    let json = try!JSON(data:data!)
                    if(json["status"]=="success"){
                        connected = true
                    }
                }
                completion(connected)
            }
        }
        task.resume()
    }
}
