//
//  Question.swift
//  SmartCalendar
//

import Foundation

struct Question: Identifiable{
    let id = UUID()
    let questionId: Int
    let description: String
    let options: [QuestionOption]
}

struct QuestionOption: Identifiable{
    let id = UUID()
    let optionId: Int
    let description: String
    let score: Int
    var chosen: Bool
    
    init(optionId: Int, description: String, score: Int) {
        self.optionId = optionId
        self.description = description
        self.score = score
        self.chosen = false
    }
}
