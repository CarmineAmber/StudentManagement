package student.management.StudentManagement.Controller.converter;

import org.springframework.stereotype.Component;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;
import student.management.StudentManagement.domain.StudentDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentConverter {
    public List<StudentDetail> convertStudentDetails
            (List<Student> students, List<StudentsCourses> studentsCourses) {
        List<StudentDetail> studentDetails = new ArrayList<>();
        students.forEach(student -> {
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student);

            List<StudentsCourses> convertStudentsCourses = studentsCourses.stream()
                    .filter(studentCourse -> student.getId().equals(studentCourse.getStudentId()))
                    .collect(Collectors.toList());

            studentDetail.setStudentsCourses(convertStudentsCourses);
            studentDetails.add(studentDetail);
        });
        return studentDetails;
    }
}
