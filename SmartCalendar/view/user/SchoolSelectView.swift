//
//  SchoolSelectView.swift
//  SmartCalendar
//

import SwiftUI

struct SchoolSelectView: View {
    @State private var search_content: String = ""
    @State private var school_list: [IdNameRow] = []
    @State private var filtered_school_list: [IdNameRow] = []
    @Binding var school_chosen: IdNameRow?
    var body: some View{
        VStack{
            TextField("Search for school...", text: $search_content)
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(8.0)
                .onChange(of: self.search_content, perform: { content in
                    if(content != ""){
                        self.filtered_school_list = self.school_list.filter { $0.contentName.contains(content) }
                    }
                    else{
                        self.filtered_school_list = self.school_list
                    }
                })
            
            List{
                if(self.school_list.count > 0){
                    ForEach(self.filtered_school_list){school in
                        Text("\(school.contentName)")
                            .frame(maxWidth: 400)
                            .onTapGesture(perform: {
                                self.search_content = school.contentName
                                self.school_chosen = school
                            })
                    }
                }
            }
        }.padding()
        .onAppear(perform: {
            UserUtils.getSchool(completion: { (status, schools) in
                if(status){
                    self.school_list.append(contentsOf: schools)
                    self.filtered_school_list.append(contentsOf: schools)
                }
            })
        })
    }
}

struct SchoolSelectView_Previews: PreviewProvider {
    static var previews: some View {
        SchoolSelectView(school_chosen: .constant(nil))
    }
}
