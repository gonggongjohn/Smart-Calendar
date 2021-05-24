//
//  AccountView.swift
//  SmartCalendar
//

import SwiftUI

struct AccountView: View {
    private var username: String
    private var nickname: String
    
    init(username: String) {
        self.username = username
        self.nickname = ""
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
    }
}

struct AccountView_Previews: PreviewProvider {
    static var previews: some View {
        AccountView(username: "")
    }
}
