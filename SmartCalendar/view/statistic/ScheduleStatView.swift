//
//  ScheduleStatView.swift
//  SmartCalendar
//

import SwiftUI
import SwiftUICharts

struct ScheduleStatView: View {
    var chartData: ChartData
    var schedules: [ScheduleItem] = []
    
    init() {
        chartData = ChartData(values: [("作息", 65), ("课程", 60), ("娱乐", 26), ("其他", 24)])
        schedules.append(ScheduleItem(name: "睡眠", category: "作息", last: 45))
        schedules.append(ScheduleItem(name: "三餐", category: "作息", last: 20))
        schedules.append(ScheduleItem(name: "休闲娱乐", category: "娱乐", last: 16))
        schedules.append(ScheduleItem(name: "外出活动", category: "娱乐", last: 10))
        schedules.append(ScheduleItem(name: "操作系统 课程", category: "课程", last: 5))
        schedules.append(ScheduleItem(name: "数据结构 课程", category: "课程", last: 5))
        schedules.append(ScheduleItem(name: "概率论与数理统计 课程", category: "课程", last: 4))
        schedules.append(ScheduleItem(name: "离散数学 课程", category: "课程", last: 4))
        schedules.append(ScheduleItem(name: "数据科学与工程数学基础 课程", category: "课程", last: 4))
        schedules.append(ScheduleItem(name: "数据伦理 课程", category: "课程", last: 2))
    }
    var body: some View {
        VStack{
            BarChartView(data: chartData, title: "日程分布", legend: "Quarterly", form: ChartForm.extraLarge)
            List{
                ForEach(self.schedules){ schedule in
                    ScheduleItemRow(item: schedule)
                }
            }
        }
    }
}

struct ScheduleItem: Identifiable {
    var id = UUID()
    var name: String
    var category: String
    var last: Int
}

struct ScheduleItemRow: View {
    var item: ScheduleItem
    
    var body: some View{
        HStack{
            Text("日程：\(item.name)")
            Spacer()
            Text("类别：\(item.category)")
            Spacer()
            Text("时长：\(item.last) 小时")
        }
        .padding(.all)
        .background(/*@START_MENU_TOKEN@*//*@PLACEHOLDER=View@*/Color.green/*@END_MENU_TOKEN@*/)
        .cornerRadius(20.0)
        .shadow(radius: 3)
    }
}

struct ScheduleStatView_Previews: PreviewProvider {
    static var previews: some View {
        ScheduleStatView()
    }
}
