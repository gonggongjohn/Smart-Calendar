//
//  ProfileUtils.swift
//  SmartCalendar
//

import Foundation

class ProfileUtils {
    public static func saveUserInfo(username: String, password: String){
        let info_wrapper = UserInfo(username: username, password: password)
        do{
            let data = try NSKeyedArchiver.archivedData(withRootObject: info_wrapper, requiringSecureCoding: true)
            UserDefaults.standard.setValue(data, forKey: "user_info")
        }
        catch{
            print("Error occurred when saving user info!")
        }
    }
    
    public static func getUserInfo() -> (username: String, password: String)?{
        let data = UserDefaults.standard.data(forKey: "user_info")
        var info_wrapper: UserInfo? = nil
        if data != nil {
            do{
                info_wrapper = try NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(data!) as? UserInfo
                if(info_wrapper != nil){
                    var info_tuple: (username: String, password: String)?
                    info_tuple = (username: info_wrapper!.getUsername(), password: info_wrapper!.getPassword())
                    return info_tuple
                }
            }
            catch{
                print("Error occurred when getting user info!")
            }
        }
        return nil
    }
    
    /* Save Geo History to UserDefaults (One day per entry) */
    public func saveGeoHistory(date: Date, history: GeoHistory){
        let oldHistory = getGeoHistory(date: date)
        if oldHistory != nil{
            history.appendLocations(from: oldHistory!)
        }
        do {
            let data = try NSKeyedArchiver.archivedData(withRootObject: history, requiringSecureCoding: true)
            let key = "GeoHistory_" + String(date.timeIntervalSince1970)
            UserDefaults.standard.setValue(data, forKey: key)
        }
        catch{
            print("Error occurred when saving geo data!")
        }
    }
    
    /* Get Geo Histroy of a day from UserDefaults */
    public func getGeoHistory(date: Date) -> GeoHistory?{
        let key = "GeoHistory_" + String(date.timeIntervalSince1970)
        let data = UserDefaults.standard.data(forKey: key)
        var history: GeoHistory? = nil
        if data != nil {
            do{
                history = try NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(data!) as? GeoHistory
            }
            catch{
                print("Error occurred when getting geo data!")
            }
        }
        return history
    }
    
    /* Get Geo Histroy of a day from UserDefaults */
    public func getGeoHistory(from: Date, to: Date) -> GeoHistory?{
        let dateSeq = DateUtils.getSequence(fromDate: from, toDate: to)
        let history = GeoHistory()
        for date in dateSeq {
            let key = "GeoHistory_" + String(date.timeIntervalSince1970)
            let data = UserDefaults.standard.data(forKey: key)
            if data != nil {
                do{
                    let tmpHistory = try NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(data!) as? GeoHistory
                    if tmpHistory != nil {
                        history.appendLocations(from: tmpHistory!)
                    }
                }
                catch{
                    print("Error occurred when getting geo data!")
                }
            }
        }
        return history
    }
    
    public func savePhotoHistory(history: PhotoHistory){
        let oldHistory = getPhotoHistory()
        if oldHistory != nil {
            history.appendPhotos(from: oldHistory!)
        }
        do{
            let data = try NSKeyedArchiver.archivedData(withRootObject: history, requiringSecureCoding: true)
            let key = "PhotoHistory"
            UserDefaults.standard.setValue(data, forKey: key)
        }
        catch{
            print("Error occurred when saving photo history!")
        }
    }
    
    public func getPhotoHistory() -> PhotoHistory?{
        let key = "PhotoHistory"
        let data = UserDefaults.standard.data(forKey: key)
        var history: PhotoHistory? = nil
        if data != nil {
            do{
                history = try NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(data!) as? PhotoHistory
            }
            catch{
                print("Error occurred when getting photo history!")
            }
        }
        return history
    }
    
    public static func saveScheduleHistory(history: ScheduleHistory){
        let oldHistory = getScheduleHistory()
        if oldHistory != nil {
            history.addSchedules(origin: oldHistory!)
        }
        do{
            let data = try NSKeyedArchiver.archivedData(withRootObject: history, requiringSecureCoding: true)
            let key = "schedule_history"
            UserDefaults.standard.setValue(data, forKey: key)
        }
        catch{
            print("Error ocurred when saving schedule history!")
        }
    }
    
    public static func getScheduleHistory() -> ScheduleHistory?{
        let key = "schedule_history"
        let data = UserDefaults.standard.data(forKey: key)
        var history: ScheduleHistory?
        if data != nil {
            do{
                history = try NSKeyedUnarchiver.unarchiveTopLevelObjectWithData(data!) as? ScheduleHistory
            }
            catch{
                print("Error occurred when getting schedule history!")
            }
        }
        return history
    }
}
