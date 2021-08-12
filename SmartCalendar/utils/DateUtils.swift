//
//  TimeHelper.swift
//  SmartCalendar
//

import Foundation

class DateUtils{
    public static func getDate(time: Date) -> Date {
        let calendar = Calendar.current
        var component = calendar.dateComponents([Calendar.Component.year, Calendar.Component.month, Calendar.Component.day], from: time)
        component.hour = 0
        component.minute = 0
        component.second = 0
        return calendar.date(from: component)!
    }
    
    public static func getDate(year: Int, month: Int, day: Int) -> Date {
        let calendar = Calendar.current
        var component = DateComponents()
        component.year = year
        component.month = month
        component.day = day
        component.hour = 0
        component.minute = 0
        component.second = 0
        return calendar.date(from: component)!
    }
    
    public static func getTime(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int) -> Date {
        let calendar = Calendar.current
        var component = DateComponents()
        component.year = year
        component.month = month
        component.day = day
        component.hour = hour
        component.minute = minute
        component.second = second
        return calendar.date(from: component)!
    }
    
    public static func getSequence(fromDate: Date, toDate: Date) -> [Date]{
        let calendar = Calendar.current
        var curDate = fromDate
        var dateList: [Date] = []
        while curDate != toDate {
            dateList.append(curDate)
            curDate = calendar.date(byAdding: Calendar.Component.day, value: 1, to: curDate)!
        }
        return dateList
    }
    
    public static func getDateString(date: Date) -> String{
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        return formatter.string(from: date)
    }
    
    public static func getNextTimeFromNow(day: Int, delta: Int) -> Date{
        let now = Date()
        let calendar = Calendar.current
        let curWeekDay = calendar.dateComponents([.weekday], from: now).weekday!
        var components = calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: now)
        var startThisWeek = calendar.date(from: components)!
        var startnextSchedule = Date()
        if curWeekDay <= day {
            startnextSchedule = calendar.date(byAdding: Calendar.Component.day, value: day, to: startThisWeek)!
        }
        else {
            startnextSchedule = calendar.date(byAdding: Calendar.Component.day, value: 7 + day, to: startThisWeek)!
        }
        var nextSchedule = calendar.date(byAdding: Calendar.Component.second, value: delta, to: startnextSchedule)!
        return nextSchedule
    }
    
    public static func getTimeSeq(from: Date, to: Date, day: Int, delta: Int) -> [Date]{
        var seq: [Date] = []
        let calendar = Calendar.current
        let curWeekDay = calendar.dateComponents([.weekday], from: from).weekday!
        var components = calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: from)
        var startThisWeek = calendar.date(from: components)!
        var startnextSchedule = Date()
        if curWeekDay <= day {
            startnextSchedule = calendar.date(byAdding: Calendar.Component.day, value: day, to: startThisWeek)!
        }
        else {
            startnextSchedule = calendar.date(byAdding: Calendar.Component.day, value: 7 + day, to: startThisWeek)!
        }
        var nextSchedule = calendar.date(byAdding: Calendar.Component.second, value: delta, to: startnextSchedule)!
        var cur = nextSchedule
        while cur.timeIntervalSince1970 <= to.timeIntervalSince1970 {
            seq.append(cur)
            cur = calendar.date(byAdding: Calendar.Component.day, value: 7, to: cur)!
        }
        return seq
    }
    
    public static func getDayNextWeek(from: Date) -> Date{
        let calendar = Calendar.current
        var target = calendar.date(byAdding: Calendar.Component.day, value: 7, to: from)!
        return target
    }
    
    public static func isDateBetween(target: Date, from: Date, to: Date) -> Bool {
        if target.timeIntervalSince1970 >= from.timeIntervalSince1970 && target.timeIntervalSince1970 <= to.timeIntervalSince1970 {
            return true
        }
        else{
            return false
        }
    }
    
    public static func date2str(date: Date) -> String{
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
        return dateFormatter.string(from: date)
    }
    
    public static func time2str(dateDelta: Int) -> String{
        let hour = dateDelta / 3600
        let minute = (dateDelta - hour * 3600) / 60
        let second = dateDelta - hour * 3600 - minute * 60
        return String(format: "%02d:%02d:%02d", hour ,minute, second)
    }
}
