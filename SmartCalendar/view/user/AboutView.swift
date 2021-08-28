//
//  AboutView.swift
//  SmartCalendar
//

import SwiftUI

struct AboutView: View {
    var body: some View {
        VStack{
            Text("开发团队：时间管理大师")
                .font(.title)
                .fontWeight(.semibold)
            Spacer().frame(maxHeight: 30, alignment: .center)
            Text("iOS端: GONGGONGJOHN, Niya0515")
                .font(.title3)
            Text("Android端: Joker, Jankeeeeee")
                .font(.title3)
            Text("服务器端: GONGGONGJOHN")
                .font(.title3)
            Text("联系我们: gonggongjohn@163.com")
                .font(.title3)
            
        }
    }
}

struct AboutView_Previews: PreviewProvider {
    static var previews: some View {
        AboutView()
    }
}
