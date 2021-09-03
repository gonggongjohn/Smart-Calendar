//
//  StatisticView.swift
//  SmartCalendar
//

import SwiftUI
import MapKit

struct StatisticView: View {
    private var cardList: [Card] = []
    @State var toggle_exportSuccess = false
    @State var loading_flag = false
    
    init(){
        let card1 = Card(title: "轨迹图", subTitle:"快来看看你都去过哪些地方", icon: "geo_stat_icon")
        let card2 = Card(title: "日程分布图", subTitle: "今天我都干了些啥？", icon: "schedule_stat_icon")
        self.cardList.append(card1)
        self.cardList.append(card2)
    }
    
    var body: some View {
        NavigationView{
            ZStack{
                VStack{
                    List{
                        ForEach(self.cardList){card in StatCardView(card:card)}
                    }
                    Button(action: {
                        self.loading_flag = true
                        ScheduleUtils.getSharePicture(completion: { (status, image) in
                            if(status){
                                self.toggle_exportSuccess = true
                            }
                            self.loading_flag = false
                        })
                    }){
                        Text("分享我的日程")
                            .font(.title2)
                            .frame(maxWidth: 300, maxHeight: 60, alignment: .center)
                            .foregroundColor(.white)
                            .background(Color.blue)
                            .cornerRadius(15.0)
                    }.alert(isPresented: $toggle_exportSuccess, content: {
                        Alert(title: Text("导出图像"), message: Text("导出成功！"), dismissButton: .default(Text("确定")))
                    })
                }.disabled(self.loading_flag)
                .blur(radius: self.loading_flag ? 3 : 0)
                
                if(self.loading_flag){
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

struct Card: Identifiable {
    var id = UUID()
    var title: String
    var subTitle: String
    var icon: String
}

struct StatCardView: View {
    var card: Card
    var body: some View {
        VStack{
            if card.title == "轨迹图"{
                NavigationLink(destination: GeoStatView()){
                    Image(card.icon).resizable()
                        .aspectRatio(contentMode: .fit)
                }
            }
            else if card.title == "日程分布图" {
                NavigationLink(destination: ScheduleStatView()){
                    Image(card.icon).resizable()
                        .aspectRatio(contentMode: .fit)
                }
            }
            
            HStack{
                VStack(alignment: .leading) {
                    Text("\(card.title)")
                        .font(.title)
                        .fontWeight(.black)
                        .foregroundColor(.primary)
                        .lineLimit(3)
                    Text("\(card.subTitle)")
                        .foregroundColor(.secondary)
                }
                .layoutPriority(100)
                Spacer()
            }
            .padding()
        }
        .cornerRadius(20)
        .overlay(
            RoundedRectangle(cornerRadius: 10)
                .stroke(Color(.sRGB, red: 150/255, green: 150/255, blue: 150/255, opacity: 0.1), lineWidth: 1)
        )
        .padding([.top, .horizontal])
    }
}

struct StatisticView_Previews: PreviewProvider {
    static var previews: some View {
        StatisticView()
    }
}
