//
//  GeoPoint.swift
//  SmartCalendar
//

import Foundation
import MapKit

class GeoPoint: NSObject, Identifiable, NSSecureCoding{
    let id = UUID()
    var name: String
    var coordinate: CLLocationCoordinate2D
    
    static var supportsSecureCoding: Bool {
        return true
    }
    
    func encode(with coder: NSCoder) {
        coder.encode(name, forKey: "name")
        coder.encode(Double(coordinate.latitude), forKey: "latitude")
        coder.encode(Double(coordinate.longitude), forKey: "longitude")
    }
    
    required init?(coder: NSCoder) {
        let name_decoded = coder.decodeObject(forKey: "name") as? String
        let latitude_decoded = coder.decodeDouble(forKey: "latitude")
        let longitude_decoded = coder.decodeDouble(forKey: "longitude")
        if(name_decoded != nil){
            self.name = name_decoded!
            self.coordinate = CLLocationCoordinate2D(latitude: latitude_decoded, longitude: longitude_decoded)
        }
        else{
            self.name = ""
            self.coordinate = CLLocationCoordinate2D()
        }
    }
    
    init(name: String, latitude: Double, longitude: Double) {
        self.name = name
        self.coordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }
}
