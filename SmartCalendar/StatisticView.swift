//
//  StatisticView.swift
//  SmartCalendar
//

import SwiftUI
import MapKit

struct StatisticView: View {
    let card1 = Card(title: "轨迹图", subTitle:"快来看看你都去过哪些地方", icon: "geo_stat_icon")
    let card2 = Card(title: "日程分布图", subTitle: "今天我都干了些啥？", icon: "schedule_stat_icon")
    private var cardList: [Card] = []
    
    init(){
        self.cardList.append(card1)
        self.cardList.append(card2)
    }
    
    var body: some View {
        List{
            ForEach(self.cardList){card in CardView(card:card)}
        }
    }
}

struct Card: Identifiable {
    var id = UUID()
    var title: String
    var subTitle: String
    var icon: String
}

struct CardView: View {
    var card: Card
    var body: some View {
        VStack{
            NavigationLink(destination: GeoStatView()){
                Image(card.icon).resizable()
                    .aspectRatio(contentMode: .fit)
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
