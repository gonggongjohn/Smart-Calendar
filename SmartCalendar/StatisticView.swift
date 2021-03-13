//
//  StatisticView.swift
//  SmartCalendar
//

import SwiftUI
import MapKit

struct StatisticView:View {
    @ObservedObject var geoUtils: GeoUtils
    private var locHistory: GeoHistory?
    
    init() {
        self.geoUtils = GeoUtils()
        self.geoUtils.initGeoTrace()
        self.locHistory = self.geoUtils.getHistory()
    }
    var body: some View {
        VStack{
            HStack{
                Button(action: {
                    geoUtils.startTrace()
                }){
                    Text("开始定位")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
                Button(action: {
                    geoUtils.stopTrace()
                }){
                    Text("停止定位")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
            }
            MapView(locations: self.locHistory)
        }
    }
}

struct MapView: UIViewRepresentable {
    var locations: GeoHistory?
    
    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.delegate = context.coordinator
        if locations != nil{
            for location in locations!.geoList{
                let annotation = MKPointAnnotation()
                annotation.title = location.name
                annotation.coordinate = location.geo.coordinate
                mapView.addAnnotation(annotation)
            }
        }
        return mapView
    }
    
    func updateUIView(_ uiView: MKMapView, context: Context) {
        
    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, MKMapViewDelegate{
        var parent: MapView
        init(_ parent: MapView) {
            self.parent = parent
        }
    }
}




struct Card: Identifiable {
    var id = UUID()
    struct DetailView: View {
        var body: some View {
            Text("Hello")
        }
    }
    var name1: String
    var name2: String
    var name3: String
}

struct CardView: View {
    var card: Card
    var body: some View {
        VStack{
            NavigationLink(destination: Card.DetailView()){
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

struct CardsView: View {
    @State private var cardList: [Card] = []
    let card1=Card(name1:"2021.1.1-2021.1.7",name2:"轨迹图", name3:"快来看看这周你都去了哪些地方")
    let card2=Card(name1:"2021.1.7", name2:"计划完成度", name3:"今天你做好时间管理了吗？")
    
    init(){
        cardList.append(card1)
        cardList.append(card2)
    }
    
    var body: some View {
        List{
            ForEach(self.cardList){card in CardView(card:card)}
        }
    }
}

struct StatisticView_Previews: PreviewProvider {
    static var previews: some View {
        CardsView()
    }
}
