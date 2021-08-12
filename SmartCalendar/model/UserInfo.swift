//
//  UserInfo.swift
//  SmartCalendar
//

import Foundation

class UserInfo: NSObject, NSSecureCoding {
    static var supportsSecureCoding: Bool {
        return true
    }
    
    private var username: String
    private var password: String
    
    required init?(coder: NSCoder) {
        self.username = coder.decodeObject(forKey: "username") as? String ?? ""
        self.password = coder.decodeObject(forKey: "password") as? String ?? ""
    }
    
    init(username: String, password: String) {
        self.username = username
        self.password = password
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(username, forKey: "username")
        coder.encode(password, forKey: "password")
    }
    
    public func getUsername() -> String{
        return self.username
    }
    
    public func getPassword() -> String{
        return self.password
    }
}
