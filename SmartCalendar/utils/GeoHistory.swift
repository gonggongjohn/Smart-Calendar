//
//  GeoHistory.swift
//  SmartCalendar
//

import Foundation
import CoreLocation

class GeoHistory: NSObject, NSSecureCoding, ObservableObject{
    static var supportsSecureCoding: Bool {
        return true
    }
    
    private var geoList: [(geo: CLLocation, name: String)]
    @Published var totLoc = 0
    @Published var phasedLoc = 0
    
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
    
    override init() {
        self.geoList = []
    }
    
    public func appendItem(geo: CLLocation, name: String){
        self.geoList.append((geo, name))
    }
    
    public func appendLocations(locations: [CLLocation]){
        self.geoList = []
        self.totLoc = locations.count
        for i in 0 ..< locations.count{
            DispatchQueue.main.asyncAfter(deadline: .now() + Double(2 * (i + 1))) {
                let geoEncoder = CLGeocoder()
                geoEncoder.reverseGeocodeLocation(locations[i], completionHandler: {
                    (placemarks: [CLPlacemark]?, err: Error?) -> Void in
                    if err != nil && placemarks == nil{
                        print(err!)
                    }
                    else{
                        for placemark in placemarks!{
                            self.geoList.append((locations[i], placemark.name!))
                        }
                        print("loc get!")
                        self.phasedLoc += 1
                    }
                })
            }
        }
    }
    
    public func getHistoryList() -> [(geo: CLLocation, name: String)]{
        return self.geoList
    }
    
}
