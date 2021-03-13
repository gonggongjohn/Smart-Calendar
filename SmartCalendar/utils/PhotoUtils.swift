//
//  PhotoUtils.swift
//  SmartCalendar
//

import Foundation
import Photos
import CoreLocation

class PhotoUtils{
    
    func getLocations() -> [CLLocation] {
        let options = PHFetchOptions()
        var dComponents = DateComponents()
        dComponents.year = 2021
        dComponents.month = 1
        dComponents.day = 1
        dComponents.hour = 0
        dComponents.minute = 0
        dComponents.second = 0
        let date = Calendar.current.date(from: dComponents)
        options.predicate = NSPredicate(format: "creationDate >= %@", argumentArray: [date!])
        options.sortDescriptors = [NSSortDescriptor(key: "creationDate", ascending: true)]
        let assetList = PHAsset.fetchAssets(with: options)
        print("Fetch successful!")
        var locations: [CLLocation] = []
        for i in 0 ..< assetList.count {
            let item = assetList.object(at: i)
            let location: CLLocation? = item.location
            if location != nil {
                locations.append(location!)
            }
        }
        return locations
    }
}
