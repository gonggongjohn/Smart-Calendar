//
//  ProfileCollectView.swift
//  SmartCalendar
//

import SwiftUI

struct ProfileCollectView: View {
    @State private var occupation_chosen: Int = -1
    @State private var occupation_options: [(id: Int, name: String)] = []
    @State private var toggle_majorSheet = false
    @State private var major_chosen: (id: Int, name: String)?
    @State private var toggle_schoolSheet = false
    @State private var school_chosen: (id: Int, name: String)?
    
    var body: some View {
        VStack{
            Text("基本信息")
                .font(.title)
            ScrollView {
                HStack{
                    Text("职业：")
                        .font(.title3)
                    Spacer()
                    Picker(selection: $occupation_chosen, label: Text("\(occupation_chosen == -1 ? "Occupation" : occupation_options[occupation_chosen].name)")){
                        if(self.occupation_options.count > 0){
                            ForEach(0 ..< self.occupation_options.count) { i in
                                Text("\(self.occupation_options[i].name)")
                                    .font(.title3)
                                    .tag(i+1)
                            }
                        }
                    }
                    .pickerStyle(MenuPickerStyle())
                }
                Spacer()
                
                if(occupation_chosen >= 0 && occupation_options[occupation_chosen].name == "学生"){
                    HStack{
                        Text("所在专业：")
                            .font(.title3)
                        Spacer()
                        Button(action:{
                            self.toggle_majorSheet = true
                        }){
                            Text(self.major_chosen == nil ? "Major" : "\(self.major_chosen!.name)")
                        }.sheet(isPresented: $toggle_majorSheet){
                            MajorSelectView(major_chosen: $major_chosen)
                            
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
                            Text(self.school_chosen == nil ? "School" : "\(self.school_chosen!.name)")
                        }.sheet(isPresented: $toggle_schoolSheet){
                            SchoolSelectView(school_chosen: $school_chosen)
                            
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
            /*
            
            UserUtils.getSchool(completion: { (status, school_list) in
                if(status){
                    self.school_options = school_list
                }
            })
             */
        })
    }
}

struct ProfileCollectView_Previews: PreviewProvider {
    static var previews: some View {
        ProfileCollectView()
    }
}
