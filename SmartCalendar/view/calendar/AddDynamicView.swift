//
//  AddAmbiguousView.swift
//  SmartCalendar
//

import SwiftUI

struct AddDynamicView: View {
    @State private var name: String = ""
    @State private var category: String = ""
    @State private var category_chosen: Int = -1
    @State private var category_options: [(id: Int, name: String)] = []
    @State private var start: Date = Date()
    @State private var end: Date = Date()
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
            
            DatePicker("开始时间：", selection: $start)
                .font(.title3)
            DatePicker("截止时间：", selection: $end)
                .font(.title3)
            
            HStack{
                Button(action: {
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

struct AddAmbiguousView_Previews: PreviewProvider {
    static var previews: some View {
        AddDynamicView(schedules: .constant([]), schedule_local: .constant(ScheduleContainer()), isPresented: .constant(true))
    }
}
