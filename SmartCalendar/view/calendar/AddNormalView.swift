//
//  AddNormalView.swift
//  SmartCalendar
//

import SwiftUI

struct AddNormalView: View {
    @State private var name: String = ""
    @State private var category: String = ""
    @State private var category_chosen: Int = -1
    @State private var category_options: [(id: Int, name: String)] = []
    @State private var is_periodic: Bool = false
    @State private var period_chosen: Int = -1
    @State private var period_options: [(id: Int, name: String)] = []
    @State private var start: Date = Date()
    @State private var end: Date = Date()
    @State private var from: Date = Date()
    @State private var to: Date = Date()
    @Binding var schedules: [Schedule]
    @Binding var schedule_local: ScheduleContainer
    @Binding var isPresented: Bool
    
    var body: some View {
        VStack{
            HStack{
                Text("日程名称：")
                    .font(.title3)
                TextField("Schedule Name", text: $name)
                    .font(.title3)
            }
            HStack{
                Text("日程类别：")
                    .font(.title3)
                Spacer()
                Picker(selection: $category_chosen, label: Text("\(self.category_chosen == -1 ? "Category" : self.category_options[self.category_chosen].name)")) {
                    if(category_options.count > 0){
                        ForEach(0..<category_options.count) { i in
                            Text("\(category_options[i].name)")
                                .font(.title3)
                                .tag(i+1)
                        }
                    }
                }
                .pickerStyle(MenuPickerStyle())
                .padding()
            }
            
            Toggle(isOn: $is_periodic) {
                Text("定时任务：")
                    .font(.title3)
            }
            
            DatePicker("开始时间：", selection: $start, displayedComponents: self.is_periodic ? [.hourAndMinute] : [.date, .hourAndMinute])
                .font(.title3)
            DatePicker("结束时间：", selection: $end, displayedComponents: self.is_periodic ? [.hourAndMinute] : [.date, .hourAndMinute])
                .font(.title3)

            if(self.is_periodic){
                HStack{
                    Text("间隔：")
                        .font(.title3)
                    Spacer()
                    Picker(selection: $period_chosen, label: Text("\(self.period_chosen == -1 ? "Period" : self.period_options[self.period_chosen].name)")) {
                        if(period_options.count > 0){
                            ForEach(0..<period_options.count) { i in
                                Text("\(period_options[i].name)")
                                    .font(.title3)
                                    .tag(i+1)
                            }
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                    .padding()
                }
                
                DatePicker("起始时间：", selection: $from, displayedComponents: [.date])
                    .font(.title3)
                DatePicker("截止时间：", selection: $to, displayedComponents: [.date])
                    .font(.title3)
            }
        
            HStack{
                Button(action: {
                    if(self.name != "" && self.category_chosen != -1) {
                        if(self.is_periodic){
                            let from_date = DateUtils.getDate(time: self.from)
                            let to_date = DateUtils.getDate(time: self.to)
                            let day = self.period_options[self.period_chosen].id
                            let start_delta = Int(self.start.timeIntervalSince(DateUtils.getDate(time: self.start)))
                            let end_delta = Int(self.end.timeIntervalSince(DateUtils.getDate(time: self.end)))
                            let start_seq = DateUtils.getTimeSeq(from: from_date, to: to_date, day: day, delta: start_delta)
                            let end_seq = DateUtils.getTimeSeq(from: from_date, to: to_date, day: day, delta: end_delta)
                            if(start_seq.count == end_seq.count){
                                for i in 0 ..< start_seq.count {
                                    let schedule = Schedule(name: self.name, categoryId: self.category_options[self.category_chosen].id, categoryName: self.category_options[self.category_chosen].name, start: start_seq[i], end: end_seq[i])
                                    self.schedule_local.append(schedule)
                                    StorageUtils.saveScheduleToLocal(container: self.schedule_local)
                                    self.schedules.append(schedule)
                                    ScheduleUtils.addStorage(schedule: schedule, local_container: self.schedule_local, completion: {
                                        (status) -> Void in
                                        if(status){
                                            print("Server synchronization succeeded!")
                                        }
                                    })
                                }
                            }
                        }
                        else{
                            let schedule = Schedule(name: self.name, categoryId: self.category_options[self.category_chosen].id, categoryName: self.category_options[self.category_chosen].name, start: self.start, end: self.end)
                            self.schedule_local.append(schedule)
                            StorageUtils.saveScheduleToLocal(container: self.schedule_local)
                            self.schedules.append(schedule)
                            ScheduleUtils.addStorage(schedule: schedule, local_container: self.schedule_local, completion: {
                                (status) -> Void in
                                if(status){
                                    print("Server synchronization succeeded!")
                                }
                            })
                        }
                        self.isPresented = false
                    }
                }){
                    Text("确定")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
                Button(action: {
                    self.isPresented = false
                }){
                    Text("取消")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
            }
        }.padding()
        .onAppear(perform: {
            ScheduleUtils.getCategory(completion: {
                (status, categories) -> Void in
                if(status){
                    self.category_options = categories
                }
            })
            let period_list = ["每天", "每周一", "每周二", "每周三", "每周四", "每周五", "每周六", "每周日"]
            for i in 0 ..< period_list.count {
                self.period_options.append((id: i, name: period_list[i]))
            }
        })
    }
}

struct AddNormalView_Previews: PreviewProvider {
    static var previews: some View {
        AddNormalView(schedules: .constant([]), schedule_local: .constant(ScheduleContainer()), isPresented: .constant(true))
    }
}
