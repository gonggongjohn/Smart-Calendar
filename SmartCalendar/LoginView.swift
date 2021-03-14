//
//  ContentView.swift
//  SmartCalendar
//

import SwiftUI

struct LoginView: View {
    private var userUtils = UserUtils()
    @State var username: String = ""
    @State var password: String = ""
    @State var isLoginSuccess = false
    @State var showLoginFailInfo = false
    @State var showRegSuccessInfo = false
    @State var showRegFailInfo = false
    var body: some View {
        NavigationView{
            VStack(spacing: 50.0){
                Text("Smart Calendar")
                    .font(.title)
                VStack{
                    HStack{
                        Text("用户名：")
                            .font(.title3)
                        TextField("Username", text: $username)
                            .font(.title3)
                    }
                    HStack{
                        Text("密码：")
                            .font(.title3)
                        SecureField("Password", text: $password)
                            .font(.title3)
                    }
                }
                HStack{
                    NavigationLink(
                        destination: MainView(),
                        isActive: $isLoginSuccess) {
                        Text("登陆")
                            .onTapGesture {
                                if self.userUtils.login(username: self.username, password: self.password) {
                                    self.isLoginSuccess = true
                                }
                                else{
                                    self.showLoginFailInfo = true
                                }
                            }
                            .font(.title2)
                            .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                            .background(RoundedRectangle(cornerRadius: 10.0)
                                            .stroke(Color.black, lineWidth: 2.0))
                    }
                    .alert(isPresented: $showLoginFailInfo){
                        Alert(title: Text("用户登录系统"), message: Text("登录失败！"), dismissButton: .default(Text("确定")))
                    }
                    Button(action: {
                        if self.userUtils.register(username: self.username, password: self.password){
                            self.showRegSuccessInfo = true
                        }
                        else{
                            self.showRegFailInfo = true
                        }
                    }){
                        Text("注册")
                            .font(.title2)
                            .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                            .background(RoundedRectangle(cornerRadius: 10.0)
                                            .stroke(Color.black, lineWidth: 2.0))
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
