package student.management.StudentManagement.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.ModelMap;
import student.management.StudentManagement.Controller.converter.StudentConverter;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;
import student.management.StudentManagement.service.StudentService;

import java.util.List;

@Controller
public class StudentController {

    private StudentService service;
    private StudentConverter converter;

    @Autowired
    public StudentController(StudentService service, StudentConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    @GetMapping("/studentList")
    public String getStudentList(Model model) {
        List<Student> students = service.searchStudentList();
        List<StudentsCourses> studentsCourses = service.searchAllCourses();

        model.addAttribute("studentList", converter.convertStudentDetails(students, studentsCourses));
        return "studentList";
    }

    @GetMapping("/allCourses")
    public List<StudentsCourses> getAllCourses() {
        return service.searchAllCourses();
    }

    @GetMapping("/studentsWithCourses")
    public List<student.management.StudentManagement.StudentsWithCourses> getStudentsWithCourses() {
        return service.searchStudentsWithCourses();
    }

    @GetMapping("/fetch-students")
    public List<Student> fetchStudents() {
        return service.fetchStudentsFromApi();
    }

    @GetMapping("/students/in30s")
    public List<Student> getStudentsIn30s() {
        return service.searchStudentsIn30s();
    }

    @GetMapping("/students/byCourse")
    public List<student.management.StudentManagement.StudentsWithCourses>
    getStudentsByCourseName(@RequestParam String courseName) {
        return service.searchStudentsByCourseName(courseName);
    }
}
    /*@Autowiredとは、Springフレームワークで用いるアノテーションのひとつ。これを記述するだけで
    インスタンス化を１回で行える。また、クラス内のnew演算子を消すことができる。つまりこのクラスでは
    @Autowiredを使うことで全てのpublic変数をインスタンス化させ、newをいちいち記述する必要が
    ないようにしている。つまり、このクラスでは@GetMappingのpublic変数にreturn service.を
    記載するだけでインスタンスとして成立させている。*/

/*@RequestParamとは、ブラウザからのリクエストの値（パラメータ）を取得することのできるアノテーション。
 * Spring bootにおいて基礎的なアノテーションの１つ。このコードでは、@RequestParamにStudentWithCoursesの
 * courseNameを文字列として取得し、curl -X GET "http://localhost:8080/students/byCourse?courseName=JAVA"で
 * JAVAコースの生徒の個人データを抽出できるようにしている。また、コース名を変更することで、別のコースを抽出できる。
 * （例えば、ExcelBasicと入れると[{"studentId":5,"studentName":"野島葵","furigana":"ノジマアオイ","nickName":
 * "TEST05","email":"TEST05","region":"愛媛","age":35,"gender":"女性","courlBasic","startDate":
 * "2024-11-09T15:00:00.000+00:00","endDate":"2025-11-08T15:00:00.000+00:00"}]と出てくる）*/
