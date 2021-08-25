//
//  ScheduleContainer.swift
//  SmartCalendar
//

import Foundation

class ScheduleContainer: NSObject, NSSecureCoding {
    public var schedules: [UUID: Schedule]
    
    static var supportsSecureCoding: Bool {
        return true
    }
    
    required init?(coder: NSCoder) {
        self.schedules = [:]
        let schedules_decoded = coder.decodeObject(forKey: "schedule_list") as? [Schedule]
        if(schedules_decoded != nil){
            for schedule in schedules_decoded!{
                if(schedule.deleted && schedule.dirty == 0){
                    continue
                }
                else{
                    self.schedules[schedule.id] = schedule
                }
            }
        }
        else{
            print("Error occurred when decoding schedule list from binary!")
        }
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(Array(schedules.values), forKey: "schedule_list")
    }
    
    override init() {
        self.schedules = [:]
    }
    
    /* Should mark dirty manually */
    public func append(_ schedule: Schedule){
        self.schedules[schedule.id] = schedule
    }
    
    public func append(contentsOf: [Schedule]){
        for schedule in contentsOf {
            self.schedules[schedule.id] = schedule
        }
    }
    
    public func remove(schedule: Schedule){
        self.schedules[schedule.id]?.deleted = true
        self.schedules[schedule.id]?.dirty = 2
    }
    
    public func markDirty(schedule: Schedule, type: Int){
        self.schedules[schedule.id]?.dirty = type
    }
    
    public func eraseDirty(schedule: Schedule){
        self.schedules[schedule.id]?.dirty = 0
    }
}
