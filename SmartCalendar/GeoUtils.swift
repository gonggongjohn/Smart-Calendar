//
//  GeoUtils.swift
//  SmartCalendar
//

import Foundation
import CoreLocation

class GeoUtils: NSObject, CLLocationManagerDelegate{
    private var lmInstance: CLLocationManager
    private var historyDict: [Date: GeoHistory] //Date (00:00) -> All the locations in that day
    
    override init() {
        self.historyDict = [Date: GeoHistory]()
        self.lmInstance = CLLocationManager()
    }
    //后续可以使用SignificantLocationChanges节省电量
    func initGeoTrace(){
        self.lmInstance.delegate = self
        self.lmInstance.requestAlwaysAuthorization()
        self.lmInstance.desiredAccuracy = kCLLocationAccuracyBest
        self.lmInstance.distanceFilter = 100
    }
    
    func startTrace(){
        self.lmInstance.startUpdatingLocation();
        print("Start Tracing!")
    }
    
    func stopTrace(){
        self.lmInstance.stopUpdatingLocation()
        let profileUtils = StorageUtils()
        for (date, history) in historyDict {
            profileUtils.saveGeoHistory(date: date, history: history)
        }
        print("Stop Tracing!")
    }
    
    //覆写更新通知代理方法
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations.last!
        let date = DateUtils.getDate(time: location.timestamp)
        if self.historyDict[date] == nil{
            self.historyDict[date] = GeoHistory()
        }
        self.historyDict[date]!.appendLocation(geo: location)
        print("Location Updated!")
    }
    
    /* 坐标转换 标准坐标系-> 中国坐标系
              WGS-84 --> GCJ-02
        wgsLocation: 标准坐标 */
    public func transformWGSToGCJ(wgsLocation:CLLocationCoordinate2D)->CLLocationCoordinate2D
    {
        let pi = Double.pi
        let a: Double = 6378245.0
        let e: Double = 0.00669342162296594323
        
        var adjustLocation  = CLLocationCoordinate2D()
        var adjustLatitude  = transformLatitudeWith(x: wgsLocation.longitude - 105.0, y:wgsLocation.latitude - 35.0);
        var adjustLongitude = transformLongitudeWith(x: wgsLocation.longitude - 105.0, y:wgsLocation.latitude - 35.0);
        let radLatitude     = wgsLocation.latitude / 180.0 * pi;
        var magic           = sin(radLatitude);
        magic               = 1 - e * magic * magic;
        let sqrtMagic       = sqrt(magic);
        adjustLatitude      = (adjustLatitude * 180.0) / ((a * (1 - e)) / (magic * sqrtMagic) * pi);
        adjustLongitude     = (adjustLongitude * 180.0) / (a / sqrtMagic * cos(radLatitude) * pi);
        
        adjustLocation.latitude     = wgsLocation.latitude + adjustLatitude;
        adjustLocation.longitude    = wgsLocation.longitude + adjustLongitude;
        return adjustLocation;
    }
    
    // 纬度转换
    public func transformLatitudeWith(x: Double, y: Double ) -> Double {
        let pi = Double.pi
        
        var lat = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y ;
        lat += 0.2 * sqrt(fabs(x));
        
        lat += (20.0 * sin(6.0 * x * pi)) * 2.0 / 3.0;
        lat += (20.0 * sin(2.0 * x * pi)) * 2.0 / 3.0;
        lat += (20.0 * sin(y * pi)) * 2.0 / 3.0;
        lat += (40.0 * sin(y / 3.0 * pi)) * 2.0 / 3.0;
        lat += (160.0 * sin(y / 12.0 * pi)) * 2.0 / 3.0;
        lat += (320 * sin(y * pi / 30.0)) * 2.0 / 3.0;
        return lat;
    }
    
    // 经度转换
    public func transformLongitudeWith(x: Double, y: Double )-> Double {
        let pi = Double.pi
        
        var lon = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y ;
        lon +=  0.1 * sqrt(fabs(x));
        lon += (20.0 * sin(6.0 * x * pi)) * 2.0 / 3.0;
        lon += (20.0 * sin(2.0 * x * pi)) * 2.0 / 3.0;
        lon += (20.0 * sin(x * pi)) * 2.0 / 3.0;
        lon += (40.0 * sin(x / 3.0 * pi)) * 2.0 / 3.0;
        lon += (150.0 * sin(x / 12.0 * pi)) * 2.0 / 3.0;
        lon += (300.0 * sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return lon;
    }
}
