//
//  PhotoUtils.swift
//  SmartCalendar
//

import Foundation
import Photos
import CoreLocation
import Vision
import UIKit

class PhotoUtils{
    
    /* Get photos from a date to another date */
    public static func getMetaData(from: Date, to: Date) -> [PHAsset] {
        PHPhotoLibrary.requestAuthorization { (status) in
            if status != .authorized {
                print("No permission!")
            }
        }
        let options = PHFetchOptions()
        options.predicate = NSPredicate(format: "(creationDate >= %@) AND (creationDate <= %@)", argumentArray: [from, to])
        options.sortDescriptors = [NSSortDescriptor(key: "creationDate", ascending: true)]
        let fetchRst = PHAsset.fetchAssets(with: options)
        var assetList: [PHAsset] = []
        for i in 0 ..< fetchRst.count {
            let item = fetchRst.object(at: i)
            assetList.append(item)
        }
        print("Fetch successful!")
        return assetList
    }
    
    /* Get photos from YYYY-MM-DD to YYYY-MM-DD */
    public static func getMetaData(from: [Int], to: [Int]) -> [PHAsset] {
        PHPhotoLibrary.requestAuthorization { (status) in
            if status != .authorized {
                print("No permission!")
            }
        }
        let options = PHFetchOptions()
        var fromComp = DateComponents()
        fromComp.year = from[0]
        fromComp.month = from[1]
        fromComp.day = from[2]
        fromComp.hour = 0
        fromComp.minute = 0
        fromComp.second = 0
        var toComp = DateComponents()
        toComp.year = to[0]
        toComp.month = to[1]
        toComp.day = to[2]
        toComp.hour = 0
        toComp.minute = 0
        toComp.second = 0
        let fromDate = Calendar.current.date(from: fromComp)
        let toDate = Calendar.current.date(from: toComp)
        options.predicate = NSPredicate(format: "(creationDate >= %@) AND (creationDate <= %@)", argumentArray: [fromDate, toDate])
        options.sortDescriptors = [NSSortDescriptor(key: "creationDate", ascending: true)]
        let fetchRst = PHAsset.fetchAssets(with: options)
        var assetList: [PHAsset] = []
        for i in 0 ..< fetchRst.count {
            let item = fetchRst.object(at: i)
            assetList.append(item)
        }
        print("Fetch successful!")
        return assetList
    }
    
    /* Get photos from YYYY-MM-DD to now */
    public static func getMetaData(from: [Int]) -> [PHAsset] {
        PHPhotoLibrary.requestAuthorization { (status) in
            if status != .authorized {
                print("No permission!")
            }
        }
        let options = PHFetchOptions()
        var fromComp = DateComponents()
        fromComp.year = from[0]
        fromComp.month = from[1]
        fromComp.day = from[2]
        fromComp.hour = 0
        fromComp.minute = 0
        fromComp.second = 0
        let fromDate = Calendar.current.date(from: fromComp)
        let toDate = Date()
        options.predicate = NSPredicate(format: "(creationDate >= %@) AND (creationDate <= %@)", argumentArray: [fromDate, toDate])
        options.sortDescriptors = [NSSortDescriptor(key: "creationDate", ascending: true)]
        let fetchRst = PHAsset.fetchAssets(with: options)
        var assetList: [PHAsset] = []
        for i in 0 ..< fetchRst.count {
            let item = fetchRst.object(at: i)
            assetList.append(item)
        }
        print("Fetch successful!")
        return assetList
    }
    
    public static func getPhotoGeo(asset: PHAsset) -> CLLocation? {
        let location: CLLocation? = asset.location
        return location
    }
    
    public static func phaseScene(assets: [PHAsset], completion: @escaping ([PHAsset: String]) -> Void){
        let imageManager = PHImageManager.default()
        var totalNum = assets.count
        var phasedNum = 0
        var sceneDict = [PHAsset: String]()
        DispatchQueue.global().async {
            do{
                let sceneModel = try VNCoreMLModel(for: scene_1615560045().model)
                for item in assets {
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
                                        print(category)
                                        sceneDict[item] = category
                                        phasedNum += 1
                                        print(phasedNum)
                                        if phasedNum == totalNum {
                                            completion(sceneDict)
                                        }
                                    }
                                })
                                let handler = VNImageRequestHandler(cgImage: result!.cgImage!)
                                try handler.perform([request])
                            }
                            catch{
                                print("Error in prediction!")
                            }
                        }
                        else{
                            totalNum -= 1
                        }
                    })
                }
            }
            catch{
                print("model error!")
            }
        }
    }
    
    public static func phaseText(asset: PHAsset, completion completionFather: @escaping ([String]) -> Void){
        let imageManager = PHImageManager.default()
        let requestOption = PHImageRequestOptions()
        requestOption.isSynchronous = true
        requestOption.deliveryMode = .opportunistic
        imageManager.requestImage(for: asset, targetSize: CGSize(width: 800, height: 800), contentMode: .aspectFit, options: requestOption, resultHandler: {
            (result, _) -> Void in
            if result != nil {
                BaiduOCR.requestText(image: result!, completion: {
                    (text) -> Void in
                    completionFather(text)
                })
            }
        })
    }
    
    public static func phaseLessons(image: UIImage, completion completionFather: @escaping (LessonWrapper) -> Void){
        BaiduOCR.requestTable(image: image, completion: {
            (json) -> Void in
            let wrapper = LessonWrapper()
            for (_,subJson):(String, JSON) in json{
                let day = subJson["day"].intValue
                let start = subJson["start"].intValue
                let end = subJson["end"].intValue
                let lesson = subJson["lesson"].stringValue
                wrapper.appendLesson(day: day, deltaStart: start, deltaEnd: end, name: lesson)
            }
            completionFather(wrapper)
        })
    }
    
    public static func fetchImage(asset: PHAsset, completion: @escaping (UIImage) -> Void){
        let imageManager = PHImageManager.default()
        let requestOption = PHImageRequestOptions()
        requestOption.isSynchronous = true
        requestOption.deliveryMode = .opportunistic
        imageManager.requestImage(for: asset, targetSize: CGSize(width: 800, height: 800), contentMode: .aspectFit, options: requestOption, resultHandler: {
            (result, _) -> Void in
            if result != nil {
                completion(result!)
            }
        })
    }
}
