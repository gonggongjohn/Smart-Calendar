//
//  ScheduleRow.swift
//  SmartCalendar
//

import SwiftUI

struct ScheduleRow: View {
    var schedule: Schedule
    
    var body: some View {
        HStack{
            VStack{
                Text("日程名称：\(schedule.name)")
                Text("日程类别：\(schedule.category.name)")
                Text("开始时间：\(DateUtils.date2str(date: schedule.start))")
                Text("结束时间：\(DateUtils.date2str(date: schedule.end))")
                if(self.schedule.position != nil){
                    Text("位置: \(schedule.position!.name)")
                }
            }.padding(.all)
            .background(
                HStack{
                    RoundedRectangle(cornerRadius: 8)
                            .fill(Color(.systemBlue))
                            .frame(width: 5)
                    Spacer()
                }
            )
            .cornerRadius(20.0)
            .shadow(radius: 2)
        }
        /*
        VStack{
            
            Text("日程名称：\(schedule.name)")
            Text("日程类别：\(schedule.category.name)")
            Text("开始时间：\(DateUtils.date2str(date: schedule.start))")
            Text("结束时间：\(DateUtils.date2str(date: schedule.end))")
            if(self.schedule.position != nil){
                Text("位置: \(schedule.position!.name)")
            }
        }
        .padding(.all)
        .background(/*@START_MENU_TOKEN@*//*@PLACEHOLDER=View@*/Color.green/*@END_MENU_TOKEN@*/)
        .cornerRadius(20.0)
        .shadow(radius: 3)
 */
    }
}

struct ScheduleRow_Previews: PreviewProvider {
    static var previews: some View {
        ScheduleRow(schedule: Schedule(name: "12424", categoryId: 1, categoryName: "学习", start: Date(), end: Date(), pos: nil))
    }
}
