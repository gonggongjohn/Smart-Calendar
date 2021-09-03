//
//  CalendarBoardView.swift
//  SmartCalendar
//

import SwiftUI
import ElegantCalendar

struct CalendarBoardView: View {
    @Binding var schedules: [Schedule]
    @Binding var schedule_local: ScheduleContainer
    let start_date = Date().addingTimeInterval(TimeInterval(86400 * -30 * 6))
    let end_date = Date().addingTimeInterval(TimeInterval(86400 * 30 * 6))
    @ObservedObject var calendar_manager: MonthlyCalendarManager
    
    init(schedules: Binding<[Schedule]>, schedule_local: Binding<ScheduleContainer>) {
        _schedules = schedules
        _schedule_local = schedule_local
        self.calendar_manager = MonthlyCalendarManager(configuration: CalendarConfiguration(startDate: start_date, endDate: end_date), initialMonth: Date())
        self.calendar_manager.datasource = CalendarDataSource(schedules: $schedules, schedule_local: $schedule_local)
        
    }
    
    var body: some View {
        VStack{
            MonthlyCalendarView(calendarManager: calendar_manager)
            .horizontal()
                .background(Color(.systemGray))
        }
    }
}

struct CalendarBoardView_Previews: PreviewProvider {
    static var previews: some View {
        CalendarBoardView(schedules: .constant([]), schedule_local: .constant(ScheduleContainer()))
    }
}

struct CalendarDataSource: MonthlyCalendarDataSource{
    @Binding var schedules: [Schedule]
    @Binding var schedule_local: ScheduleContainer
    func calendar(viewForSelectedDate date: Date, dimensions size: CGSize) -> AnyView {
        AnyView(
            ScheduleView(schedules: $schedules, schedule_local: $schedule_local, date: date)
        )
    }
}
