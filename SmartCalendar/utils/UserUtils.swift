//
//  UserUtils.swift
//  SmartCalendar
//

import Foundation

class UserUtils{
    public func login(server: String, username: String, password: String) -> Bool{
        var connected = false
        var request = URLRequest(url: URL(string: server)!)
        request.httpMethod = "GET"
        let httpArg = "?username="+username+"&password="+password
        request.httpBody = httpArg.data(using:.utf8)
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
            }
        }
        task.resume()
        return connected
    }
}
