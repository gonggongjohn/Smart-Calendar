//
//  PhotoUtils.swift
//  SmartCalendar
//

import Foundation
import Photos
import CoreLocation

class PhotoUtils{
    
    public func getMetaData() -> PHFetchResult<PHAsset> {
        PHPhotoLibrary.requestAuthorization { (status) in
            if status != .authorized {
                print("No permission!")
            }
        }
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
        return assetList
    }
    
    public func getPhotoGeo(assets: PHFetchResult<PHAsset>) -> [CLLocation] {
        var locations: [CLLocation] = []
        for i in 0 ..< assets.count {
            let item = assets.object(at: i)
            let location: CLLocation? = item.location
            if location != nil {
                locations.append(location!)
            }
        }
        return locations
    }
}
