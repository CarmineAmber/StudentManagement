package student.management.StudentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;
import student.management.StudentManagement.service.StudentService;

import java.util.List;

@RestController
public class StudentController {

    private StudentService service;

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping("/studentList")
    public List<Student> getStudentList() {
        /*リクエストの加工処理、入力チェック*/
        return service.searchStudentList();
    }

    @GetMapping("/allCourses")
    public List<StudentsCourses> getAllCourses() {
        return service.searchAllCourses();
    }

    @GetMapping("/studentsWithCourses")
    public List<StudentsWithCourses> getStudentsWithCourses() {
        return service.searchStudentsWithCourses();
    }

    @GetMapping("/fetch-students")
    public List<Student> fetchStudents() {
        return service.fetchStudentsFromApi();
    }

    @GetMapping("/studentsIn30s")
    public List<Student> getStudentsIn30s() {
        return service.searchStudentsIn30s();
    }

    @GetMapping("/JAVA")
    public List<StudentsWithCourses> getJavaCourse() {
        return service.searchJavaCourse();
    }
}
