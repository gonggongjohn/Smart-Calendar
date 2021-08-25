//
//  GeoStatView.swift
//  SmartCalendar
//

import SwiftUI
import MapKit
import Photos

struct GeoStatView: View {
    private var geoUtils: GeoUtils
    private var profileUtils: StorageUtils
    private var locHistory: GeoHistory?
    private var geoItems: [GeoItem] = []
    
    init() {
        self.geoUtils = GeoUtils()
        self.profileUtils = StorageUtils()
        let fromDate = DateUtils.getDate(year: 2021, month: 3, day: 20)
        let toDate = DateUtils.getDate(time: Date())
        self.locHistory = self.profileUtils.getGeoHistory(from: fromDate, to: toDate)
        if locHistory != nil{
            for item in self.locHistory!.getHistoryList() {
                let time = item.geo.timestamp
                let name = item.name
                self.geoItems.append(GeoItem(place: name, time: DateUtils.getDateString(date: time)))
            }
        }
    }
    var body: some View {
        VStack{
            //MapView(locations: self.locHistory)
            List{
                ForEach(self.geoItems){ geoItem in GeoItemRow(geoItem: geoItem)
                }
            }
        }
    }
}

struct GeoItem: Identifiable {
    var id = UUID()
    var place: String
    var time: String
}

struct GeoItemRow: View {
    var geoItem: GeoItem
    
    var body: some View {
        HStack{
            Text("地点：\(geoItem.place)")
            Spacer()
            Text("时间：\(geoItem.time)")
        }
        .padding(.all)
        .background(/*@START_MENU_TOKEN@*//*@PLACEHOLDER=View@*/Color.green/*@END_MENU_TOKEN@*/)
        .cornerRadius(20.0)
        .shadow(radius: 3)
    }
}

/*
struct MapView: UIViewRepresentable {
    var locations: GeoHistory?
    let geoUtils = GeoUtils()
    
    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.delegate = context.coordinator
        let region = MKCoordinateRegion(center: CLLocationCoordinate2D(latitude: 31.229055, longitude: 121.406704), span: MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01))
        mapView.setRegion(region, animated: true)
        if locations != nil{
            for location in locations!.getHistoryList(){
                let annotation = MKPointAnnotation()
                annotation.title = location.name
                let fixedCoord = geoUtils.transformWGSToGCJ(wgsLocation: location.geo.coordinate)
                annotation.coordinate = fixedCoord
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
*/

struct GeoStatView_Previews: PreviewProvider {
    static var previews: some View {
        GeoStatView()
    }
}
