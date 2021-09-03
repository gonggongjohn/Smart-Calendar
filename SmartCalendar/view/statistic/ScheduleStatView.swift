//
//  ScheduleStatView.swift
//  SmartCalendar
//

import SwiftUI

struct ScheduleStatView: View {
    @State var chart_data: [(String, Double)] = []
    @State var schedules: [ScheduleStatItem] = []
    
    init() {
        /*
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
 */
    }
    var body: some View {
        VStack{
            if(self.chart_data.count > 0){
                /*
                BarChartView(data: chart_data!, title: "日程分布", legend: "Quarterly", form: ChartForm.extraLarge)
 */
                BarChart()
                    .data(chart_data)
                    .chartStyle(ChartStyle(backgroundColor: .accentColor, foregroundColor: ColorGradient(.blue, .purple)))
            }
            List{
                if(self.schedules.count > 0){
                    ForEach(self.schedules){ schedule in
                        ScheduleItemRow(item: schedule)
                    }
                }
            }
        }
        .onAppear(perform: {
            let schedule_local = StorageUtils.getScheduleFromLocal()
            if(schedule_local != nil){
                var stat_dict: [String: Double] = [:]
                for schedule in schedule_local!.schedules.values{
                    if(stat_dict[schedule.category.name] == nil){
                        stat_dict[schedule.category.name] = 0.0
                    }
                    let duration = (schedule.end.timeIntervalSince1970 - schedule.start.timeIntervalSince1970) / 3600.0
                    stat_dict[schedule.category.name]! += duration
                }
                var stat_list: [(String, Double)] = []
                self.schedules = []
                for (title, stat) in stat_dict{
                    stat_list.append((title, stat))
                    self.schedules.append(ScheduleStatItem(name: title, category: title, duration: stat))
                }
                self.chart_data = stat_list
            }
        })
    }
}

struct ScheduleStatItem: Identifiable {
    var id = UUID()
    var name: String
    var category: String
    var duration: Double
}

struct ScheduleItemRow: View {
    var item: ScheduleStatItem
    
    var body: some View{
        VStack{
            Text("类别：\(item.category)")
            Spacer()
            Text("总计时长：\(String(format: "%.2f", item.duration)) 小时")
        }
        .padding(.all)
        .background(
            HStack{
                RoundedRectangle(cornerRadius: 8)
                        .fill(Color(.systemGreen))
                        .frame(width: 5)
                Spacer()
            }
        )
        .cornerRadius(20.0)
        .shadow(radius: 2)
    }
}

struct ScheduleStatView_Previews: PreviewProvider {
    static var previews: some View {
        ScheduleStatView()
    }
}
