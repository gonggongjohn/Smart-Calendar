//
//  IdNameRow.swift
//  SmartCalendar
//

import Foundation

struct IdNameRow: Identifiable{
    let id = UUID()
    let contentId: Int
    let conentName: String
}
