//
//  AccountView.swift
//  SmartCalendar
//

import SwiftUI

struct AccountView: View {
    @State private var username: String = ""
    @State private var nickname: String = ""
    
    init() {
    }
    
    var body: some View {
        HStack{
            Image("schedule_stat_icon").resizable().aspectRatio(contentMode: .fit)
            VStack{
                Text("账号：\(self.username)")
                Text("昵称：\(self.nickname)")
            }
        }.frame(maxWidth: 300, maxHeight: 100, alignment: .leading)
        .padding()
        .onAppear(perform: {
            UserUtils.getInfo(completion: {
                (status, info) -> Void in
                if(status){
                    self.username = info!["username"]!
                    self.nickname = info!["nickname"]!
                }
            })
        })
    }
}

struct AccountView_Previews: PreviewProvider {
    static var previews: some View {
        AccountView()
    }
}
