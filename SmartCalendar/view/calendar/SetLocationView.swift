//
//  SetLocationView.swift
//  SmartCalendar
//

import SwiftUI
import MapKit

struct SetLocationView: View {
    @State private var search_content: String = ""
    @State private var place_options: [GeoPoint] = []
    @State var map_state = MapStateWrapper(latitude: 31.230685, longitude: 121.475207, latitudeDelta: 0.06, longitudeDelta: 0.06)
    @Binding var chosen: GeoPoint?
    var body: some View {
        VStack{
            TextField("Search for places...", text: $search_content)
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(8.0)
                .onChange(of: self.search_content, perform: { content in
                    let request = MKLocalSearch.Request()
                    request.naturalLanguageQuery = self.search_content
                    request.region = self.map_state.coordinateRegion
                    let search = MKLocalSearch(request: request)
                    search.start(completionHandler: { (response, error) in
                        if(error == nil && response != nil){
                            self.place_options = []
                            for item in response!.mapItems{
                                self.place_options.append(GeoPoint(name: item.name!, latitude: item.placemark.coordinate.latitude, longitude: item.placemark.coordinate.longitude))
                            }
                        }
                    })
                })
            
            List{
                ForEach(self.place_options){ place_item in
                    Text("\(place_item.name)")
                        .frame(maxWidth: 400)
                        .onTapGesture(perform: {
                            self.search_content = place_item.name
                            self.map_state.setTargetPoint(point: place_item)
                            self.chosen = place_item
                        })
                }
            }
            
            MapView(state: self.map_state)
                .frame(maxHeight: 300)
        }.padding()
    }
}

struct SetLocationView_Previews: PreviewProvider {
    static var previews: some View {
        SetLocationView(chosen: .constant(GeoPoint(name: "", latitude: 0.0, longitude: 0.0)))
    }
}
