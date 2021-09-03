//
//  MapView.swift
//  SmartCalendar
//

import SwiftUI
import MapKit

class MapStateWrapper: ObservableObject{
    @Published var coordinateRegion: MKCoordinateRegion
    @Published var targetPoint: GeoPoint?
    @Published var pointList: [GeoPoint]?
    @Published var updateFlag: Bool
    
    init(latitude: Double, longitude: Double, latitudeDelta: Double, longitudeDelta: Double) {
        self.coordinateRegion = MKCoordinateRegion(center: CLLocationCoordinate2D(latitude: latitude, longitude: longitude), span: MKCoordinateSpan(latitudeDelta: latitudeDelta, longitudeDelta: longitudeDelta))
        self.updateFlag = false
    }
    
    init(latitudeDelta: Double, longitudeDelta: Double, targetPoint: GeoPoint) {
        self.coordinateRegion = MKCoordinateRegion(center: targetPoint.coordinate, span: MKCoordinateSpan(latitudeDelta: latitudeDelta, longitudeDelta: longitudeDelta))
        self.targetPoint = targetPoint
        self.updateFlag = false
    }
    
    public func setRegionCenter(latitude: Double, longitude: Double){
        self.coordinateRegion.center = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        self.updateFlag = true
    }
    
    public func setRegionCenter(coordinate: CLLocationCoordinate2D){
        self.coordinateRegion.center = coordinate
        self.updateFlag = true
    }
    
    public func setTargetPoint(point: GeoPoint){
        self.targetPoint = point
        setRegionCenter(coordinate: point.coordinate)
        self.updateFlag = true
    }
}

struct MapView: UIViewRepresentable {
    @ObservedObject var state: MapStateWrapper
    
    init(state: MapStateWrapper) {
        self.state = state
    }
    
    func makeUIView(context: Context) -> MKMapView {
        let mapView = MKMapView()
        mapView.delegate = context.coordinator
        mapView.region = self.state.coordinateRegion
        if(self.state.targetPoint != nil){
            let annotation = MKPointAnnotation()
            annotation.title = self.state.targetPoint!.name
            annotation.coordinate = self.state.targetPoint!.coordinate
            mapView.addAnnotation(annotation)
        }
        else if(self.state.pointList != nil){
            for point in self.state.pointList!{
                let annotation = MKPointAnnotation()
                annotation.title = point.name
                annotation.coordinate = point.coordinate
                mapView.addAnnotation(annotation)
            }
        }
        return mapView
    }
    
    func updateUIView(_ uiView: MKMapView, context: Context) {
        uiView.delegate = context.coordinator
        
        if(self.state.updateFlag){
            uiView.setRegion(self.state.coordinateRegion, animated: true)
            let old_annotations = uiView.annotations
            for old_annotation in old_annotations{
                uiView.removeAnnotation(old_annotation)
            }
            if(self.state.targetPoint != nil){
                let annotation = MKPointAnnotation()
                annotation.title = self.state.targetPoint!.name
                annotation.coordinate = self.state.targetPoint!.coordinate
                uiView.addAnnotation(annotation)
            }
            else if(self.state.pointList != nil){
                for point in self.state.pointList!{
                    let annotation = MKPointAnnotation()
                    annotation.title = point.name
                    annotation.coordinate = point.coordinate
                    uiView.addAnnotation(annotation)
                }
            }
            self.state.updateFlag = false
        }
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

struct MapView_Previews: PreviewProvider {
    static var previews: some View {
        MapView(state: MapStateWrapper(latitude: 31.230685, longitude: 121.475207, latitudeDelta: 0.06, longitudeDelta: 0.06))
    }
}
