//
//  ContentView.swift
//  SmartCalendar
//

import SwiftUI

struct LoginView: View {
    @State var server: String = ""
    @State var username: String = ""
    @State var password: String = ""
    @State var isLoginSuccess: Bool = false
    var body: some View {
        NavigationView{
            VStack(spacing: 40.0){
                Text("Smart Calendar")
                    .font(.title)
                VStack{
                    HStack{
                        Text("服务器地址：")
                            .font(.title3)
                        TextField("Host Address", text: $server)
                            .font(.title3)
                    }
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
                NavigationLink(
                    destination: MainView(),
                    isActive: $isLoginSuccess) {
                    Text("登陆")
                        .onTapGesture {
                            let user_utils = UserUtils()
                            self.isLoginSuccess = user_utils.login(server: self.server, username: self.username, password: self.password)
                        }
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
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
