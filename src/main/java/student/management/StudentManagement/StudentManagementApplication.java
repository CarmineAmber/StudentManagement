package student.management.StudentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class StudentManagementApplication {

    @Autowired
    private StudentRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }

    @GetMapping("/studentList")
    public List<Student> getStudentList() {
        return repository.searchStudents();
    }

    @GetMapping("/allCourses")
    public List<StudentsCourses> getAllCourses() {
        return repository.searchAllCourses();
    }

    @GetMapping("/studentsWithCourses")
    public List<StudentsWithCourses> getStudentsWithCourses() {
        return repository.searchStudentsWithCourses();
    }
}
