//
//  CalendarView.swift
//  SmartCalendar
//

import SwiftUI
import Photos

struct CalendarView: View {
    @State private var username: String = ""
    @State private var schedules: [Schedule] = []
    @State private var toggle_addSchedule: Bool = false
    @ObservedObject private var history = GeoHistory()
    @State private var phaseFlag = false
    @State private var loadingFlag = false
    @State private var homeworkNotice = false
    @State private var reviewSheet = false
    @State var assetList: [PHAsset] = []
    @ObservedObject var geoDict = HistoryDateWrapper()
    @State private var imageChosen: UIImage?
    @State private var hwLesson: [String] = []
    @State private var hwStart: [Date] = []
    @State private var hwDeadline: [Date] = []
    @State private var hwFlag: Bool = false
    @State private var txtTotNum = 0
    @State private var txtPhasedNum = 0
    @State private var functionEnable = false
    @State private var startTime = Date()
    @State private var endTime = Date()
    @State private var scheduleHistory = ScheduleHistory()
    
    var body: some View {
        ZStack{
            VStack{
                HStack{
                    Button(action: {
                        self.toggle_addSchedule = true
                    }){
                        Text("添加日程")
                            .font(.title2)
                            .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                            .background(RoundedRectangle(cornerRadius: 10.0)
                                            .stroke(Color.black, lineWidth: 2.0))
                    }
                    Spacer()
                    EditButton()
                }.padding()
                List{
                    ForEach(self.schedules){ schedule in
                        ScheduleRow(schedule: schedule)
                    }.onDelete(perform: { indexSet in
                        indexSet.forEach({
                            (index) -> Void in
                            let uuid = self.schedules[index].id
                            ScheduleUtils.removeFromServer(uuid: uuid, completion: {
                                (status) -> Void in
                                if(status){
                                    print("Remove from server succeeded!")
                                }
                            })
                        })
                        self.schedules.remove(atOffsets: indexSet)
                    })
                }
            }.disabled(self.toggle_addSchedule)
            .blur(radius: self.toggle_addSchedule ? 3 : 0)
            .onAppear(perform: {
                ScheduleUtils.getFromServer(completion: {
                    (status, schedule_list) -> Void in
                    if(status){
                        self.schedules.append(contentsOf: schedule_list!)
                    }
                })
            })
            
            if(self.toggle_addSchedule){
                AddScheduleView(schedules: $schedules, isPresented: $toggle_addSchedule)
            }
        }
    }
}

struct ScheduleRow: View {
    var schedule: Schedule
    
    var body: some View {
        VStack{
            Text("日程名称：\(schedule.name)")
            Text("日程类别：\(schedule.category.name)")
            Text("开始时间：\(DateUtils.date2str(date: schedule.start))")
            Text("结束时间：\(DateUtils.date2str(date: schedule.end))")
        }
        .padding(.all)
        .background(/*@START_MENU_TOKEN@*//*@PLACEHOLDER=View@*/Color.green/*@END_MENU_TOKEN@*/)
        .cornerRadius(20.0)
        .shadow(radius: 3)
    }
}

func phaseGeo(assets: [PHAsset], geoRecord: HistoryDateWrapper, completion: @escaping () -> Void){
    let geoUtils = GeoUtils()
    let profileHistory = ProfileUtils()
    var geoCnt = 0
    var photoHistory = profileHistory.getPhotoHistory()
    for item in assets {
        if photoHistory != nil && photoHistory!.isPhased(target: item.localIdentifier){
            continue
        }
        let location = PhotoUtils.getPhotoGeo(asset: item)
        if location != nil {
            geoRecord.totalNum += 1
            geoCnt += 1
            DispatchQueue.main.asyncAfter(deadline: .now() + Double(2 * geoCnt)){
                let geoEncoder = CLGeocoder()
                let transCoord = geoUtils.transformWGSToGCJ(wgsLocation: location!.coordinate)
                let transLoc = CLLocation(latitude: transCoord.latitude, longitude: transCoord.longitude)
                geoEncoder.reverseGeocodeLocation(transLoc, completionHandler: {
                    (placemarks: [CLPlacemark]?, err: Error?) -> Void in
                    if err != nil && placemarks == nil{
                        print(err!)
                    }
                    else{
                        for placemark in placemarks!{
                            let date = DateUtils.getDate(time: location!.timestamp)
                            if geoRecord.dateDict[date] == nil {
                                geoRecord.dateDict[date] = GeoHistory()
                            }
                            geoRecord.dateDict[date]?.appendLocation(geo: location!, name: placemark.name!)
                            
                        }
                        print("loc get!")
                    }
                    geoRecord.phasedNum += 1
                    if photoHistory == nil {
                        photoHistory = PhotoHistory()
                    }
                    photoHistory!.appendPhoto(name: item.localIdentifier)
                    if geoRecord.phasedNum == geoRecord.totalNum {
                        saveGeo(record: geoRecord)
                        profileHistory.savePhotoHistory(history: photoHistory!)
                        completion()
                    }
                })
            }
        }
        else{
            if photoHistory == nil {
                photoHistory = PhotoHistory()
            }
            photoHistory!.appendPhoto(name: item.localIdentifier)
        }
    }
}

func saveGeo(record: HistoryDateWrapper){
    let profileUtils = ProfileUtils()
    for (date, history) in record.dateDict {
        profileUtils.saveGeoHistory(date: date, history: history)
    }
    print("Geo saved!")
}

func formulateReviewPlan(deadline: Date) -> [(Date, Date)] {
    var plan: [(Date, Date)] = []
    plan.append((DateUtils.getTime(year: 2021, month: 6, day: 19, hour: 10, minute: 00, second: 00), DateUtils.getTime(year: 2021, month: 6, day: 19, hour: 18, minute: 00, second: 00)))
    plan.append((DateUtils.getTime(year: 2021, month: 6, day: 20, hour: 10, minute: 00, second: 00), DateUtils.getTime(year: 2021, month: 6, day: 20, hour: 18, minute: 00, second: 00)))
    plan.append((DateUtils.getTime(year: 2021, month: 6, day: 19, hour: 10, minute: 00, second: 00), DateUtils.getTime(year: 2021, month: 6, day: 19, hour: 18, minute: 00, second: 00)))
    plan.append((DateUtils.getTime(year: 2021, month: 6, day: 23, hour: 12, minute: 30, second: 00), DateUtils.getTime(year: 2021, month: 6, day: 23, hour: 16, minute: 00, second: 00)))
    plan.append((DateUtils.getTime(year: 2021, month: 6, day: 25, hour: 15, minute: 30, second: 00), DateUtils.getTime(year: 2021, month: 6, day: 25, hour: 20, minute: 00, second: 00)))
    plan.append((DateUtils.getTime(year: 2021, month: 6, day: 26, hour: 10, minute: 00, second: 00), DateUtils.getTime(year: 2021, month: 6, day: 26, hour: 18, minute: 00, second: 00)))
    return plan
}

func joinString(from: [String]) -> String {
    var result = ""
    for str in from {
        result = result + " " + str
    }
    return result
}

struct CalendarView_Previews: PreviewProvider {
    static var previews: some View {
        CalendarView()
    }
}
