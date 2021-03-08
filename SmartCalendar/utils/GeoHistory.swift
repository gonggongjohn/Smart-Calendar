//
//  GeoHistory.swift
//  SmartCalendar
//

import Foundation
import CoreLocation

class GeoHistory: NSObject, NSSecureCoding{
    static var supportsSecureCoding: Bool {
        return true
    }
    
    var geoList: [(geo: CLLocation, name: String)]
    
    override init() {
        self.geoList = []
    }
    
    func encode(with coder: NSCoder) {
        var geoComp: [CLLocation] = []
        var nameComp: [String] = []
        for item in geoList{
            geoComp.append(item.geo)
            nameComp.append(item.name)
        }
        coder.encode(geoComp, forKey: "geoList_geo")
        coder.encode(nameComp, forKey: "geoList_name")
    }
    
    required init?(coder: NSCoder) {
        self.geoList = []
        let geoComp: [CLLocation]? = coder.decodeObject(forKey: "geoList_geo") as? [CLLocation]
        let nameComp: [String]? = coder.decodeObject(forKey: "geoList_name") as? [String]
        if geoComp != nil && nameComp != nil && geoComp!.count > 0 && geoComp!.count > 0{
            for i in 0...geoComp!.count - 1{
                self.geoList.append((geoComp![i], nameComp![i]))
            }
        }
    }
}
