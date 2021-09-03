//
//  DynamicSchedule.swift
//  SmartCalendar
//

import Foundation

struct DynamicSchedule{
    var name: String
    var category: (id: Int, name: String)
    var duration: Int /* In hour */
    var from: Date
    var to: Date
}
