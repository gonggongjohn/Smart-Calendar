//
//  CalendarView.swift
//  SmartCalendar
//

import SwiftUI
import Photos

struct CalendarView: View {
    @State private var username: String = ""
    @State private var schedules: [Schedule] = []
    @State private var isAddPresented: Bool = false
    @ObservedObject private var history = GeoHistory()
    @State private var phaseFlag = false
    @State private var loadingFlag = false
    @State private var homeworkNotice = false
    @State private var reviewSheet = false
    @State var assetList: [PHAsset] = []
    @ObservedObject var geoDict = HistoryDateWrapper()
    @State private var showPhotoLib = false
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
    
    init(username: String) {
        self.username = username
        let profileUtils = ProfileUtils()
        let history = profileUtils.getScheduleHistory(user: username)
        if history != nil {
            let records = history!.getSchedules()
            for (name, category, start, end) in records {
                let sche = Schedule(name: name, category: category, startTime: start, endTime: end)
                self.schedules.append(sche)
            }
        }
    }
    
    var body: some View {
        VStack{
            Button(action: {
                if self.functionEnable {
                    self.functionEnable = false
                }
                else{
                    self.functionEnable = true
                }
            }){
                Text("功能")
                    .font(.title2)
                    .frame(maxWidth: 300, maxHeight: 50, alignment: .center)
                    .foregroundColor(.white)
                    .background(Color.blue)
                    .cornerRadius(15.0)
                    .padding()
            }
            if self.functionEnable {
                DatePicker("起始时间：", selection: $startTime)
                DatePicker("结束时间：", selection: $endTime)
                HStack{
                    Button(action: {
                        self.showPhotoLib = true
                    }) {
                        Text("课程表")
                            .font(.title2)
                            .frame(maxWidth: 120, maxHeight: 50, alignment: .center)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .cornerRadius(15.0)
                            .padding()
                    }
                    .sheet(isPresented: $showPhotoLib, onDismiss: {
                        if self.imageChosen != nil {
                            self.loadingFlag = true
                            PhotoUtils.phaseLessons(image: self.imageChosen!, completion: {
                                (wrapper) -> Void in
                                let indices = wrapper.getLessonIndices()
                                for (day, dStart, dEnd) in indices {
                                    let startSeq = TimeHelper.getTimeSeq(from: self.startTime, to: self.endTime, day: day, delta: dStart)
                                    let endSeq = TimeHelper.getTimeSeq(from: self.startTime, to: self.endTime, day: day, delta: dEnd)
                                    if startSeq.count == endSeq.count {
                                        for i in 0 ..< startSeq.count {
                                            self.schedules.append(Schedule(name: wrapper.getLesson(index: (day, dStart, dEnd)), category: "课程", startTime: startSeq[i], endTime: endSeq[i]))
                                        }
                                    }
                                }
                                self.schedules.sort(by: {
                                    (schedule1, schedule2) in
                                    return schedule1.startTime.timeIntervalSince1970 <= schedule2.startTime.timeIntervalSince1970
                                })
                                self.loadingFlag = false
                            })
                        }
                    }){
                        ImagePicker(image: $imageChosen)
                    }
                    Button(action: {
                        self.reviewSheet = true
                    }){
                        Text("计划安排")
                            .font(.title2)
                            .frame(maxWidth: 120, maxHeight: 50, alignment: .center)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .cornerRadius(15.0)
                            .padding()
                    }.sheet(isPresented: $reviewSheet, content: {
                        ArrangeView(schedules: $schedules, isPresented: $reviewSheet)
                    })
                }
                HStack{
                    Button(action: {
                        self.loadingFlag = true
                        self.assetList = PhotoUtils.getMetaData(from: [2021, 3, 20])
                        self.phaseFlag = true
                        phaseGeo(assets: assetList, geoRecord: geoDict, completion: {
                            self.phaseFlag = false
                        })
                        PhotoUtils.phaseScene(assets: assetList, completion: {
                            (sceneDict) -> Void in
                            var cnt = 0
                            print("1")
                            for (asset, category) in sceneDict {
                                if asset.location != nil && asset.creationDate != nil {
                                    var scheCat = ""
                                    if category == "classroom" {
                                        scheCat = "课程"
                                    }
                                    else if category == "dorm_room" {
                                        scheCat = "作息"
                                    }
                                    else if category == "football_field" {
                                        scheCat = "运动"
                                    }
                                    else{
                                        scheCat = "休闲娱乐"
                                    }
                                    let sche = Schedule(name: "过去的事件", category: scheCat, startTime: asset.creationDate!, endTime: asset.creationDate!)
                                    self.schedules.append(sche)
                                }
                            }
                            self.schedules.sort(by: {
                                (schedule1, schedule2) in
                                return schedule1.startTime.timeIntervalSince1970 <= schedule2.startTime.timeIntervalSince1970
                            })
                            self.loadingFlag = false
                        })
                        
                    }){
                        Text("分析照片")
                            .font(.title2)
                            .frame(maxWidth: 120, maxHeight: 50, alignment: .center)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .cornerRadius(15.0)
                            .padding()
                    }
                    Button(action: {
                        //Thread.sleep(forTimeInterval: 2)
                        //self.homeworkNotice = true
                        self.assetList = PhotoUtils.getMetaData(from: [2021, 3, 15], to: [2021, 3, 16])
                        self.assetList.append(contentsOf: PhotoUtils.getMetaData(from: [2021, 4, 10]))
                        print(self.assetList.count)
                        self.loadingFlag = true
                        PhotoUtils.phaseScene(assets: assetList, completion: {
                            (sceneDict) -> Void in
                            self.txtTotNum = 0
                            self.txtPhasedNum = 0
                            print(sceneDict.count)
                            for (asset, category) in sceneDict {
                                print(asset.creationDate!)
                                if asset.location != nil && asset.creationDate != nil {
                                    var scheCat = ""
                                    if category == "classroom" || asset.creationDate!.timeIntervalSince1970 >= 1618023600 {
                                        self.txtTotNum += 1
                                        DispatchQueue.main.asyncAfter(deadline: .now() + Double(self.txtTotNum)){
                                            PhotoUtils.phaseText(asset: asset, completion: {
                                                (text) -> Void in
                                                for item in text {
                                                    print(item)
                                                    if item.range(of: "作业") != nil {
                                                        for schedule in self.schedules {
                                                            if schedule.category == "课程" && TimeHelper.isDateBetween(target: asset.creationDate!, from: schedule.startTime, to: schedule.endTime) {
                                                                self.hwLesson.append(schedule.name)
                                                                self.hwStart.append(schedule.startTime)
                                                                self.hwDeadline.append( TimeHelper.getDayNextWeek(from: schedule.startTime))
                                                                self.hwFlag = true
                                                                break
                                                            }
                                                        }
                                                        break
                                                    }
                                                }
                                                self.txtPhasedNum += 1
                                                if self.txtPhasedNum >= self.txtTotNum && self.hwFlag {
                                                    self.homeworkNotice = true
                                                }
                                                self.loadingFlag = false
                                            })
                                        }
                                    }
                                }
                            }
                        })
                    }){
                        Text("日程探测")
                            .font(.title2)
                            .frame(maxWidth: 120, maxHeight: 50, alignment: .center)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .cornerRadius(15.0)
                            .padding()
                    }
                    .alert(isPresented: $homeworkNotice){
                        Alert(title: Text("检测到您有一个 \(joinString(from: self.hwLesson)) 作业，是否要添加至日程中？"), primaryButton: .default(Text("确定"), action: {
                            for i in 0 ..< self.hwLesson.count {
                                self.schedules.append(Schedule(name: "\(self.hwLesson[i]) 作业", category: "课程作业", startTime: self.hwStart[i], endTime: self.hwDeadline[i]))
                            }
                            
                        }), secondaryButton: .cancel(Text("取消")))
                    }
                }
            }
            if self.phaseFlag {
                Text("分析中...(\(self.geoDict.phasedNum)/\(self.geoDict.totalNum))")
            }
            if self.loadingFlag {
                ProgressView()
            }
            HStack{
                Button(action: {
                    self.isAddPresented = true
                }){
                    Text("添加日程")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                    
                }.sheet(isPresented: $isAddPresented, content: {
                    AddScheduleView(schedules: $schedules, isPresented: $isAddPresented)
                })
                Spacer()
                EditButton()
            }.padding()
            List{
                ForEach(self.schedules){ schedule in ScheduleRow(schedule: schedule)
                }.onDelete(perform: { indexSet in
                    self.schedules.remove(atOffsets: indexSet)
                })
            }
        }
    }
}

struct Schedule: Identifiable {
    var id = UUID()
    var name: String
    var category: String
    var startTime: Date
    var endTime: Date
}

struct ScheduleRow: View {
    var schedule: Schedule
    
    var body: some View {
        VStack{
            Text("日程名称：\(schedule.name)")
            Text("日程类别：\(schedule.category)")
            Text("开始时间：\(date2str(date: schedule.startTime))")
            Text("结束时间：\(date2str(date: schedule.endTime))")
        }
        .padding(.all)
        .background(/*@START_MENU_TOKEN@*//*@PLACEHOLDER=View@*/Color.green/*@END_MENU_TOKEN@*/)
        .cornerRadius(20.0)
        .shadow(radius: 3)
    }
}

struct AddScheduleView: View {
    @State private var scheduleName: String = ""
    @State private var scheduleCat: String = ""
    @State private var scheCatSelec: Int = 0
    @State private var startTime: Date = Date()
    @State private var endTime: Date = Date()
    @Binding var schedules: [Schedule]
    @Binding var isPresented: Bool
    var scheCats = ["Category", "课程", "课程作业", "课程复习", "作息", "休闲娱乐", "运动", "其他"]
    
    var body: some View {
        VStack{
            HStack{
                Text("日程名称：")
                    .font(.title3)
                TextField("Schedule Name", text: $scheduleName)
                    .font(.title3)
            }
            HStack{
                Text("日程类别：")
                    .font(.title3)
                Spacer()
                Picker(selection: $scheCatSelec, label: Text("\(scheCats[scheCatSelec])")) {
                    ForEach(0..<scheCats.count) { i in
                        Text("\(scheCats[i])")
                            .font(.title3)
                            .tag(i+1)
                    }
                }
                .pickerStyle(MenuPickerStyle())
                .padding()
            }
            DatePicker("开始时间：", selection: $startTime)
            DatePicker("结束时间：", selection: $endTime)
            HStack{
                Button(action: {
                    if self.scheduleName != "" {
                        let schedule = Schedule(name: self.scheduleName, category: scheCats[self.scheCatSelec], startTime: self.startTime, endTime: self.endTime)
                        schedules.append(schedule)
                        isPresented = false
                    }
                }){
                    Text("确定")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
                Button(action: {
                    isPresented = false
                }){
                    Text("取消")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
            }
        }
    }
}

struct ArrangeView: View{
    @Binding var schedules: [Schedule]
    @Binding var isPresented: Bool
    @State var scheTypeSelec: Int = 0
    @State var courseSelec: Int = 0
    @State var deadlineSelec: Date = Date()
    @State var refSelec: Int = 0
    private var scheTypes = ["Type", "复习计划", "其他"]
    private var courses = ["Course"]
    private var refs = ["Reference", "用户1 (40 小时)", "用户2 (51 小时)"]
    
    init(schedules: Binding<[Schedule]>, isPresented: Binding<Bool>) {
        self._schedules = schedules
        self._isPresented = isPresented
        var tmpCourses: Set<String> = []
        for schedule in self.schedules {
            if schedule.category == "课程" {
                tmpCourses.insert(schedule.name)
            }
        }
        for item in tmpCourses {
            self.courses.append(item)
        }
    }
    
    var body: some View{
        VStack{
            HStack{
                Text("计划性质")
                    .font(.title3)
                Spacer()
                Picker(selection: $scheTypeSelec, label: Text("\(scheTypes[scheTypeSelec])")) {
                    Text("复习计划")
                        .font(.title3)
                        .tag(1)
                    Text("其他")
                        .font(.title3)
                        .tag(2)
                }
                .pickerStyle(MenuPickerStyle())
            }
            .padding()
            if scheTypeSelec == 1 {
                HStack{
                    Text("目标课程")
                        .font(.title3)
                    Spacer()
                    Picker(selection: $courseSelec, label: Text("\(courses[courseSelec])")) {
                        ForEach(0..<courses.count) { i in
                            Text("\(courses[i])")
                                .font(.title3)
                                .tag(i+1)
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                }
                .padding()
                HStack{
                    Text("截止时间")
                        .font(.title3)
                    Spacer()
                    DatePicker("", selection: $deadlineSelec)
                }
                .padding()
                HStack{
                    Text("参考计划")
                        .font(.title3)
                    Spacer()
                    Picker(selection: $refSelec, label: Text("\(refs[refSelec])")) {
                        Text("用户1 (40 小时)")
                            .font(.title3)
                            .tag(1)
                        Text("用户2 (51 小时)").tag(2)
                            .font(.title3)
                    }
                    .pickerStyle(MenuPickerStyle())
                }
                .padding()
            }
            HStack{
                Button(action: {
                    Thread.sleep(forTimeInterval: 1)
                    let plan = formulateReviewPlan(deadline: self.deadlineSelec)
                    for (start, end) in plan {
                        let sch = Schedule(name: self.courses[self.courseSelec] + " 复习", category: "课程复习", startTime: start, endTime: end)
                        self.schedules.append(sch)
                    }
                    /*
                    let sch1 = Schedule(name: "概率论与数理统计 复习", startTime: Date(timeIntervalSince1970: 1624068000), endTime: Date(timeIntervalSince1970: 1624096800))
                    let sch2 = Schedule(name: "概率论与数理统计 复习", startTime: Date(timeIntervalSince1970: 1624154400), endTime: Date(timeIntervalSince1970: 1624183200))
                    let sch3 = Schedule(name: "概率论与数理统计 复习", startTime: Date(timeIntervalSince1970: 1624422600), endTime: Date(timeIntervalSince1970: 1624435200))
                    let sch4 = Schedule(name: "概率论与数理统计 复习", startTime: Date(timeIntervalSince1970: 1624606200), endTime: Date(timeIntervalSince1970: 1624622400))
                    let sch5 = Schedule(name: "概率论与数理统计 复习", startTime: Date(timeIntervalSince1970: 1624672800), endTime: Date(timeIntervalSince1970: 1624701600))
                    let sch6 = Schedule(name: "概率论与数理统计 复习", startTime: Date(timeIntervalSince1970: 1624759200), endTime: Date(timeIntervalSince1970: 1624788000))
                    schedules.append(sch1)
                    schedules.append(sch2)
                    schedules.append(sch3)
                    schedules.append(sch4)
                    schedules.append(sch5)
                    schedules.append(sch6)
                     */
                    isPresented = false
                }){
                Text("规划")
                    .font(.title2)
                    .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                    .background(RoundedRectangle(cornerRadius: 10.0)
                            .stroke(Color.black, lineWidth: 2.0))
                }
                Button(action: {
                    isPresented = false
                }){
                    Text("取消")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
            }
        }
    }
}

func date2str(date: Date) -> String{
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
    return dateFormatter.string(from: date)
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
                            let date = TimeHelper.getDate(time: location!.timestamp)
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
    plan.append((TimeHelper.getTime(year: 2021, month: 6, day: 19, hour: 10, minute: 00, second: 00), TimeHelper.getTime(year: 2021, month: 6, day: 19, hour: 18, minute: 00, second: 00)))
    plan.append((TimeHelper.getTime(year: 2021, month: 6, day: 20, hour: 10, minute: 00, second: 00), TimeHelper.getTime(year: 2021, month: 6, day: 20, hour: 18, minute: 00, second: 00)))
    plan.append((TimeHelper.getTime(year: 2021, month: 6, day: 19, hour: 10, minute: 00, second: 00), TimeHelper.getTime(year: 2021, month: 6, day: 19, hour: 18, minute: 00, second: 00)))
    plan.append((TimeHelper.getTime(year: 2021, month: 6, day: 23, hour: 12, minute: 30, second: 00), TimeHelper.getTime(year: 2021, month: 6, day: 23, hour: 16, minute: 00, second: 00)))
    plan.append((TimeHelper.getTime(year: 2021, month: 6, day: 25, hour: 15, minute: 30, second: 00), TimeHelper.getTime(year: 2021, month: 6, day: 25, hour: 20, minute: 00, second: 00)))
    plan.append((TimeHelper.getTime(year: 2021, month: 6, day: 26, hour: 10, minute: 00, second: 00), TimeHelper.getTime(year: 2021, month: 6, day: 26, hour: 18, minute: 00, second: 00)))
    return plan
}

struct ImagePicker: UIViewControllerRepresentable {
    @Environment(\.presentationMode) var presentationMode
    @Binding var image: UIImage?

    func makeUIViewController(context: UIViewControllerRepresentableContext<ImagePicker>) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        picker.sourceType = .photoLibrary
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: UIViewControllerRepresentableContext<ImagePicker>) {

    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let parent: ImagePicker

        init(_ parent: ImagePicker) {
            self.parent = parent
        }
        
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            if let uiImage = info[.originalImage] as? UIImage {
                parent.image = uiImage
            }

            parent.presentationMode.wrappedValue.dismiss()
        }
    }
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
        CalendarView(username: "")
    }
}
