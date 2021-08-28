//
//  UserView.swift
//  SmartCalendar
//


import SwiftUI

struct UserView: View {
    @State private var selectedIndex = 0
    
    init() {
    }
    
    var body: some View {
        NavigationView{
            Form {
                NavigationLink(destination: AccountView()){
                    Text("我的账户")
                }
                NavigationLink(destination: SettingView()){
                    Text("系统设置")
                }
                NavigationLink(destination: AboutView()){
                    Text("关于")
                }
                
            }
        }
    }
}

struct UserView_Previews: PreviewProvider {
    static var previews: some View {
        UserView()
    }
}
