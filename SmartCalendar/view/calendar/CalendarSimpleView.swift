//
//  CalendarSimpleView.swift
//  SmartCalendar
//

import SwiftUI
import Photos

struct CalendarSimpleView: View {
    @State private var username: String = ""
    @State private var toggle_addSchedule: Bool = false
    @State private var schedules: [Schedule] = []
    @State private var schedule_local = ScheduleContainer()
    @State private var data_loaded: Bool = false
    @ObservedObject private var history = GeoHistory()
    @State private var phaseFlag = false
    @State private var loadingFlag = false
    @State var assetList: [PHAsset] = []
    @ObservedObject var geoDict = HistoryDateWrapper()
    
    var body: some View {
        ZStack{
            VStack{
                HStack{
                    Button(action: {
                        self.toggle_addSchedule = true
                    }){
                        Text("添加日程")
                            .font(.title3)
                            .padding()
                            .background(RoundedRectangle(cornerRadius: 10.0)
                                            .stroke(Color.black, lineWidth: 2.0))
                    }
                    Spacer()
                    EditButton()
                }.padding()
                
                List{
                    ForEach(self.schedules.sorted(by: { $0.start <= $1.start })){ schedule in
                        HStack{
                            ScheduleRow(schedule: schedule)
                            Button(action: {
                                ScheduleUtils.removeStorage(schedule: schedule, local_container: self.schedule_local, completion: { status in
                                    if(status){
                                        let duration = Int((schedule.end.timeIntervalSince1970 - schedule.start.timeIntervalSince1970) / 3600)
                                        let feature = DynamicSchedule(name: schedule.name, category: schedule.category, duration: duration, from: schedule.start.addingTimeInterval(TimeInterval(86300)), to: schedule.start.addingTimeInterval(TimeInterval(864000)))
                                        self.schedules.remove(at: self.schedules.firstIndex(of: schedule)!)
                                        ScheduleUtils.getOptimalSchedule(feature: feature, completion: { (status, schedule_list) in
                                            if(status){
                                                for schedule_new in schedule_list{
                                                    self.schedules.append(schedule_new)
                                                    ScheduleUtils.addStorage(schedule: schedule_new, local_container: self.schedule_local, completion: { status in
                                                        if(status){
                                                            print("Synchronization succeeded!")
                                                        }
                                                    })
                                                }
                                            }
                                        })
                                    }
                                })
                            }){
                                Text("推迟")
                            }
                        }
                    }.onDelete(perform: { indexSet in
                        indexSet.forEach({
                            (index) -> Void in
                            let schedule = self.schedules[index]
                            ScheduleUtils.removeStorage(schedule: schedule, local_container: self.schedule_local, completion: {
                                (status) -> Void in
                                if(status){
                                    print("Remove from server succeeded!")
                                }
                            })
                        })
                        self.schedules.remove(atOffsets: indexSet)
                    })
                }
            }.disabled(self.toggle_addSchedule)
            .blur(radius: self.toggle_addSchedule ? 3.0 : 0.0)
            .onAppear(perform: {
                if(!self.data_loaded){
                    ScheduleUtils.getStorage(local_container: self.schedule_local, completion: {
                        (status, schedule_list) -> Void in
                        if(status){
                            self.schedules = schedule_list
                            self.data_loaded = true
                        }
                    })
                }
            })
            if(self.toggle_addSchedule){
                AddScheduleView(schedules: $schedules, schedule_local: $schedule_local, isPresented: $toggle_addSchedule)
            }
        }
    }
}

struct CalendarSimpleView_Previews: PreviewProvider {
    static var previews: some View {
        CalendarSimpleView()
    }
}
