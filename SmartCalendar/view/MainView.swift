//
//  MainView.swift
//  SmartCalendar
//

import SwiftUI

struct MainView: View {
    @State var lazy_flag: Bool = false
    @State var loading_flag: Bool = false
    @State var complete_flag: Bool = false
    
    init(lazy: Bool) {
        _lazy_flag = State(initialValue: lazy)
    }
    
    var body: some View {
        ZStack{
            if(self.complete_flag){
                VStack{
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
                }.disabled(self.loading_flag)
                .blur(radius: self.loading_flag ? 3 : 0)
            }
            
            if(self.loading_flag){
                VStack{
                    ProgressView()
                }.frame(maxWidth: 200, maxHeight: 200)
                .background(Color.secondary.colorInvert())
                .cornerRadius(20.0)
            }
        }.onAppear(perform: {
            if(self.lazy_flag){
                self.loading_flag = true
                let info_wrapper = StorageUtils.getUserInfo()
                if(info_wrapper != nil){
                    UserUtils.login(username: info_wrapper!.username, password: info_wrapper!.password, completion: {
                        (status) -> Void in
                        if(status == 1){
                            print("Lazy login succeeded!")
                            self.complete_flag = true
                        }
                        else{
                            DispatchQueue.main.async{
                                let delegate: UIWindowSceneDelegate? = {
                                    var uiScreen: UIScene?
                                    UIApplication.shared.connectedScenes.forEach {
                                        (screen) in
                                        uiScreen = screen
                                    }
                                    return uiScreen?.delegate as? UIWindowSceneDelegate
                                }()
                                delegate?.window!?.rootViewController = UIHostingController(rootView: LoginView())
                            }
                        }
                        self.loading_flag = false
                    })
                }
            }
            else{
                self.complete_flag = true
            }
        })
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView(lazy: false)
    }
}
