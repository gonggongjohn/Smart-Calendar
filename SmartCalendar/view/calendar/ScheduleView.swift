//
//  ScheduleView.swift
//  SmartCalendar
//

import SwiftUI

struct ScheduleView: View {
    @Binding private var schedules: [Schedule]
    @State private var schedule_filter: [Schedule] = []
    @Binding var schedule_local: ScheduleContainer
    private var date: Date
    
    init(schedules: Binding<[Schedule]>, schedule_local: Binding<ScheduleContainer>, date: Date) {
        _schedules = schedules
        _schedule_local = schedule_local
        self.date = date
    }
    
    var body: some View {
        VStack{
            EditButton()
            List{
                ForEach(self.schedule_filter){ schedule in
                    ScheduleRow(schedule: schedule)
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
            }.onAppear(perform: {
                self.schedule_filter = self.schedules.filter {
                    ($0.start >= date) && ($0.start <= date.addingTimeInterval(TimeInterval(86400)))
                }
            })
            .onChange(of: self.schedules, perform: { origin in
                self.schedule_filter = self.schedules.filter { $0.start >= date && $0.end <= date.addingTimeInterval(TimeInterval(86400)) }
            })
        }
    }
}

struct ScheduleView_Previews: PreviewProvider {
    static var previews: some View {
        ScheduleView(schedules: .constant([]), schedule_local: .constant(ScheduleContainer()), date: Date())
    }
}
