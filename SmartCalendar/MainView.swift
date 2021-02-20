//
//  MainView.swift
//  SmartCalendar
//

import SwiftUI

struct MainView: View {
    var body: some View {
        TabView {
            CalendarView().tabItem {
                Image(systemName: "calendar")
                Text("日程")
            }
            StatisticView().tabItem {
                Image(systemName: "newspaper")
                Text("统计")
            }
            UserView().tabItem {
                Image(systemName: "person")
                Text("我的")
            }
        }
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView()
    }
}
