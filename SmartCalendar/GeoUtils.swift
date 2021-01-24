//
//  GeoUtils.swift
//  SmartCalendar
//

import Foundation
import CoreLocation

class GeoUtils: NSObject, CLLocationManagerDelegate, ObservableObject{
    private var lmInstance: CLLocationManager?
    @Published var lastLoc = ""
    
    //后续可以使用SignificantLocationChanges节省电量
    func initGeoTrace(){
        if (self.lmInstance == nil) {
            self.lmInstance = CLLocationManager()
        }
        self.lmInstance?.delegate = self
        self.lmInstance?.requestAlwaysAuthorization()
        self.lmInstance?.desiredAccuracy = kCLLocationAccuracyBest
        self.lmInstance?.distanceFilter = 1
    }
    
    func startTrace(){
        self.lmInstance?.startUpdatingLocation()
        print("Start Tracing!")
    }
    
    func stopTrace(){
        self.lmInstance?.stopUpdatingLocation()
        print("Stop Tracing!")
    }
    
    //覆写更新通知代理方法
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations.last!.coordinate
        self.lastLoc = String(location.latitude) + "---" + String(location.longitude)
        print("Location Updated!")
        print(lastLoc)
    }
}
