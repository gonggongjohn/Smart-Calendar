//
//  Lesson.swift
//  SmartCalendar
//

import Foundation

struct Lesson: Identifiable {
    var id = UUID()
    var name: String
    var day: Int
    var startDelta: Int
    var endDelta: Int
}
