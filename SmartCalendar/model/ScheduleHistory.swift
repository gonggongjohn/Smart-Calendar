//
//  ScheduleHistory.swift
//  SmartCalendar
//

import Foundation

class ScheduleHistory: NSObject, NSSecureCoding {
    static var supportsSecureCoding: Bool {
        return true
    }
    
    private var nameList: [String] = []
    private var categoryList: [String] = []
    private var startList: [Date] = []
    private var endList: [Date] = []
    
    required init?(coder: NSCoder) {
        let names: [String]? = coder.decodeObject(forKey: "schedule_history_name") as? [String]
        let cats: [String]? = coder.decodeObject(forKey: "schedule_history_cat") as? [String]
        let starts: [Date]? = coder.decodeObject(forKey: "schedule_history_start") as? [Date]
        let ends: [Date]? = coder.decodeObject(forKey: "schedule_history_end") as? [Date]
        if names != nil && cats != nil && starts != nil && ends != nil {
            for i in 0 ..< names!.count {
                self.nameList.append(names![i])
                self.categoryList.append(cats![i])
                self.startList.append(starts![i])
                self.endList.append(ends![i])
            }
        }
    }
    
    override init(){
        
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(self.nameList, forKey: "schedule_history_name")
        coder.encode(self.categoryList, forKey: "schedule_history_cat")
        coder.encode(self.startList, forKey: "schedule_history_start")
        coder.encode(self.endList, forKey: "schedule_history_end")
    }
    
    public func addSchedule(name: String, category: String, start: Date, end: Date){
        self.nameList.append(name)
        self.categoryList.append(category)
        self.startList.append(start)
        self.endList.append(end)
    }
    
    public func addSchedules(origin: ScheduleHistory){
        self.nameList.append(contentsOf: origin.getNameList())
        self.categoryList.append(contentsOf: origin.getCategoryList())
        self.startList.append(contentsOf: origin.getStartList())
        self.endList.append(contentsOf: origin.getEndList())
    }
    
    public func removeSchedule(indices: IndexSet){
        self.nameList.remove(atOffsets: indices)
        self.categoryList.remove(atOffsets: indices)
        self.startList.remove(atOffsets: indices)
        self.endList.remove(atOffsets: indices)
    }
    
    public func getSchedules() -> [(name: String, category: String, start: Date, end: Date)] {
        var result: [(name: String, category: String, start: Date, end: Date)] = []
        for i in 0 ..< self.nameList.count {
            result.append((nameList[i], categoryList[i], startList[i], endList[i]))
        }
        return result
    }
    
    public func getNameList() -> [String]{
        return self.nameList
    }
    
    public func getCategoryList() -> [String]{
        return self.categoryList
    }
    
    public func getStartList() -> [Date]{
        return self.startList
    }
    
    public func getEndList() -> [Date]{
        return self.endList
    }
}
