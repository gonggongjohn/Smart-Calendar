//
//  PhotoUtils.swift
//  SmartCalendar
//

import Foundation
import Photos
import CoreLocation
import Vision
import UIKit

class PhotoUtils: ObservableObject{
    private var sceneMap = [PHAsset: String]()
    @Published var sceneRecogTotal = 0
    @Published var sceneRecogCnt = 0
    
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
    
    public func phaseScene(assets: PHFetchResult<PHAsset>){
        let imageManager = PHImageManager.default()
        self.sceneRecogTotal = assets.count
        do{
            let sceneModel = try VNCoreMLModel(for: scene_1615560045().model)
            for i in 0 ..< assets.count {
                let item = assets.object(at: i)
                let cDate = item.creationDate
                let requestOption = PHImageRequestOptions()
                requestOption.isSynchronous = true
                requestOption.deliveryMode = .fastFormat
                imageManager.requestImage(for: item, targetSize: CGSize(width: 256, height: 256), contentMode: .aspectFit, options: requestOption, resultHandler: {
                    (result, _) -> Void in
                    if result != nil {
                        do{
                            let request = VNCoreMLRequest(model: sceneModel, completionHandler: {
                                (request, error) in
                                let results = request.results as? [VNClassificationObservation]
                                if results != nil{
                                    var maxConf: Float = 0.0
                                    var category = ""
                                    for classification in results! {
                                        if classification.confidence > maxConf {
                                            maxConf = classification.confidence
                                            category = classification.identifier
                                        }
                                    }
                                    self.sceneMap[item] = category
                                    print(cDate, category)
                                    self.sceneRecogCnt += 1
                                }
                            })
                            let handler = VNImageRequestHandler(cgImage: result!.cgImage!)
                            try handler.perform([request])
                        }
                        catch{
                            print("Error in prediction!")
                        }
                    }
                })
            }
        }
        catch{
            print("model error!")
        }
    }
    
    private func phaseText(image: UIImage) -> [String]{
        var text: [String] = []
        
        return text
    }
    
    public func getSceneMap() -> [PHAsset: String] {
        return self.sceneMap
    }
}
