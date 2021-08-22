//
//  ContentView.swift
//  SmartCalendar
//

import SwiftUI

struct LoginView: View {
    @State var username: String = ""
    @State var password: String = ""
    @State var showRegSuccessInfo = false
    @State var showRegFailInfo = false
    @State var loadingFlag = false
    var body: some View {
        NavigationView{
            ZStack {
                VStack {
                    Text("Smart Calendar")
                        .font(.title)
                        .fontWeight(.semibold)
                    Spacer().frame(maxHeight: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                    VStack{
                        HStack{
                            Text("用户名：")
                                .font(.title3)
                                .frame(width: 90, alignment: .leading)
                            TextField("Username", text: $username)
                                .font(.title3)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                        }
                        HStack{
                            Text("密码：")
                                .font(.title3)
                                .frame(width: 90, alignment: .leading)
                            SecureField("Password", text: $password)
                                .font(.title3)
                                .textFieldStyle(RoundedBorderTextFieldStyle())
                        }
                    }.padding()
                    .frame(maxHeight: 200, alignment: .center)
                    .background(Color.green.opacity(0.8))
                    .cornerRadius(15.0)
                    Spacer().frame(maxHeight: 50, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                    VStack{
                        LoginButton(username: $username, password: $password, loadingFlag: $loadingFlag)
                        Spacer().frame(maxHeight: 20, alignment: /*@START_MENU_TOKEN@*/.center/*@END_MENU_TOKEN@*/)
                        JumpRegisterButton()
                    }
                }.padding()
                .disabled(self.loadingFlag)
                .blur(radius: self.loadingFlag ? 3 : 0)
                
                if(self.loadingFlag){
                    VStack{
                        ProgressView()
                    }.frame(maxWidth: 200, maxHeight: 200)
                    .background(Color.secondary.colorInvert())
                    .cornerRadius(20.0)
                }
            }
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}

struct LoginButton: View{
    @State private var isLoginSuccess = false
    @State var showLoginFailInfo = false
    @State var statusCode = 0
    @Binding var username: String
    @Binding var password: String
    @Binding var loadingFlag: Bool
    
    var body: some View {
        Button(action: {
            if(self.username == "" || self.password == ""){
                self.showLoginFailInfo = true
                self.statusCode = 9
            }
            else{
                self.loadingFlag = true
                UserUtils.login(username: self.username, password: self.password, completion: {
                    (status) -> Void in
                    self.loadingFlag = false
                    if (status == 1) {
                        self.isLoginSuccess = true
                        StorageUtils.saveUserInfo(username: self.username, password: self.password)
                        DispatchQueue.main.sync{
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
                    else{
                        self.showLoginFailInfo = true
                        self.statusCode = status
                    }
                })
            }
        }){
            Text("登陆")
                .font(.title2)
                .frame(maxWidth: 300, maxHeight: 50, alignment: .center)
                .foregroundColor(.white)
                .background(Color.blue)
                .cornerRadius(15.0)
        }.alert(isPresented: $showLoginFailInfo){
            if(self.statusCode == 2){
                return Alert(title: Text("登录失败"), message: Text("密码错误！"), dismissButton: .default(Text("确定")))
            }
            else if(self.statusCode == 3){
                return Alert(title: Text("登录失败"), message: Text("用户名不存在！"), dismissButton: .default(Text("确定")))
            }
            else if(self.statusCode == 9){
                return Alert(title: Text("登录失败"), message: Text("用户名和密码不能为空！"), dismissButton: .default(Text("确定")))
            }
            else{
                return Alert(title: Text("登录失败"), message: Text("未知错误"), dismissButton: .default(Text("确定")))
            }
        }
    }
}

struct JumpRegisterButton: View{
    @State var jumpFlag = false
    var body: some View{
        NavigationLink(
            destination: RegisterView(),
            isActive: $jumpFlag){
            Button(action: {
                self.jumpFlag = true
            }){
                Text("注册")
                    .frame(maxWidth: 300, maxHeight: 50, alignment: .center)
                    .background(RoundedRectangle(cornerRadius: 10).stroke())
            }
        }
    }
}
 
