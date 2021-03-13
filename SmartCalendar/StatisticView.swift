//
//  StatisticView.swift
//  SmartCalendar
//

import SwiftUI
import MapKit

struct StatisticView: View {
    let card1=Card(name1:"2021.1.1-2021.1.7",name2:"轨迹图", name3:"快来看看这周你都去了哪些地方")
    let card2=Card(name1:"2021.1.7", name2:"计划完成度", name3:"今天你做好时间管理了吗？")
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
    var name1: String
    var name2: String
    var name3: String
}

struct CardView: View {
    var card: Card
    var body: some View {
        VStack{
            NavigationLink(destination: GeoStatView()){
                Image("").resizable()
                    .aspectRatio(contentMode: .fit)
            }
            
            HStack{
                VStack(alignment: .leading) {
                    Text("\(card.name1)")
                        .font(.headline)
                        .foregroundColor(.secondary)
                    Text("\(card.name2)")
                        .font(.title)
                        .fontWeight(.black)
                        .foregroundColor(.primary)
                        .lineLimit(3)
                    Text("\(card.name3)")
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
