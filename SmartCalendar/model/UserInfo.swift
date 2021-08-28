//
//  UserInfo.swift
//  SmartCalendar
//

import Foundation

class UserInfo: NSObject, NSSecureCoding, ObservableObject {
    static var supportsSecureCoding: Bool {
        return true
    }
    
    private var username: String
    private var password: String
    @Published var nickname: String?
    @Published var occupation: IdNameRow?
    @Published var major: IdNameRow?
    @Published var school: IdNameRow?
    
    required init?(coder: NSCoder) {
        self.username = coder.decodeObject(forKey: "username") as? String ?? ""
        self.password = coder.decodeObject(forKey: "password") as? String ?? ""
        self.nickname = coder.decodeObject(forKey: "nickname") as? String
        self.occupation = coder.decodeObject(forKey: "occupation") as? IdNameRow
        self.major = coder.decodeObject(forKey: "major") as? IdNameRow
        self.school = coder.decodeObject(forKey: "school") as? IdNameRow
    }
    
    init(username: String, password: String) {
        self.username = username
        self.password = password
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(username, forKey: "username")
        coder.encode(password, forKey: "password")
        if(nickname != nil){
            coder.encode(nickname, forKey: "nickname")
        }
        if(occupation != nil){
            coder.encode(occupation, forKey: "occupation")
        }
        if(major != nil){
            coder.encode(major, forKey: "major")
        }
        if(school != nil){
            coder.encode(school, forKey: "school")
        }
    }
    
    public func getUsername() -> String{
        return self.username
    }
    
    public func getPassword() -> String{
        return self.password
    }
}
