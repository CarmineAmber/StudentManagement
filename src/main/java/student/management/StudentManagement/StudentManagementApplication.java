package student.management.StudentManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SpringBootApplication
@RestController
public class StudentManagementApplication {

    @Autowired
    private StudentRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }

    @GetMapping("/student")
    public String getStudent(@RequestParam String name){
        Student student = repository.searchByName(name);
        if(student != null) {
            return student.getName() + " " + student.getAge() + "歳";
        } else {
            return "Student not found";
        }
    }
    /*repositoryとは、データや情報、知識や成果物を蓄積するデータベースの
     * ことを示す。このコードでは、データベースからStudentRepositoryという
     * インターフェースを使用してSQLからデータを呼び出し、見つからない場合は
     * "Student not found"を返している。。*/

    @PostMapping("/student")
    public void registerStudent(String name, int age){
        repository.registerStudent(name, age);
    }

    @PatchMapping("/student")
    public void updateStudent(String name, int age){
        repository.updateStudent(name,age);
    }

    @DeleteMapping("/student")
    public void deleteStudent(String name){
        repository.deleteStudent(name);
    }
    @GetMapping("/students")
    public List<Student> getAllStudents() {
        return repository.getAllStudents();
    }
}
