//
//  AddNormalView.swift
//  SmartCalendar
//

import SwiftUI

struct AddNormalView: View {
    @State private var name: String = ""
    @State private var category: String = ""
    @State private var category_chosen: Int = 0
    @State private var category_options: [(id: Int, name: String)] = []
    @State private var start: Date = Date()
    @State private var end: Date = Date()
    @Binding var schedules: [Schedule]
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
                Picker(selection: $category_chosen, label: Text("Category")) {
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
            DatePicker("开始时间：", selection: $start)
            DatePicker("结束时间：", selection: $end)
            
            HStack{
                Button(action: {
                    if self.name != "" {
                        print(self.category_chosen)
                        let schedule = Schedule(name: self.name, categoryId: self.category_options[self.category_chosen].id, categoryName: self.category_options[self.category_chosen].name, start: self.start, end: self.end)
                        self.schedules.append(schedule)
                        ScheduleUtils.addToServer(schedule: schedule, completion: {
                            (status) -> Void in
                            if(status == 1){
                                print("Server synchronization succeeded!")
                            }
                        })
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
        }.frame(maxHeight: 200)
        .padding()
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

struct AddNormalView_Previews: PreviewProvider {
    static var previews: some View {
        AddNormalView(schedules: .constant([]), isPresented: .constant(true))
    }
}
