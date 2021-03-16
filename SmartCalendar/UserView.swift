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
                    Text("我的账户")
                }
                NavigationLink(destination: DetailView1()){
                    Text("系统设置")
                }
                NavigationLink(destination: DetailView1()){
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
