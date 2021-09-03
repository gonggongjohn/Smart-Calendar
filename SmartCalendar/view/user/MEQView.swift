//
//  MEQView.swift
//  SmartCalendar
//

import SwiftUI

struct MEQView: View {
    @State var questions: [Question] = []
    @State var answers: [QuestionOption?] = []
    var body: some View {
        VStack{
            Text("帮助我们理解您的作息规律")
                .font(.title)
            ScrollView{
                VStack{
                    if(self.questions.count > 0){
                        ForEach(self.questions){ question in
                            QuestionCell(description: question.description, options: question.options, chosen: $answers[question.questionId])
                        }
                    }
                }.padding()
            }.onAppear(perform: {
                UserUtils.getMEQ(completion: { (status, question_list) in
                    if(status){
                        self.questions = question_list
                        for question in question_list{
                            while(answers.count <= question.questionId){
                                self.answers.append(nil)
                            }
                        }
                    }
                })
            })
            
            Button(action: {
                let score = UserUtils.evalMEQScore(chosens: self.answers)
                let user_info = StorageUtils.getUserInfo()
                if(user_info != nil){
                    user_info!.meqScore = score
                    UserUtils.updateInfo(info: user_info!, completion: { status in
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
                    })
                }
            }){
                Text("完成")
                    .font(.title2)
                    .frame(maxWidth: 300, maxHeight: 50, alignment: .center)
                    .foregroundColor(.white)
                    .background(Color.blue)
                    .cornerRadius(15.0)
            }
        }
    }
}

struct QuestionCell: View {
    @State private var description: String
    @State private var options: [QuestionOption]
    @State private var chosen_onehot: [Bool] = []
    @Binding var chosen: QuestionOption?
    
    
    init(description: String, options: [QuestionOption], chosen: Binding<QuestionOption?>) {
        _chosen = chosen
        _description = State(initialValue: description)
        _options = State(initialValue: options)
        var onehot_tmp: [Bool] = []
        for option in options{
            while(onehot_tmp.count <= option.optionId){
                onehot_tmp.append(false)
            }
        }
        _chosen_onehot = State(initialValue: onehot_tmp)
    }
    
    var body: some View {
        VStack{
            Text("\(self.description)")
                .font(.title3)
                .minimumScaleFactor(0.3)
            ForEach(self.options){ option in
                Toggle(isOn: $chosen_onehot[option.optionId]){
                    Text("\(option.description)")
                }.toggleStyle(CheckBoxToggleStyle(shape: .circle))
                .onChange(of: chosen_onehot[option.optionId], perform: { flag in
                    if(flag){
                        for i in 0 ..< chosen_onehot.count{
                            if(i != option.optionId){
                                chosen_onehot[i] = false
                            }
                        }
                        self.chosen = option
                    }
                })
            }
        }
    }
}

struct CheckBoxToggleStyle: ToggleStyle{
    enum CheckBoxShape: String{
        case circle
        case square
    }
    let shape : CheckBoxShape
    init(shape: CheckBoxShape = .circle){
        self.shape = shape
    }
    //configuration中包含isOn和label
    func makeBody(configuration: Configuration) -> some View {
        let systemName:String = configuration.isOn ? "checkmark.\(shape.rawValue).fill" : shape.rawValue
        Button(action: {
            configuration.isOn.toggle()
        }) {
            configuration.label
            Image(systemName: systemName)
                .resizable()
                .frame(width: 30, height: 30)
        }
    }
}

struct MEQView_Previews: PreviewProvider {
    static var previews: some View {
        MEQView()
    }
}
