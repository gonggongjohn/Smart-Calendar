//
//  GeoUtils.swift
//  SmartCalendar
//

import Foundation
import CoreLocation

class GeoUtils: NSObject, CLLocationManagerDelegate{
    private var lmInstance: CLLocationManager
    private var geoHistory: GeoHistory
    
    override init() {
        self.geoHistory = GeoHistory()
        self.lmInstance = CLLocationManager()
    }
    //后续可以使用SignificantLocationChanges节省电量
    func initGeoTrace(){
        self.lmInstance.delegate = self
        self.lmInstance.requestAlwaysAuthorization()
        self.lmInstance.desiredAccuracy = kCLLocationAccuracyBest
        self.lmInstance.distanceFilter = 50
    }
    
    func startTrace(){
        let geoTmp = getHistory()
        if geoTmp != nil {
            self.geoHistory = geoTmp!
        }
        self.lmInstance.startUpdatingLocation()
        print("Start Tracing!")
    }
    
    func stopTrace(){
        self.lmInstance.stopUpdatingLocation()
        saveHistory(history: self.geoHistory)
        print("Stop Tracing!")
    }
    
    //覆写更新通知代理方法
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations.last!
        let geoEncoder = CLGeocoder()
        geoEncoder.reverseGeocodeLocation(location, completionHandler: {
            (placemarks: [CLPlacemark]?, err: Error?) -> Void in
            if err != nil && placemarks == nil{
                print(err!)
            }
            else{
                for placemark in placemarks!{
                    self.geoHistory.appendItem(geo: location, name: placemark.name!)
                }
            }
        })
        print("Location Updated!")
    }
    
    public func saveHistory(history: GeoHistory){
        do {
            let data = try NSKeyedArchiver.archivedData(withRootObject: history, requiringSecureCoding: true)
            UserDefaults.standard.setValue(data, forKey: "GeoHistory")
        }
        catch{
            print("Error occurred when saving geo data!")
        }
    }
    
    public func getHistory() -> GeoHistory?{
        let data = UserDefaults.standard.data(forKey: "GeoHistory")
        var history: GeoHistory? = nil
        if(data != nil){
            do{
                history = try NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(data!) as? GeoHistory
            }
            catch{
                print("Error occurred when getting geo data!")
            }
        }
        return history
    }
}
