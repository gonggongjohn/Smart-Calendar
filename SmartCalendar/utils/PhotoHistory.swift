//
//  PhotoHistory.swift
//  SmartCalendar
//

import Foundation

class PhotoHistory: NSObject, NSSecureCoding {
    static var supportsSecureCoding: Bool {
        return true
    }
    
    private var phasedList: [String]
    
    required init?(coder: NSCoder) {
        self.phasedList = []
        let phasedComp: [String]? = coder.decodeObject(forKey: "photo_phased") as? [String]
        if phasedComp != nil && phasedComp!.count > 0 {
            for i in 0...phasedComp!.count - 1{
                self.phasedList.append(phasedComp![i])
            }
        }
    }
    
    func encode(with coder: NSCoder) {
        var phasedComp: [String] = []
        for item in self.phasedList{
            phasedComp.append(item)
        }
        coder.encode(phasedComp, forKey: "photo_phased")
    }
    
    override init() {
        self.phasedList = []
    }
    
    public func appendPhoto(name: String){
        self.phasedList.append(name)
    }
    
    public func appendPhotos(from: PhotoHistory){
        let oldList = from.getPhasedList()
        self.phasedList.append(contentsOf: oldList)
    }
    
    public func getPhasedList() -> [String] {
        return self.phasedList
    }
    
    public func isPhased(target: String) -> Bool{
        if self.phasedList.contains(target) {
            return true
        }
        else {
            return false
        }
    }
}
