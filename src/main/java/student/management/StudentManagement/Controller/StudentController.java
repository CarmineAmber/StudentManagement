package student.management.StudentManagement.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import student.management.StudentManagement.Controller.converter.StudentConverter;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.service.StudentService;
/*Modelを使用する際は、この場合はui.Modelを選択する（間違って別のものを選ばないようにする）*/

import java.util.Arrays;
import java.util.List;

@RestController
public class StudentController {

    private StudentService service;
    private StudentConverter converter;

    @Autowired
    public StudentController(StudentService service, StudentConverter converter) {
        this.service = service;
        this.converter = converter;
    }

    /*@Autowiredとは、Springフレームワークで用いるアノテーションのひとつ。これを記述するだけで
    インスタンス化を１回で行える。また、クラス内のnew演算子を消すことができる。つまりこのクラスでは
    @Autowiredを使うことで全てのpublic変数をインスタンス化させ、newをいちいち記述する必要が
    ないようにしている。つまり、このクラスでは@GetMappingのpublic変数にreturn service.を
    記載するだけでインスタンスとして成立させている。尚、this.service = service;
    this.converter = converterはStudentServiceとStudentConverterをこのクラスに紐づけている。*/

    @GetMapping("/studentList")
    public List<StudentDetail> getStudentList() {
        List<Student> students = service.searchStudentList();
        List<StudentsCourses> studentsCourses = service.searchAllCourses();

        System.out.println("Students: " + students);
        System.out.println("StudentsCourses: " + studentsCourses);

        List<StudentDetail> result = converter.convertStudentDetails(students, studentsCourses);
        System.out.println("Result: " + result);

        return converter.convertStudentDetails(students, studentsCourses);
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
    public ResponseEntity<StudentDetail> registerStudent(@RequestBody StudentDetail studentDetail) {
        StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
        return ResponseEntity.ok(responseStudentDetail);
    }
    /*@ModelAttributeは一般的にHTTPのGETメソッドで使用されるが、POSTメソッドでも使用できる。
    * このアノテーションは主にフォームデータの送信に使用される。これを使うことによって個別の
    * リクエストパラメータを自動でセットが可能になり、コードが読みやすくなる。この@ModelAttributeで
    * 指定されたオブジェクトは自動的にビューに渡され、画面表示にオブジェクトデータ（テキストボックス等）を
    * 簡単に利用できる。また、BindingResultを利用してエラーを簡単に処理できる。*/
    /*スペルミスに注意（updateStudentではない）*/

    @GetMapping("/student/{id}")
    public StudentDetail getStudent(@PathVariable String id){
        Long studentId = Long.valueOf(id);
        return service.searchStudent(studentId);
    }

    @PostMapping("/student/{id}")
    public ResponseEntity<String> updateStudentWithCourses(@RequestBody StudentDetail studentDetail) {
        try {
            // サービスメソッドでStudentとCoursesをまとめて更新
            service.updateStudentWithCourses(studentDetail);
            return ResponseEntity.ok("学生情報とコース情報の更新に成功しました。");
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body("更新処理が失敗しました: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("サーバーエラーが発生しました: " + e.getMessage());
        }
    }

    /*/{id}としなければ個別のページを表示できない。例えばid３の受講生を表示する場合は
    * /student/3と入力する*/
    /*このコードは受講生オブジェクトを取得し、削除フラグisDeleted（キャンセルのチェックボックス）が
    *trueであるか確認する。trueの場合、受講生を論理削除（非表示にする、つまりキャンセルのチェックボックスを
    * オンにする）するgetIsDeleted処理を行う。その際、longValue()を使用してIntegerをLongに変更している。
    * isDeletedがfalseかnullの場合、受講生情報を更新する。*/

    @GetMapping("/student/detail/{id}")
    public String getStudentDetail(@PathVariable Long id, Model model) {
        StudentDetail studentDetail = service.getStudentDetailById(id);
        if (studentDetail == null) {
            return "error/404"; // 学生が見つからない場合、404エラーページを表示
        }
        model.addAttribute("studentDetail", studentDetail);
        return "studentDetail"; // 詳細を表示するビュー
    }

    @PostMapping("/updateCourse")
    public ResponseEntity<String> updateCourse(@RequestBody StudentsCourses studentsCourses) {
        int rowsAffected = service.updateStudentsCourses(studentsCourses);
        if (rowsAffected > 0) {
            return ResponseEntity.ok("コース名の更新に成功しました。");
        }
        return ResponseEntity.status(400).body("更新処理が失敗しました。");
    }

}
/*@RequestParamとは、ブラウザからのリクエストの値（パラメータ）を取得することのできるアノテーション。
 * Spring bootにおいて基礎的なアノテーションの１つ。このコードでは、@RequestParamにStudentWithCoursesの
 * courseNameを文字列として取得し、curl -X GET "http://localhost:8080/students/byCourse?courseName=JAVA"で
 * JAVAコースの生徒の個人データを抽出できるようにしている。また、コース名を変更することで、別のコースを抽出できる。
 * （例えば、ExcelBasicと入れると[{"studentId":5,"studentName":"野島葵","furigana":"ノジマアオイ","nickName":
 * "TEST05","email":"TEST05","region":"愛媛","age":35,"gender":"女性","courlBasic","startDate":
 * "2024-11-09T15:00:00.000+00:00","endDate":"2025-11-08T15:00:00.000+00:00"}]と出てくる）*/

/*GPTを使う場合、StudentServiceで動作しない可能性がある場合はrepositoryに変更するとうまくいくようだ。*/

/*GET(@GetMapping),POST(@PostMapping),PUT(@PutMapping),DELETE(@DeleteMapping)はHTTPリクエストであり、
*アドレス可能性（URL)において何らかのアクションを起こすためのメソッド。例えばユーザー登録の場合はGETならばユーザーの
*取得、POSTならユーザーの登録を行うということ。*/
/*RESTの原則：ステートレスはログイン情報を保持しないが、ステートフルはログイン情報を保持する。
SNS等はこの２つを使い分けて機能している。基本的に現場では状況がアプリの内容によって異なるため
その都度柔軟に考える必要がある。*/
/*URIは階層的な構造をもたせる必要がある。そうでないと可読性が低下したりURIが複雑になる。
*例えば、特定のユーザー情報を取得する際にはIDをURIのパラメータとして指定する必要がある。
*chatGPTが@GetMappingにおいて("/student/{id}")と指定してきたのもこのため。*/