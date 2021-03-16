//
//  GeoStatView.swift
//  SmartCalendar
//

import SwiftUI
import MapKit

struct GeoStatView: View {
    private var geoUtils: GeoUtils
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
            for location in locations!.getHistoryList(){
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

struct GeoStatView_Previews: PreviewProvider {
    static var previews: some View {
        GeoStatView()
    }
}
