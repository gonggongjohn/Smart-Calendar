//
//  ContentView.swift
//  SmartCalendar
//

import SwiftUI

struct LoginView: View {
    @State var username: String = ""
    @State var password: String = ""
    @State private var isLoginSuccess = false
    @State var showLoginFailInfo = false
    @State var showRegSuccessInfo = false
    @State var showRegFailInfo = false
    @State var loadingFlag = false
    var body: some View {
        NavigationView{
            VStack(){
                Text("Smart Calendar")
                    .font(.title)
                    .fontWeight(.semibold)
                Spacer().frame(maxHeight: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                VStack{
                    HStack{
                        Text("用户名：")
                            .font(.title3)
                            .fontWeight(.semibold)
                            .frame(width: 90, alignment: .leading)
                        TextField("Username", text: $username)
                            .font(.title3)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                    HStack{
                        Text("密码：")
                            .font(.title3)
                            .fontWeight(.semibold)
                            .frame(width: 90, alignment: .leading)
                        SecureField("Password", text: $password)
                            .font(.title3)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                }.padding()
                .frame(maxHeight: 200, alignment: .center)
                .background(Color.green)
                .cornerRadius(15.0)
                Spacer().frame(maxHeight: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                VStack{
                    if self.loadingFlag {
                        ProgressView()
                    }
                    Button(action: {
                        self.loadingFlag = true
                        UserUtils.login(username: self.username, password: self.password, completion: {
                            (status) -> Void in
                            self.loadingFlag = false
                            if status {
                                self.isLoginSuccess = true
                                DispatchQueue.main.sync{
                                    let delegate: UIWindowSceneDelegate? = {
                                        var uiScreen: UIScene?
                                        UIApplication.shared.connectedScenes.forEach {
                                            (screen) in
                                            uiScreen = screen
                                        }
                                        return uiScreen?.delegate as? UIWindowSceneDelegate
                                    }()
                                    delegate?.window!?.rootViewController = UIHostingController(rootView: MainView(username: self.username))
                                }
                            }
                            else{
                                self.showLoginFailInfo = true
                            }
                        })
                    }){
                        Text("登陆")
                            .font(.title2)
                            .frame(maxWidth: 300, maxHeight: 50, alignment: .center)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .cornerRadius(15.0)
                    }.alert(isPresented: $showLoginFailInfo){
                        Alert(title: Text("用户登录系统"), message: Text("登录失败！"), dismissButton: .default(Text("确定")))
                    }
                    Spacer().frame(maxHeight: 20)
                    Button(action: {
                        self.loadingFlag = true
                        UserUtils.register(username: self.username, password: self.password, completion: {
                            (status) -> Void in
                            self.loadingFlag = false
                            if status {
                                self.showRegSuccessInfo = true
                            }
                            else{
                                self.showRegFailInfo = true
                            }
                        })
                    }){
                        Text("注册")
                            .font(.title2)
                            .frame(maxWidth: 300, maxHeight: 50, alignment: .center)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .cornerRadius(15.0)
                    }
                    .alert(isPresented: $showRegSuccessInfo){
                        Alert(title: Text("用户登录系统"), message: Text("注册成功！"), dismissButton: .default(Text("确定")))
                    }
                    .alert(isPresented: $showRegFailInfo){
                        Alert(title: Text("用户登录系统"), message: Text("注册失败！"), dismissButton: .default(Text("确定")))
                    }
                }
            }.padding()
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
