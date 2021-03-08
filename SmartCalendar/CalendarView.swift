//
//  CalendarView.swift
//  SmartCalendar
//

import SwiftUI

struct CalendarView: View {
    @State private var schedules: [Schedule] = []
    @State private var isAddPresented: Bool = false
    
    var body: some View {
        
        VStack{
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
    var startTime: Date
    var endTime: Date
}

struct ScheduleRow: View {
    var schedule: Schedule
    
    var body: some View {
        VStack{
            Text("日程名称：\(schedule.name)")
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
    @State private var startTime: Date = Date()
    @State private var endTime: Date = Date()
    @Binding var schedules: [Schedule]
    @Binding var isPresented: Bool
    
    var body: some View {
        VStack{
            HStack{
                Text("日程名称：")
                    .font(.title3)
                TextField("Schedule Name", text: $scheduleName)
                    .font(.title3)
            }
            DatePicker("开始时间：", selection: $startTime)
            DatePicker("结束时间：", selection: $endTime)
            HStack{
                Button(action: {
                    if self.scheduleName != "" {
                        let schedule = Schedule(name: self.scheduleName, startTime: self.startTime, endTime: self.endTime)
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

func date2str(date: Date) -> String{
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
    return dateFormatter.string(from: date)
}

struct CalendarView_Previews: PreviewProvider {
    static var previews: some View {
        CalendarView()
    }
}
