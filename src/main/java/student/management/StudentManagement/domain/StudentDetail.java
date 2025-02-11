package student.management.StudentManagement.domain;

import jakarta.validation.Valid;
import lombok.*;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudentDetail {
    @Valid
    private Student student;

    @Valid
    private List<@Valid StudentsCourse> studentCourseList= new ArrayList<>(); ;
    /*studentとstudentCoursesの２つのクラスに表記されているものを
    * 繋ぎ合わせ、StudentDetailを作っている。
    * 尚、リストがnullになることによるサーバーエラーを防ぐために
    * new ArrayList<>を表示して初期化している。*/
}
