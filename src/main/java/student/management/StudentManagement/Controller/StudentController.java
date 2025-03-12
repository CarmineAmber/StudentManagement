package student.management.StudentManagement.Controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.type.descriptor.java.ObjectJavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import student.management.StudentManagement.Validation.ValidationGroups;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.exception.StudentNotFoundException;
import student.management.StudentManagement.exception.TestException;
import student.management.StudentManagement.repository.StudentRepository;
import student.management.StudentManagement.service.StudentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;
/*Modelを使用する際は、この場合はui.Modelを選択する（間違って別のものを選ばないようにする）*/

import java.util.List;

/*受講生の検索や登録、更新などを行うREST APIとして受け付けるController*/

@Validated
@RestController
public class StudentController {

    private StudentService service;
    private StudentRepository repository;
    private static final Logger log = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    public StudentController(StudentService service) {
        this.service = service;
    }

    /*@Autowiredとは、Springフレームワークで用いるアノテーションのひとつ。これを記述するだけで
    インスタンス化を１回で行える。また、クラス内のnew演算子を消すことができる。つまりこのクラスでは
    @Autowiredを使うことで全てのpublic変数をインスタンス化させ、newをいちいち記述する必要が
    ないようにしている。つまり、このクラスでは@GetMappingのpublic変数にreturn service.を
    記載するだけでインスタンスとして成立させている。尚、this.service = service;
    this.converter = converterはStudentServiceとStudentConverterをこのクラスに紐づけている。*/

    @Operation(summary = "一覧検索", description = "受講生の一覧を検索する。")
    @GetMapping("/studentList")
    public List<StudentDetail> getStudentList() {
        List<StudentDetail> studentDetails = service.searchStudentList();
        return studentDetails;
    }

    /*@ModelAttributeは一般的にHTTPのGETメソッドで使用されるが、POSTメソッドでも使用できる。
     * このアノテーションは主にフォームデータの送信に使用される。これを使うことによって個別の
     * リクエストパラメータを自動でセットが可能になり、コードが読みやすくなる。この@ModelAttributeで
     * 指定されたオブジェクトは自動的にビューに渡され、画面表示にオブジェクトデータ（テキストボックス等）を
     * 簡単に利用できる。また、BindingResultを利用してエラーを簡単に処理できる。*/
    /*スペルミスに注意（updateStudentではない）*/

    @Operation(summary = "受講生登録", description = "受講生を登録する。")
    @PostMapping("/registerStudent")
    public ResponseEntity<StudentDetail> registerStudent(@Valid @RequestBody StudentDetail studentDetail) {
        StudentDetail savedStudent = service.registerStudent(studentDetail);
        return ResponseEntity.ok(savedStudent);
    }
    /*public ResponseEntity<String>とするとnullになるので注意すること*/

    @Operation(summary = "受講生更新", description = "受講生の更新を個人検索画面から行う。")
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

    @Operation(summary = "受講生検索",description = "受講生を検索する")
    @GetMapping("/student/{id}")
    public ResponseEntity<Object> getStudent(@PathVariable("id") String id) {
        if (!id.matches("\\d+")) {
            return ResponseEntity.badRequest().body("IDは半角数字で入力して下さい。");
        }

        Long studentId = Long.parseLong(id);
        StudentDetail studentDetail = service.searchStudent(studentId);

        if (studentDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("該当のデータが存在しません。");
        }

        return ResponseEntity.ok(studentDetail); // StudentDetailを返す
    }


    @Operation(summary = "受講生更新", description = "受講生情報を更新する。")
    @PutMapping("/updateStudents")
    public ResponseEntity<?> updateStudent(@RequestBody @Valid StudentDetail studentDetail) {
        log.info("Updating student: {}", studentDetail);

        if (studentDetail.getStudentCourseList() != null) {
            for (StudentsCourse course : studentDetail.getStudentCourseList()) {
                log.info("Course info: {}", course);
            }
        }

        service.updateStudent(studentDetail);
        return ResponseEntity.ok("Update successful");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal Server Error: " + e.getMessage());
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