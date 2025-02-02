package student.management.StudentManagement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import student.management.StudentManagement.Controller.converter.StudentConverter;
import student.management.StudentManagement.StudentsWithCourses;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.repository.StudentRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

/*受講生情報を取り扱うサービス。
 * 受講生の検索や登録・更新処理を行う*/

@Service
@Slf4j
/* ログを確認してデバッグを行うこと*/
public class StudentService {

    private final StudentRepository repository;
    private StudentConverter converter;
    /*finalを宣言すると、そのクラスを継承したクラスにおいてそのメソッドを
     * オーバーライドできなくなる。コンストラクタの宣言にfinalを使用することはない*/

    @Autowired

    public StudentService(StudentRepository repository, StudentConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }
    /*本来はnewが入らないとインスタンスとして機能しないが、SpringBootの@Serviceで
    インスタンスとして呼び出すことが可能。更にAutowiredでStudentManagementApplicationの
    repositoryを呼び出せる。これを行うことで上書きが容易になる。*/

    /*受講生一覧詳細検索機能。
     * 全件検索を行うため、条件指定は行わない。
     * @return 受講生一覧（全件検索）*/
    public List<StudentDetail> searchStudentList() {
        List<Student> studentList = repository.search();
        studentList.forEach(student -> System.out.println("Repository Output: " + student));
        List<StudentsCourse> studentCourseList = repository.searchAllCoursesList();

        List<StudentDetail> studentDetails = converter.convertStudentDetails(studentList, studentCourseList);
        studentDetails.forEach(detail -> System.out.println("Converted Detail: " + detail));
        return converter.convertStudentDetails(studentList, studentCourseList);
    }

    public int updateStudentsCourses(StudentsCourse studentsCourses) {
        log.debug("Updating StudentsCourses: {}", studentsCourses);
        return repository.updateStudentCourse(studentsCourses);
    }

    /*受講生詳細検索。
     * IDに紐づく任意の受講生の情報を取得する。
     * @param id 受講生ID
     * @return 受講生詳細*/
    public StudentDetail searchStudent(Long id) {
        Student student = repository.searchStudent(id);

        // student.getId() を Long 型に変換
        List<StudentsCourse> studentCourse = repository.searchAllCourse(Long.valueOf(student.getId()));
        return new StudentDetail(student, studentCourse);
    }

    public List<StudentsWithCourses> searchStudentsWithCourses() {
        return repository.searchStudentsWithCourses();
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

    /*受講生詳細の登録を行う。
     *受講生と受オク生コース情報を個別に登録し、受講生コース情報には受講生情報を
     *紐づける値とコース開始日、コース終了日を設定する。
     *@param studentDetail 受講生詳細
     *@return 登録情報を付与した受講生詳細*/
    @Transactional
    public StudentDetail registerStudent(StudentDetail studentDetail) {
        // 学生情報を登録
        Student student = studentDetail.getStudent();
        repository.registerStudent(student);

        // データベースで生成されたIDを取得
        Integer generatedId = student.getId();

        if ( generatedId == null ) {
            throw new IllegalStateException("Student ID was not generated after registration.");
        }

        // 登録するコース情報を設定。forEachストリームにすることで、一括したリピート処理を行える
        studentDetail.getStudentCourseList().forEach(studentsCourses -> {
            initStudentsCourses(studentsCourses, generatedId);
            repository.registerStudentCourse(studentsCourses);
        });

        // studentDetail を返す
        return studentDetail;
    }

    /*受講生コース情報を登録する際の初期情報を設定する。
     *@param studentsCourses 受講生コース情報
     *@param student 受講生*/
    private void initStudentsCourses(StudentsCourse studentCourse, Integer generatedId) {
        studentCourse.setStudentId(generatedId);
        LocalDate now = LocalDate.now();
        studentCourse.setStartDate(now);
        studentCourse.setEndDate(now.plusYears(1));
    }

    public Student findStudentById(Long id) {
        return repository.findStudentById(id); // Repository に対応するメソッドを追加する
    }

    public List<StudentsCourse> findCoursesByStudentId(Long studentId) {
        return repository.findCoursesByStudentId(studentId); // 受講生IDに関連付けられたコースを取得
    }

    public StudentDetail getStudentDetailById(Long id) {
        Student student = repository.findStudentById(id);
        if ( student == null ) {
            throw new IllegalArgumentException("Student not found with id: " + id);
        }
        List<StudentsCourse> courses = repository.findCoursesByStudentId(id);
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);
        detail.setStudentCourseList(courses);
        return detail;
    }

    public void markAsDeleted(Long studentId) {
        log.debug("Marking student as deleted with ID: {}", studentId);
        repository.updateIsDeleted(studentId, true);
    }
    /*lombokを使用している場合、import lombok.extern.slf4j.Slf4j; @Slf4jを
     * 使うことでログを表示できる。主にデバッグで使用する*/

    /*受講生詳細の更新を行う。
     * 受講生と受講生コース情報をそれぞれ更新する。
     * @param studentDetail 受講生詳細*/
    @Transactional
    public void updateStudent(StudentDetail studentDetail) {
        if ( studentDetail.getStudent() == null ) {
            throw new IllegalArgumentException("Student object cannot be null.");
        }

        // デバッグ用ログ
        System.out.println("Updating Student: " + studentDetail.getStudent());
        System.out.println("Student ID: " + studentDetail.getStudent().getId());

        int updatedRows = repository.updateStudent(studentDetail.getStudent());
        if ( updatedRows == 0 ) {
            throw new IllegalStateException("Failed to update student. Student with ID "
                    + studentDetail.getStudent().getId() + " not found.");
        }
    }

    @Transactional
    public void updateStudentWithCourses(StudentDetail studentDetail) {
        // 学生情報の更新
        int updatedRows = repository.updateStudent(studentDetail.getStudent());
        if ( updatedRows == 0 ) {
            throw new IllegalStateException("学生情報の更新に失敗しました。該当する学生が見つかりません。");
        }

        // コース情報の更新または挿入
        for (StudentsCourse course : Optional.ofNullable(studentDetail.getStudentCourseList()).orElse(Collections.emptyList())) {
            int updatedCourseRows = repository.updateStudentCourse(course);
            if ( updatedCourseRows == 0 ) {
                repository.insertStudentsCourses(course); // 新規挿入
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