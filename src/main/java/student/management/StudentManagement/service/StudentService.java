package student.management.StudentManagement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import student.management.StudentManagement.StudentsWithCourses;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.repository.StudentRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
/* Lombok を使用している場合、@Slf4j アノテーションを追加するだけでログを生成可能である。*/
public class StudentService {

    private final StudentRepository repository;
    /*finalを宣言すると、そのクラスを継承したクラスにおいてそのメソッドを
    * オーバーライドできなくなる。コンストラクタの宣言にfinalを使用することはない*/

    @Autowired

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }
    /*本来はnewが入らないとインスタンスとして機能しないが、SpringBootの@Serviceで
    インスタンスとして呼び出すことが可能。更にAutowiredでStudentManagementApplicationの
    repositoryを呼び出せる。これを行うことで上書きが容易になる。*/

    public List<Student> searchStudentList() {
        return repository.searchStudents();
    }

    public int updateStudentsCourses(StudentsCourses studentsCourses) {
        log.debug("Updating StudentsCourses: {}", studentsCourses);
        return repository.updateStudentsCourses(studentsCourses);
    }

    public StudentDetail searchStudent(Long id) {
        Student student = repository.searchStudent(id);

        // student.getId() を Long 型に変換
        List<StudentsCourses> studentsCourses = repository.searchAllCourses(Long.valueOf(student.getId()));

        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);
        studentDetail.setStudentsCourses(studentsCourses);
        return studentDetail;
    }

    public List<StudentsCourses> searchAllCourses() {
        return repository.searchAllCoursesList();
    }

    public List<StudentsWithCourses> searchStudentsWithCourses() {
        return repository.searchStudentsWithCourses();
    }

    public List<Student> searchStudentsIn30s() {
        return repository.searchStudentsInAgeRange(30, 39);
    }

    public List<StudentsWithCourses> searchStudentsByCourseName(String courseName) {
        return repository.searchStudentsByCourseName(courseName);
    }

    public List<Student> fetchStudentsFromApi() {
        try {
            URL url = new URL("http://localhost:8080/studentList");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
            reader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return objectMapper.readValue(response.toString(), new TypeReference<List<Student>>() {
            });
        } catch (Exception e) {
            log.error("Failed to fetch students from API", e);
            throw new RuntimeException("Unable to fetch students from API.", e);
        }
    }

    @Transactional
    public StudentDetail registerStudent(StudentDetail studentDetail) {
        // 学生情報を登録
        repository.registerStudent(studentDetail.getStudent());

        // データベースで生成されたIDを取得
        Integer generatedId = studentDetail.getStudent().getId();

        if (generatedId == null) {
            throw new IllegalStateException("Student ID was not generated after registration.");
        }

        // 登録するコース情報を設定
        for (StudentsCourses studentsCourses : studentDetail.getStudentsCourses()) {
            studentsCourses.setStudentId(generatedId);
            studentsCourses.setStartDate(LocalDate.now());
            studentsCourses.setEndDate(LocalDate.now().plusYears(1));
            repository.registerStudentsCourses(studentsCourses);
        }

        // studentDetail を返す
        return studentDetail;
    }

    public Student findStudentById(Long id) {
        return repository.findStudentById(id); // Repository に対応するメソッドを追加する
    }

    public List<StudentsCourses> findCoursesByStudentId(Long studentId) {
        return repository.findCoursesByStudentId(studentId); // 受講生IDに関連付けられたコースを取得
    }

    public StudentDetail getStudentDetailById(Long id) {
        Student student = repository.findStudentById(id);
        if ( student == null ) {
            throw new IllegalArgumentException("Student not found with id: " + id);
        }
        List<StudentsCourses> courses = repository.findCoursesByStudentId(id);
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);
        detail.setStudentsCourses(courses);
        return detail;
    }

    public void markAsDeleted(Long studentId) {
        log.debug("Marking student as deleted with ID: {}", studentId);
        repository.updateIsDeleted(studentId, true);
    }
    /*lombokを使用している場合、import lombok.extern.slf4j.Slf4j; @Slf4jを
    * 使うことでログを表示できる。主にデバッグで使用する*/

    @Transactional
    public void updateStudent(StudentDetail studentDetail) {
        if (studentDetail.getStudent() == null) {
            throw new IllegalArgumentException("Student object cannot be null.");
        }

        // デバッグ用ログ
        System.out.println("Updating Student: " + studentDetail.getStudent());
        System.out.println("Student ID: " + studentDetail.getStudent().getId());

        int updatedRows = repository.updateStudent(studentDetail.getStudent());
        if (updatedRows == 0) {
            throw new IllegalStateException("Failed to update student. Student with ID "
                    + studentDetail.getStudent().getId() + " not found.");
        }
    }

    @Transactional
    public void updateStudentWithCourses(StudentDetail studentDetail) {
        // 学生情報の更新
        int updatedRows = repository.updateStudent(studentDetail.getStudent());
        if (updatedRows == 0) {
            throw new IllegalStateException("学生情報の更新に失敗しました。該当する学生が見つかりません。");
        }

        // コース情報の更新
        for (StudentsCourses course : studentDetail.getStudentsCourses()) {
            int updatedCourseRows = repository.updateStudentsCourses(course);
            if (updatedCourseRows == 0) {
                throw new IllegalStateException("コース情報の更新に失敗しました。学生ID: " + course.getStudentId());
            }
        }
    }
    /*@Transactionalをメソッドやクラスに付与すると、その範囲内でのデータベース操作がトランザクションとして
    * 扱われる。メソッドの実行開始時にトランザクションが行われ、正常に終了するとコミットし、例外が発生すると
    * 自動的にロールバックする。このロールバック対象の例外を自由にカスタマイズすることが可能。データの変更を
    * 行わない場合、読み取り専用モードにも設定できる*/
}
        /*本来はnewが入らないとインスタンスとして機能しないが、SpringBootの@Serviceで
        インスタンスとして呼び出すことが可能。更にAutowiredでStudentManagementApplicationの
        repositoryを呼び出せる。これを行うことで上書きが容易になる。
        @Autowiredとは、Springフレームワークで用いるアノテーションのひとつ。これを記述するだけで
        インスタンス化を１回で行える。また、クラス内のnew演算子を消すことができる。つまりこのクラスでは
        @Autowiredを使うことで全てのpublic変数をインスタンス化させ、newをいちいち記述する必要が
        ないようにしている。*/