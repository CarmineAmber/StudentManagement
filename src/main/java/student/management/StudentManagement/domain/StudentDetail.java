package student.management.StudentManagement.domain;

import lombok.*;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetail {
    private Student student;
    private List<StudentsCourses> studentsCourses;
    /*studentとstudentCoursesの２つのクラスに表記されているものを
    * 繋ぎ合わせ、StudentDetailを作っている。*/
}
