//
//  GeoHistory.swift
//  SmartCalendar
//

import Foundation
import CoreLocation

class GeoHistory: NSObject, NSSecureCoding{
    private var geoList: [(geo: CLLocation, name: String)]
    
    static var supportsSecureCoding: Bool {
        return true
    }
    
    override init() {
        self.geoList = []
    }
    
    public func appendItem(geo: CLLocation, name: String){
        self.geoList.append((geo, name))
    }
    
    public func appendLocations(locations: [CLLocation]){
        self.geoList = []
        let geoEncoder = CLGeocoder()
        for location in locations{
            geoEncoder.reverseGeocodeLocation(location, completionHandler: {
                (placemarks: [CLPlacemark]?, err: Error?) -> Void in
                if err != nil && placemarks == nil{
                    print(err!)
                }
                else{
                    for placemark in placemarks!{
                        self.geoList.append((location, placemark.name!))
                    }
                }
            })
        }
    }
    
    public func getHistory() -> [(geo: CLLocation, name: String)]{
        return self.geoList
    }
    
    func encode(with coder: NSCoder) {
        var geoComp: [CLLocation] = []
        var nameComp: [String] = []
        for item in self.geoList{
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
