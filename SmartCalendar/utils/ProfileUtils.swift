//
//  ProfileUtils.swift
//  SmartCalendar
//

import Foundation

class ProfileUtils {
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
        let dateSeq = TimeHelper.getSequence(fromDate: from, toDate: to)
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
    
    public func saveScheduleHistory(user: String, history: ScheduleHistory){
        let oldHistory = getScheduleHistory(user: user)
        if oldHistory != nil {
            history.addSchedules(origin: oldHistory!)
        }
        do{
            let data = try NSKeyedArchiver.archivedData(withRootObject: history, requiringSecureCoding: true)
            let key = "ScheduleHistory_" + user
            UserDefaults.standard.setValue(data, forKey: key)
        }
        catch{
            print("Error ocurred when saving schedule history!")
        }
    }
    
    public func getScheduleHistory(user: String) -> ScheduleHistory?{
        let key = "ScheduleHistory_" + user
        let data = UserDefaults.standard.data(forKey: key)
        var history: ScheduleHistory? = nil
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
