//
//  AddScheduleView.swift
//  SmartCalendar
//

import SwiftUI

struct AddScheduleView: View {
    @Binding var schedules: [Schedule]
    @Binding var schedule_local: ScheduleContainer
    @Binding var isPresented: Bool
    @State var subview_code: Int = 1
    
    var body: some View {
        VStack{
            HStack{
                Button(action: {
                    self.subview_code = 1
                }){
                    Text("常规日程")
                        .font(.title3)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: 40)
                        .background(RoundedRectangle(cornerRadius: 8.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
                
                Button(action: {
                    self.subview_code = 2
                }){
                    Text("动态日程")
                        .font(.title3)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: 40)
                        .background(RoundedRectangle(cornerRadius: 8.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
            }
            Spacer().frame(maxHeight: 50, alignment: .center)
            
            if(self.subview_code == 1){
                AddNormalView(schedules: $schedules, schedule_local: $schedule_local, isPresented: $isPresented)
            }
            else if(self.subview_code == 2){
                AddDynamicView(schedules: $schedules, schedule_local: $schedule_local, isPresented: $isPresented)
            }
        }.padding(.top)
        .background(Color(.systemGray6).opacity(0.8))
        .cornerRadius(8.0)
    }
}

struct AddScheduleView_Previews: PreviewProvider {
    static var previews: some View {
        AddScheduleView(schedules: .constant([]), schedule_local: .constant(ScheduleContainer()), isPresented: .constant(true))
    }
}
