//
//  MainView.swift
//  SmartCalendar
//

import SwiftUI

struct MainView: View {
    private let username: String
    //private let geoUtils: GeoUtils
    
    init(username: String) {
        self.username = username
        /*
        self.geoUtils = GeoUtils()
        self.geoUtils.initGeoTrace()
        self.geoUtils.startTrace()
         */
    }
    
    var body: some View {
        VStack{
            /*
            Button(action: {
                self.geoUtils.stopTrace()
            }){
                Text("停止定位")
                    .font(.title2)
            }
             */
            TabView {
                CalendarView(username: self.username).tabItem {
                    Image(systemName: "calendar")
                    Text("日程")
                }
                StatisticView().tabItem {
                    Image(systemName: "newspaper")
                    Text("统计")
                }
                UserView(username: self.username).tabItem {
                    Image(systemName: "person")
                    Text("我的")
                }
            }
        }
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView(username: "")
    }
}
