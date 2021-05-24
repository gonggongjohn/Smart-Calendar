//
//  HistoryDateWrapper.swift
//  SmartCalendar
//

import Foundation

class HistoryDateWrapper: NSObject, ObservableObject {
    @Published var dateDict = [Date: GeoHistory]()
    @Published var totalNum = 0
    @Published var phasedNum = 0
}
