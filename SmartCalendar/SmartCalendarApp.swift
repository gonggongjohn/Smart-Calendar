//
//  SmartCalendarApp.swift
//  SmartCalendar
//

import SwiftUI

@main
struct SmartCalendarApp: App {
    @State var is_login = false
    init() {
        let info = StorageUtils.getUserInfo()
        if(info != nil){
            _is_login = State(initialValue: true)
        }
    }
    
    var body: some Scene {
        WindowGroup {
            if(self.is_login){
                MainView(lazy: true)
            }
            else{
                LoginView()
            }
        }
    }
}
