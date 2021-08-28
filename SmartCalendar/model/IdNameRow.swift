//
//  IdNameRow.swift
//  SmartCalendar
//

import Foundation

class IdNameRow: NSObject, Identifiable, NSSecureCoding{
    let id = UUID()
    let contentId: Int
    let contentName: String
    
    static var supportsSecureCoding: Bool {
        return true
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(contentId, forKey: "id")
        coder.encode(contentName, forKey: "name")
    }
    
    required init?(coder: NSCoder) {
        let id_decoded = coder.decodeInteger(forKey: "id")
        let name_decoded = coder.decodeObject(forKey: "name") as? String
        self.contentId = id_decoded
        self.contentName = name_decoded ?? ""
    }
    
    init(id: Int, name: String) {
        self.contentId = id
        self.contentName = name
    }
}
