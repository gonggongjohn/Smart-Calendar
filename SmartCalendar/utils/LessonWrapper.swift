//
//  LessonWrapper.swift
//  SmartCalendar
//

import Foundation

class LessonWrapper: NSObject {
    private var timeList: [(Int, Int, Int)] = [] //Day, DeltaStart(FromDayBeginning), DeltaEnd(FromDayBeginning)
    private var lessonList: [String] = []
    
    public func appendLesson(day: Int, deltaStart: Int, deltaEnd: Int, name: String){
        self.timeList.append((day, deltaStart, deltaEnd))
        self.lessonList.append(name)
    }
    
    public func getLessonIndices() -> [(Int, Int, Int)] {
        return self.timeList
    }
    
    public func getLesson(index: (Int, Int, Int)) -> String{
        var lesson = ""
        for i in 0 ..< self.timeList.count {
            if self.timeList[i] == index {
                lesson = self.lessonList[i]
                break
            }
        }
        return lesson
    }
}
