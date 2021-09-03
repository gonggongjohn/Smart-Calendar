//
//  AddDynamicView.swift
//  SmartCalendar
//

import SwiftUI

struct AddDynamicView: View {
    @State private var name: String = ""
    @State private var category: String = ""
    @State private var category_chosen: Int = -1
    @State private var category_options: [(id: Int, name: String)] = []
    @State private var duration_str: String = ""
    @State private var duration: Int = 0
    @State private var start: Date = Date()
    @State private var end: Date = Date()
    @State private var reference_options = ["Name：离散数学复习计划 User: GONGGONGJOHN"]
    @State private var reference_chosen = -1
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
            HStack{
                Text("预期时长：")
                    .font(.title3)
                Spacer()
                TextField("Hour", text: $duration_str)
                    .font(.title3)
                    .background(Color(.systemGray6))
                    .cornerRadius(5.0)
                Text("小时")
                    .font(.title3)
            }
            
            DatePicker("起始时间：", selection: $start)
                .font(.title3)
            DatePicker("最后期限：", selection: $end)
                .font(.title3)
            
            if(self.category_chosen != -1 && self.category_options[self.category_chosen].name == "学习"){
                HStack{
                    Text("参考：")
                        .font(.title3)
                    Spacer()
                    Picker(selection: $reference_chosen, label: Text("\(self.reference_chosen == -1 ? "Reference" : self.reference_options[self.reference_chosen])")) {
                        if(reference_options.count > 0){
                            ForEach(0..<reference_options.count) { i in
                                Text("\(reference_options[i])")
                                    .font(.title3)
                                    .tag(i+1)
                            }
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                    .padding()
                }
            }
            
            HStack{
                Button(action: {
                    if(self.name != "" && self.category_chosen >= 0){
                        if(Int(self.duration_str) != nil){
                            self.duration = Int(self.duration_str)!
                            let ds = DynamicSchedule(name: self.name, category: self.category_options[category_chosen], duration: self.duration, from: self.start, to: self.end)
                            ScheduleUtils.getOptimalSchedule(feature: ds, completion: { (status, schedule_list) in
                                if(status){
                                    for schedule in schedule_list{
                                        self.schedules.append(schedule)
                                        ScheduleUtils.addStorage(schedule: schedule, local_container: self.schedule_local, completion: {
                                            (status) -> Void in
                                            if(status){
                                                print("Server synchronization succeeded!")
                                            }
                                            self.isPresented = false
                                        })
                                    }
                                }
                            })
                        }
                        else{
                            print("Incorrect input format!")
                        }
                    }
                    self.isPresented = false
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
        })
    }
}

struct AddDynamicView_Previews: PreviewProvider {
    static var previews: some View {
        AddDynamicView(schedules: .constant([]), schedule_local: .constant(ScheduleContainer()), isPresented: .constant(true))
    }
}
