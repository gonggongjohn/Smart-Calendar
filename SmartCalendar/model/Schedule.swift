//
//  Schedule.swift
//  SmartCalendar
//

import Foundation

struct Schedule: Identifiable {
    var id: UUID
    var name: String
    var category: (id: Int, name: String)
    var start: Date
    var end: Date
    
    init(id: UUID, name: String, categoryId: Int, categoryName: String, start: Date, end: Date) {
        self.id = id
        self.name = name
        self.category = (id: categoryId, name: categoryName)
        self.start = start
        self.end = end
    }
    
    init(name: String, categoryId: Int, categoryName: String, start: Date, end: Date) {
        self.id = UUID()
        self.name = name
        self.category = (id: categoryId, name: categoryName)
        self.start = start
        self.end = end
    }
}
