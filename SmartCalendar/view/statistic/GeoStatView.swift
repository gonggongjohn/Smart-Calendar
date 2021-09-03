//
//  GeoStatView.swift
//  SmartCalendar
//

import SwiftUI
import MapKit
import Photos

struct GeoStatView: View {
    @State private var geo_items: [GeoItem] = []
    @State private var map_state = MapStateWrapper(latitude: 31.230685, longitude: 121.475207, latitudeDelta: 0.06, longitudeDelta: 0.06)
    
    var body: some View {
        VStack{
            MapView(state: self.map_state)
                .frame(maxHeight: 300)
            List{
                ForEach(self.geo_items.sorted(by: { $0.start <= $1.start })){ geo_item in GeoItemRow(geoItem: geo_item)
                }
            }
        }
        .onAppear(perform: {
            let schedule_local = StorageUtils.getScheduleFromLocal()
            if(schedule_local != nil){
                let schedule_list = Array(schedule_local!.schedules.values)
                for schedule in schedule_list{
                    if(schedule.position != nil){
                        let geo_item = GeoItem(place: schedule.position!, start:  schedule.start, end: schedule.end)
                        self.geo_items.append(geo_item)
                    }
                }
            }
            self.map_state.pointList = []
            for item in self.geo_items{
                self.map_state.pointList!.append(item.place)
                self.map_state.updateFlag = true
            }
        })
    }
}

struct GeoItem: Identifiable {
    var id = UUID()
    var place: GeoPoint
    var start: Date
    var end: Date
}

struct GeoItemRow: View {
    var geoItem: GeoItem
    
    var body: some View {
        VStack{
            Text("地点：\(geoItem.place.name)")
            Text("开始时间：\(DateUtils.date2str(date: geoItem.start))")
            Text("开始时间：\(DateUtils.date2str(date: geoItem.end))")
        }
        .padding(.all)
        .background(
            HStack{
                RoundedRectangle(cornerRadius: 8)
                        .fill(Color(.systemGreen))
                        .frame(width: 5)
                Spacer()
            }
        )
        .cornerRadius(20.0)
        .shadow(radius: 2)
    }
}

struct GeoStatView_Previews: PreviewProvider {
    static var previews: some View {
        GeoStatView()
    }
}
