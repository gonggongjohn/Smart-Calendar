//
//  UserView.swift
//  SmartCalendar
//


import SwiftUI

struct DetailView1: View {
    var body: some View {
        Text("Hello1")
    }
}

struct UserView: View {
    @State private var selectedIndex = 0
    var body: some View {
        NavigationView{
            Form {
                NavigationLink(destination: DetailView1()){
                    Text("个人信息")
                }
                NavigationLink(destination: DetailView1()){
                    Text("系统设置")
                }
                NavigationLink(destination: DetailView1()){
                    Text("其他")
                }
                
            }
            .navigationBarTitle(Text("我的"))//displayMode:.inline
        }
    
    }
}

struct UserView_Previews: PreviewProvider {
    static var previews: some View {
        UserView()
    }
}
