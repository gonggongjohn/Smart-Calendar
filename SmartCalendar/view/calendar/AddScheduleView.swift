//
//  AddScheduleView.swift
//  SmartCalendar
//

import SwiftUI

struct AddScheduleView: View {
    @Binding var schedules: [Schedule]
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
                    Text("课程")
                        .font(.title3)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: 40)
                        .background(RoundedRectangle(cornerRadius: 8.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
            }
            Spacer().frame(maxHeight: 50, alignment: .center)
            
            if(self.subview_code == 1){
                AddNormalView(schedules: $schedules, isPresented: $isPresented)
            }
            else if(self.subview_code == 2){
                AddLessonView(schedules: $schedules, isPresented: $isPresented)
            }
        }
    }
}

struct AddScheduleView_Previews: PreviewProvider {
    static var previews: some View {
        AddScheduleView(schedules: .constant([]), isPresented: .constant(true))
    }
}
