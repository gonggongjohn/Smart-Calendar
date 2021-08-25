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
        VStack{
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
            
            Button(action: {
                DispatchQueue.main.async{
                    let delegate: UIWindowSceneDelegate? = {
                        var uiScreen: UIScene?
                        UIApplication.shared.connectedScenes.forEach {
                            (screen) in
                            uiScreen = screen
                        }
                        return uiScreen?.delegate as? UIWindowSceneDelegate
                    }()
                    delegate?.window!?.rootViewController = UIHostingController(rootView: ProfileCollectView())
                }
            }){
                Text("修改基本信息")
                    .font(.title2)
                    .frame(maxWidth: 300, maxHeight: 50, alignment: .center)
                    .foregroundColor(.white)
                    .background(Color.blue)
                    .cornerRadius(15.0)
            }
        }
    }
}

struct AccountView_Previews: PreviewProvider {
    static var previews: some View {
        AccountView()
    }
}
