//
//  MiscViewCode.swift
//  SmartCalendar
//

import SwiftUI

struct MiscViewCode: View {
    var body: some View {
        VStack{
            /*
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
                        self.assetList = PhotoUtils.getMetaData(from: [2021, 6, 1])
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
        }
         */
        }
    }
}

struct MiscViewCode_Previews: PreviewProvider {
    static var previews: some View {
        MiscViewCode()
    }
}

/*
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
*/
