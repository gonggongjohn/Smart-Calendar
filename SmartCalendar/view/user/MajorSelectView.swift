//
//  MajorSelectView.swift
//  SmartCalendar
//

import SwiftUI

struct MajorSelectView: View{
    @State private var search_content: String = ""
    @State private var major_list: [IdNameRow] = []
    @State private var filtered_major_list: [IdNameRow] = []
    @Binding var major_chosen: IdNameRow?
    var body: some View{
        VStack{
            TextField("Search for major...", text: $search_content)
                .padding()
                .background(Color(.systemGray6))
                .cornerRadius(8.0)
                .onChange(of: self.search_content, perform: { content in
                    if(content != ""){
                        self.filtered_major_list = self.major_list.filter { $0.contentName.contains(content) }
                    }
                    else{
                        self.filtered_major_list = self.major_list
                    }
                })
            
            List{
                if(self.major_list.count > 0){
                    ForEach(self.filtered_major_list){ major in
                        Text("\(major.contentName)")
                            .frame(maxWidth: 400)
                            .onTapGesture(perform: {
                                self.search_content = major.contentName
                                self.major_chosen = major
                            })
                    }
                }
            }
        }.padding()
        .onAppear(perform: {
            UserUtils.getMajor(completion: { (status, majors) in
                if(status){
                    self.major_list.append(contentsOf: majors)
                    self.filtered_major_list.append(contentsOf: majors)
                }
            })
        })
    }
}

struct MajorSelectView_Previews: PreviewProvider {
    static var previews: some View {
        MajorSelectView(major_chosen: .constant(nil))
    }
}
