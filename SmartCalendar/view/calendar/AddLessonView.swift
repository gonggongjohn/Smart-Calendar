//
//  AddLessonView.swift
//  SmartCalendar
//

import SwiftUI

struct AddLessonView: View {
    @Binding var schedules: [Schedule]
    @Binding var isPresented: Bool
    @State var toggle_photoSelector: Bool = false
    @State var timetable_image: UIImage?
    @State var lessons: [Lesson] = []
    
    var body: some View {
        VStack{
            Button(action: {
                self.toggle_photoSelector = true
            }){
                Text("解析课程表")
                    .font(.title3)
                    .padding()
                    .background(RoundedRectangle(cornerRadius: 8.0)
                                    .stroke(Color.black, lineWidth: 2.0))
            }.sheet(isPresented: $toggle_photoSelector, onDismiss: {
                if(self.timetable_image != nil){
                    PhotoUtils.phaseLessons(image: self.timetable_image!, completion: {
                        (status, lesson_list) -> Void in
                        if(status){
                            self.lessons.append(contentsOf: lesson_list)
                        }
                    })
                }
            }, content: {
                ImagePicker(image: $timetable_image)
            })
            
            List {
                ForEach(self.lessons){ lesson in
                    LessonRow(lesson: lesson)
                }
            }.frame(maxHeight: 400, alignment: .center)
            
            HStack{
                Button(action: {
                    self.isPresented = false
                }){
                    Text("确定")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
                Button(action: {
                    self.isPresented = false
                }){
                    Text("取消")
                        .font(.title2)
                        .frame(width: /*@START_MENU_TOKEN@*/100.0/*@END_MENU_TOKEN@*/, height: /*@START_MENU_TOKEN@*/50.0/*@END_MENU_TOKEN@*/)
                        .background(RoundedRectangle(cornerRadius: 10.0)
                                        .stroke(Color.black, lineWidth: 2.0))
                }
            }
        }.padding()
    }
}

struct AddLessonView_Previews: PreviewProvider {
    static var previews: some View {
        AddLessonView(schedules: .constant([]), isPresented: .constant(true))
    }
}

struct LessonRow: View {
    var lesson: Lesson
    var body: some View {
        VStack{
            Text("课程名：\(lesson.name)")
            Text("时间：星期\(lesson.day) \(DateUtils.time2str(dateDelta: lesson.startDelta)) ~ \(DateUtils.time2str(dateDelta: lesson.endDelta))")
        }
    }
}

struct ImagePicker: UIViewControllerRepresentable {
    @Environment(\.presentationMode) var presentationMode
    @Binding var image: UIImage?

    func makeUIViewController(context: UIViewControllerRepresentableContext<ImagePicker>) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        picker.sourceType = .photoLibrary
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: UIViewControllerRepresentableContext<ImagePicker>) {

    }
    
    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }
    
    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let parent: ImagePicker

        init(_ parent: ImagePicker) {
            self.parent = parent
        }
        
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            if let uiImage = info[.originalImage] as? UIImage {
                parent.image = uiImage
            }

            parent.presentationMode.wrappedValue.dismiss()
        }
    }
}
