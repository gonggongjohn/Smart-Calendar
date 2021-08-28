//
//  ProfileCollectView.swift
//  SmartCalendar
//

import SwiftUI

struct ProfileCollectView: View {
    @ObservedObject private var user_info: UserInfo
    @State private var nickname: String = ""
    @State private var occupation_chosen_index: Int = -1
    @State private var occupation_options: [IdNameRow] = []
    @State private var toggle_majorSheet = false
    @State private var toggle_schoolSheet = false
    
    init() {
        let info_local = StorageUtils.getUserInfo()
        if(info_local != nil){
            self.user_info = info_local!
        }
        else{
            self.user_info = UserInfo(username: "", password: "")
        }
    }
    
    var body: some View {
        VStack{
            Text("基本信息")
                .font(.title)
            ScrollView {
                HStack{
                    Text("昵称：")
                        .font(.title3)
                    TextField("Nickname...", text: $nickname)
                        .padding()
                        .onChange(of: self.nickname, perform: { content in
                            self.user_info.nickname = content
                        })
                }
                HStack{
                    Text("职业：")
                        .font(.title3)
                    Spacer()
                    Picker(selection: $occupation_chosen_index, label: Text("\(occupation_chosen_index == -1 ? "Occupation" : occupation_options[occupation_chosen_index].contentName)")){
                        if(self.occupation_options.count > 0){
                            ForEach(0 ..< self.occupation_options.count) { i in
                                Text("\(self.occupation_options[i].contentName)")
                                    .font(.title3)
                                    .tag(i+1)
                            }
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                    .onChange(of: self.occupation_chosen_index, perform: { index in
                        self.user_info.occupation = occupation_options[occupation_chosen_index]
                    })
                }
                Spacer()
                
                if(occupation_chosen_index >= 0 && occupation_options[occupation_chosen_index].contentName == "学生"){
                    HStack{
                        Text("所在专业：")
                            .font(.title3)
                        Spacer()
                        Button(action:{
                            self.toggle_majorSheet = true
                        }){
                            Text(self.user_info.major == nil ? "Major" : "\(self.user_info.major!.contentName)")
                        }.sheet(isPresented: $toggle_majorSheet){
                            MajorSelectView(major_chosen: $user_info.major)
                            
                            Button(action: {
                                self.toggle_majorSheet = false
                            }){
                                Text("确定")
                                    .font(.title2)
                                    .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                                    .background(RoundedRectangle(cornerRadius: 10.0)
                                                    .stroke(Color.black, lineWidth: 2.0))
                            }
                        }
                    }
                    Spacer()
                    HStack{
                        Text("所在院校：")
                            .font(.title3)
                        Spacer()
                        Button(action:{
                            self.toggle_schoolSheet = true
                        }){
                            Text(self.user_info.school == nil ? "School" : "\(self.user_info.school!.contentName)")
                        }.sheet(isPresented: $toggle_schoolSheet){
                            SchoolSelectView(school_chosen: $user_info.school)
                            
                            Button(action: {
                                self.toggle_schoolSheet = false
                            }){
                                Text("确定")
                                    .font(.title2)
                                    .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                                    .background(RoundedRectangle(cornerRadius: 10.0)
                                                    .stroke(Color.black, lineWidth: 2.0))
                            }
                        }
                    }
                }
                
            }.padding()
            Button(action: {
                UserUtils.updateInfo(info: self.user_info, completion: { status in
                    if(status){
                        DispatchQueue.main.async{
                            let delegate: UIWindowSceneDelegate? = {
                                var uiScreen: UIScene?
                                UIApplication.shared.connectedScenes.forEach {
                                    (screen) in
                                    uiScreen = screen
                                }
                                return uiScreen?.delegate as? UIWindowSceneDelegate
                            }()
                            delegate?.window!?.rootViewController = UIHostingController(rootView: MainView(lazy: false))
                        }
                    }
                })
            }){
                Text("完成")
                    .font(.title2)
                    .frame(maxWidth: 300, maxHeight: 50, alignment: .center)
                    .foregroundColor(.white)
                    .background(Color.blue)
                    .cornerRadius(15.0)
            }
        }.onAppear(perform: {
            UserUtils.getOccupation(completion: { (status, occupation_list) in
                if(status){
                    self.occupation_options = occupation_list
                }
            })
        })
    }
}

struct ProfileCollectView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileCollectView()
    }
}
