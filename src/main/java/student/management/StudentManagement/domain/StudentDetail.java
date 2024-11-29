package student.management.StudentManagement.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;

import java.util.List;

@Getter
@Setter
@ToString
public class StudentDetail {
    private Student student;
    private List<StudentsCourses> studentsCourses;
}
