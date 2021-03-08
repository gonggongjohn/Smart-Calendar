//
//  BaiduOcr.swift
//  SmartCalendar
//

import UIKit

class ViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        //获取图片
        let file = Bundle.main.path(forResource: "xxx", ofType: "jpg")!
        let fileUrl = URL(fileURLWithPath: file)
        let fileData = try! Data(contentsOf: fileUrl)
        //将图片转为base64编码
        let base64 = fileData.base64EncodedString(options: .endLineWithLineFeed)
        //继续将base64字符串urlencode，确保只有数字和字母
        let imageString = base64.addingPercentEncoding(withAllowedCharacters:
            .alphanumerics)
      
        //请求接口
        request(imageString: imageString!)
    }
    
    //请求API接口
    func request(imageString: String) {
        let httpUrl="https://aip.baidubce.com/rest/2.0/ocr/v1/general?access_token=24.33d799e667620cffb82492fbb6e10041.2592000.1616327811.282335-23647105"
        
        //创建请求对象
        var request = URLRequest(url: URL(string: httpUrl)!)
        request.timeoutInterval = 6
        request.httpMethod = "POST"
        
        //header
        request.addValue("application/x-www-form-urlencoded",forHTTPHeaderField: "Content-Type")
        
        //httpbody
        let httpArg = "&fromdevice=iPhone&clientip=10.10.10.0&detecttype=LocateRecognize" +
            "&languagetype=CHN_ENG&imagetype=1&image=" + imageString
        request.httpBody = httpArg.data(using: .utf8)
        
        //使用URLSession发起请求
        let session = URLSession.shared
        let dataTask = session.dataTask(with: request,
                            completionHandler: {(data, response, error) -> Void in
                                if error != nil{
                                    print(error.debugDescription)
                                }else if let d = data{
                                    //let str = String(data: d, encoding: .utf8)!
                                    //print("----- 原始数据 -----\n\(str)")
                                    //解析数据并显示结果
                                    self.showResult(data: d)
                                }
        }) as URLSessionTask
        //使用resume方法启动任务
        dataTask.resume()
    }
    
    //解析数据
    func showResult(data:Data) {
        var result = ""
        let json = try!JSON(data: data)
        for (_,subJson):(String, JSON) in json["words_result"]{
            result.append("\(subJson["words"].stringValue)\n")
        }
        print(result)
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
}


