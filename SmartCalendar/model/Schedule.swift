//
//  Schedule.swift
//  SmartCalendar
//

import Foundation

class Schedule: NSObject, Identifiable, NSSecureCoding {
    var id: UUID
    var dirty: Int /* 0 - Unchanged; 1 - New Schedule; 2 - Removed Schedule */
    var deleted: Bool
    var name: String
    var category: (id: Int, name: String)
    var start: Date
    var end: Date
    var position: GeoPoint?
    
    static var supportsSecureCoding: Bool {
        return true
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(id.uuidString, forKey: "id")
        coder.encode(dirty, forKey: "dirty")
        coder.encode(deleted, forKey: "deleted")
        coder.encode(name, forKey: "name")
        coder.encode(category.id, forKey: "category_id")
        coder.encode(category.name, forKey: "category_name")
        coder.encode(start, forKey: "start")
        coder.encode(end, forKey: "end")
        if(self.position != nil){
            coder.encode(position, forKey: "position")
        }
    }
    
    required init?(coder: NSCoder) {
        let id_decoded = coder.decodeObject(forKey: "id") as? String
        let dirty_decoded = coder.decodeInteger(forKey: "dirty")
        let deleted_decoded = coder.decodeBool(forKey: "deleted")
        let name_decoded = coder.decodeObject(forKey: "name") as? String
        let catId_decoded = coder.decodeInteger(forKey: "category_id")
        let catName_decoded = coder.decodeObject(forKey: "category_name") as? String
        let start_decoded = coder.decodeObject(forKey: "start") as? Date
        let end_decoded = coder.decodeObject(forKey: "end") as? Date
        let position_decoded = coder.decodeObject(forKey: "position") as? GeoPoint
        if(id_decoded != nil && name_decoded != nil && catName_decoded != nil && start_decoded != nil && end_decoded != nil){
            let id_tmp = UUID(uuidString: id_decoded!)
            if(id_tmp != nil){
                self.id = id_tmp!
            }
            else{
                self.id = UUID()
                print("Error when phasing UUID of a local schedule!")
            }
            self.dirty = dirty_decoded
            self.deleted = deleted_decoded
            self.name = name_decoded!
            self.category = (id: catId_decoded, name: catName_decoded!)
            self.start = start_decoded!
            self.end = end_decoded!
            if(position_decoded != nil){
                self.position = position_decoded
            }
        }
        else{
            /* Need to contemplate on error handling later */
            self.id = UUID()
            self.dirty = 0
            self.deleted = false
            self.name = ""
            self.category = (id: 0, name: "")
            self.start = Date()
            self.end = Date()
            print("Error when decoding schedule structure from binary!")
        }
    }
    
    init(id: UUID, dirty: Int, name: String, categoryId: Int, categoryName: String, start: Date, end: Date, pos: GeoPoint?) {
        self.id = id
        self.name = name
        self.category = (id: categoryId, name: categoryName)
        self.start = start
        self.end = end
        self.dirty = dirty
        self.deleted = false
        self.position = pos
    }
    
    init(name: String, categoryId: Int, categoryName: String, start: Date, end: Date, pos: GeoPoint?) {
        self.id = UUID()
        self.name = name
        self.category = (id: categoryId, name: categoryName)
        self.start = start
        self.end = end
        self.dirty = 1
        self.deleted = false
        self.position = pos
    }
}
