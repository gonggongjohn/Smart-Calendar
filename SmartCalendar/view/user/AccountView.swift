//
//  AccountView.swift
//  SmartCalendar
//

import SwiftUI

struct AccountView: View {
    @ObservedObject private var user_info: UserInfo
    @State private var toggle_occupationSheet = false
    @State private var toggle_majorSheet = false
    @State private var toggle_schoolSheet = false
    
    init() {
        let info_local = StorageUtils.getUserInfo()
        if(info_local != nil){
            user_info = info_local!
        }
        else{
            user_info = UserInfo(username: "", password: "")
        }
    }
    
    var body: some View {
        VStack{
            List{
                HStack{
                    Image("schedule_stat_icon").resizable().aspectRatio(contentMode: .fit)
                    VStack{
                        Text("账号：\(self.user_info.getUsername())")
                        Text("昵称：\(self.user_info.nickname == nil ? "No Nickname" : self.user_info.nickname!)")
                    }
                }.frame(maxWidth: 300, maxHeight: 100)
                
                HStack{
                    Text("职业")
                    Spacer()
                    Button(action:{
                        self.toggle_occupationSheet = true
                    }){
                        Text("\(self.user_info.occupation == nil ? "Occupation" : self.user_info.occupation!.contentName)")
                    }.sheet(isPresented: $toggle_occupationSheet){
                        OccupationSelectView(occupation_chosen: $user_info.occupation)
                        UserInfoSubmitButton(toggle: $toggle_occupationSheet, info: user_info)
                    }
                }
                
                HStack{
                    Text("专业")
                    Spacer()
                    Button(action:{
                        self.toggle_majorSheet = true
                    }){
                        Text("\(self.user_info.major == nil ? "Major" : self.user_info.major!.contentName)")
                    }.sheet(isPresented: $toggle_majorSheet){
                        MajorSelectView(major_chosen: $user_info.major)
                        UserInfoSubmitButton(toggle: $toggle_majorSheet, info: user_info)
                    }
                }
                
                HStack{
                    Text("院校")
                    Spacer()
                    Button(action:{
                        self.toggle_schoolSheet = true
                    }){
                        Text("\(self.user_info.school == nil ? "School" : self.user_info.school!.contentName)")
                    }.sheet(isPresented: $toggle_schoolSheet){
                        SchoolSelectView(school_chosen: $user_info.school)
                        UserInfoSubmitButton(toggle: $toggle_schoolSheet, info: user_info)
                    }
                }
            }
        }.padding()
        .onAppear(perform: {
            UserUtils.getInfo(completion: {
                (status, info) -> Void in
                if(status){
                    DispatchQueue.main.async {
                        self.user_info.nickname = info!["nickname"]!
                    }
                }
            })
        })
    }
}

struct OccupationSelectView: View{
    @State private var occupation_chosen_index: Int = -1
    @State private var occupation_options: [IdNameRow] = []
    @Binding var occupation_chosen: IdNameRow?
    
    var body: some View{
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
                self.occupation_chosen = occupation_options[occupation_chosen_index]
            })
        }.padding()
        .onAppear(perform: {
            UserUtils.getOccupation(completion: { (status, occupation_list) in
                if(status){
                    self.occupation_options = occupation_list
                }
            })
        })
    }
}

struct UserInfoSubmitButton: View{
    @Binding var toggle: Bool
    var info: UserInfo
    var body: some View{
        Button(action: {
            UserUtils.updateInfo(info: info, completion: { (status) in
                if(status){
                    print("Synchronization succeeded!")
                }
            })
            self.toggle = false
        }){
            Text("确定")
                .font(.title2)
                .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                .background(RoundedRectangle(cornerRadius: 10.0)
                                .stroke(Color.black, lineWidth: 2.0))
        }
    }
}

struct AccountView_Previews: PreviewProvider {
    static var previews: some View {
        AccountView()
    }
}
