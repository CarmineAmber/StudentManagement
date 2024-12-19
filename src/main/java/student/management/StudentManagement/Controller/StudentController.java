package student.management.StudentManagement.Controller;

import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import student.management.StudentManagement.Controller.converter.StudentConverter;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.repository.StudentRepository;
import student.management.StudentManagement.service.StudentService;
/*Modelを使用する際は、この場合はui.Modelを選択する（間違って別のものを選ばないようにする）*/

import java.util.Arrays;
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

    @GetMapping("/newStudent")
    public String newStudent(Model model) {
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudentsCourses(Arrays.asList(new StudentsCourses()));  // 必要なら空リストを初期化
        model.addAttribute("studentDetail", studentDetail);
        return "registerStudents";
    }

    @PostMapping("/registerStudent")
    public String registerStudent(@ModelAttribute StudentDetail studentDetail, BindingResult result) {
        if ( result.hasErrors() ) {
            return "registerStudents";  // 再度フォームを表示
        }
        service.registerStudent(studentDetail);
        return "redirect:/studentList";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(Model model, @PathVariable  Long id) {
        StudentDetail studentDetail = service.getStudentDetailById(id);
        model.addAttribute("studentDetail", studentDetail);
        return "updateStudents";
    }

    @PostMapping("/update")
    public String updateStudent(@ModelAttribute StudentDetail studentDetail) {
        // サービスを呼び出してデータを更新
        service.updateStudent(studentDetail);
        return "redirect:/student/list"; // 更新後のリダイレクト先
    }

    @GetMapping("/student/detail/{studentName}")
    public String getStudentDetail(@PathVariable String studentName, Model model) {
        // サービスを使ってデータを取得
        StudentDetail studentDetail = service.findByName(studentName);
        if (studentDetail == null) {
            return "error/404"; // 該当する名前がない場合、404エラーページを返す
        }
        model.addAttribute("studentDetail", studentDetail);
        return "studentDetail"; // studentDetail.html というテンプレートを表示
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

/*GPTを使う場合、StudentServiceで動作しない可能性がある場合はrepositoryに変更するとうまくいくようだ。*/