//
//  Baiduocr.swift
//  MemorizeApp

import Foundation
import UIKit

class BaiduOCR {
    //请求API接口
    public static func requestText(image: UIImage, completion: @escaping ([String]) -> Void){
        var text: [String] = []
        // 将图片转化成Data
        let imageData = image.pngData()

        // 将Data转化成 base64的字符串
        let base64 = imageData?.base64EncodedString()

        //继续将base64字符串urlencode，确保只有数字和字母
        let imageString = base64?.addingPercentEncoding(withAllowedCharacters:
            .alphanumerics)

        let httpUrl="https://aip.baidubce.com/rest/2.0/ocr/v1/general?access_token=24.cde4ac167c5028ae80a65c0f1fe7651c.2592000.1619914601.282335-23919450"
        
        //创建请求对象
        var request = URLRequest(url: URL(string: httpUrl)!)
        request.timeoutInterval = 6
        request.httpMethod = "POST"
        
        //header
        request.addValue("application/x-www-form-urlencoded",forHTTPHeaderField: "Content-Type")
        
        //httpbody
        let httpArg = "&fromdevice=iPhone&clientip=10.10.10.0&detecttype=LocateRecognize" +
            "&languagetype=CHN_ENG&imagetype=1&image=" + imageString!
        request.httpBody = httpArg.data(using: .utf8)
        
        //使用URLSession发起请求
        let session = URLSession.shared
        let dataTask = session.dataTask(with: request, completionHandler: {
            (data, response, error) -> Void in
            if error != nil{
                print(error.debugDescription)
            }else{
                //解析数据
                let json = try!JSON(data:data!)
                print(json)
                for (_,subJson):(String, JSON) in json["words_result"]{
                    text.append("\(subJson["words"].stringValue)")
                }
            }
            completion(text)
        }) as URLSessionTask
        //使用resume方法启动任务
        dataTask.resume()
    }
    
    public static func requestTable(image: UIImage, completion: @escaping (JSON) -> Void){
        let imageData = image.pngData()
        // 将Data转化成 base64的字符串
        let base64 = imageData?.base64EncodedString()
        //继续将base64字符串urlencode，确保只有数字和字母
        //let imageString = base64?.addingPercentEncoding(withAllowedCharacters:
        //    .alphanumerics)
        let json_req: [String: Any] = ["image": base64]
        let jsonData = try? JSONSerialization.data(withJSONObject: json_req)
        let httpUrl = "http://mc.mcgo.pw:40018/lesson"
        var request = URLRequest(url: URL(string: httpUrl)!)
        request.timeoutInterval = 60
        request.httpMethod = "POST"
        request.httpBody = jsonData
        //使用URLSession发起请求
        let session = URLSession.shared
        let dataTask = session.dataTask(with: request, completionHandler: {
            (data, response, error) -> Void in
            if error != nil{
                print(error.debugDescription)
            }else{
                //解析数据
                let json = try!JSON(data:data!)
                print(json)
                completion(json["lessons"])
            }
        }) as URLSessionTask
        //使用resume方法启动任务
        dataTask.resume()
    }
}
