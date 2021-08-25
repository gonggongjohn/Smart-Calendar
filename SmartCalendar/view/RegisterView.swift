//
//  RegisterView.swift
//  SmartCalendar
//

import SwiftUI

struct RegisterView: View {
    @State var username: String = ""
    @State var password: String = ""
    @State var phone: String = ""
    @State var loadingFlag: Bool = false
    
    var body: some View {
        ZStack{
            VStack{
                Text("注册账户")
                    .font(.title)
                Spacer().frame(maxHeight: 50, alignment: .center)
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
                    HStack{
                        Text("手机：")
                            .font(.title3)
                            .frame(width: 90, alignment: .leading)
                        TextField("Phone", text: $phone)
                            .font(.title3)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                    }
                }.padding()
                .frame(maxHeight: 220, alignment: .center)
                .background(Color.green)
                .cornerRadius(15.0)
                Spacer().frame(maxHeight: 50, alignment: .center)
                RegisterButton(username: $username, password: $password, phone: $phone, loadingFlag: $loadingFlag)
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

struct RegisterView_Previews: PreviewProvider {
    static var previews: some View {
        RegisterView()
    }
}

struct RegisterButton: View{
    @Binding var username: String
    @Binding var password: String
    @Binding var phone: String
    @Binding var loadingFlag: Bool
    @State var showRegInfo: Bool = false
    @State var statusCode = 0
    var body: some View{
        Button(action: {
            if(self.username == "" || self.password == "" || self.phone == ""){
                self.statusCode = 9
                self.showRegInfo = true
            }
            else{
                self.loadingFlag = true
                UserUtils.register(username: self.username, password: self.password, phone: self.phone, completion: {
                    (status) -> Void in
                    self.loadingFlag = false
                    if status {
                        statusCode = 1
                        //self.showRegInfo = true
                        StorageUtils.saveUserInfo(username: self.username, password: self.password)
                        UserUtils.login(username: self.username, password: self.password, completion: { (status) in
                            if(status == 1){
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
                        })
                    }
                    else{
                        statusCode = 2
                        self.showRegInfo = true
                    }
                })
            }
        }){
            Text("注册")
                .font(.title2)
                .frame(maxWidth: 300, maxHeight: 50, alignment: .center)
                .foregroundColor(.white)
                .background(Color.blue)
                .cornerRadius(15.0)
        }
        .alert(isPresented: $showRegInfo){
            if(self.statusCode == 1){
                return Alert(title: Text("提示"), message: Text("注册成功！"), dismissButton: .default(Text("确定")))
            }
            else if(self.statusCode == 2){
                return Alert(title: Text("注册失败"), message: Text("用户名已存在！"), dismissButton: .default(Text("确定")))
            }
            else if(self.statusCode == 9){
                return Alert(title: Text("注册失败"), message: Text("信息不能为空！"), dismissButton: .default(Text("确定")))
            }
            else{
                return Alert(title: Text("注册失败"), message: Text("未知错误！"), dismissButton: .default(Text("确定")))
            }
        }
    }
}
