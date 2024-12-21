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
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository repository;

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

    public StudentDetail searchStudent(Long id){
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
            /*URLの設定*/
            URL url = new URL("http://localhost:8080/studentList");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            /*HttpURLConnectionとは、httpを基にget,post,put,deleteなどのリクエストを
             * サポートする。このコードでは、下記のcon.setRequestMethod("GET");と
             * con.setRequestProperty("Accept", "application/json");という
             * リクエストをサポートしている。*/
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            /*JAVAでAPI通信を使ってJSONを取得するための接続設定。*/

            // InputStreamの読み込み
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            /*ここでUTF_8を設定しないと文字化けする。*/
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line.trim());  // 改行やスペースを削除して結合
            }
            reader.close();

            /*JSONのパース（JSON形式の文字列をJAVAオブジェクトに変換する）。ObjectMapperとは、JAVAオブジェクトと
            JSONのパースを簡単にするためのjacksonパッケージの１つ。*/
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            // JSONをList<Student>にパース
            return objectMapper.readValue(response.toString(), new TypeReference<List<Student>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Transactional
    public void registerStudent(StudentDetail studentDetail) {
        repository.registerStudent(studentDetail.getStudent());
        studentDetail.getStudent().getId();
        /*このWebアプリでは、サービスにトランザクション処理を記載している。
         * サービス層に記載することを推奨している。*/
        /*TODO：コース情報登録も行う。*/
        for (StudentsCourses studentsCourses : studentDetail.getStudentsCourses()) {
            studentsCourses.setStudentId(studentDetail.getStudent().getId());
            studentsCourses.setStartDate(LocalDate.now());
            studentsCourses.setEndDate(LocalDate.now().plusYears(1));
            repository.registerStudentsCourses(studentsCourses);
        }
    }

    public Student findStudentById(Long id) {
        return repository.findStudentById(id); // Repository に対応するメソッドを追加する
    }

    public List<StudentsCourses> findCoursesByStudentId(Long studentId) {
        return repository.findCoursesByStudentId(studentId); // 受講生IDに関連付けられたコースを取得
    }

    public StudentDetail getStudentDetailById(Long id) {
        Student student = repository.findStudentById(id);
        if (student == null) {
            throw new IllegalArgumentException("Student not found with id: " + id);
        }
        List<StudentsCourses> courses = repository.findCoursesByStudentId(id);
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);
        detail.setStudentsCourses(courses);
        return detail;
    }

    @Transactional
    public void updateStudent(StudentDetail studentDetail) {
        if (studentDetail.getStudent() == null) {
            throw new IllegalArgumentException("Student object cannot be null.");
        }

        // 学生情報の更新
        repository.updateStudent(studentDetail.getStudent());

        for (StudentsCourses course : studentDetail.getStudentsCourses()) {
            if (course.getId() == null) {
                // 新規登録の場合
                course.setStudentId(studentDetail.getStudent().getId()); // student_idを設定
                course.setStartDate(LocalDate.now());
                course.setEndDate(LocalDate.now().plusYears(1));
                repository.registerStudentsCourses(course);
            } else {
                // 既存データの更新の場合
                repository.updateStudentsCourses(course);
            }
        }
    }
}
        /*本来はnewが入らないとインスタンスとして機能しないが、SpringBootの@Serviceで
        インスタンスとして呼び出すことが可能。更にAutowiredでStudentManagementApplicationの
        repositoryを呼び出せる。これを行うことで上書きが容易になる。
        @Autowiredとは、Springフレームワークで用いるアノテーションのひとつ。これを記述するだけで
        インスタンス化を１回で行える。また、クラス内のnew演算子を消すことができる。つまりこのクラスでは
        @Autowiredを使うことで全てのpublic変数をインスタンス化させ、newをいちいち記述する必要が
        ないようにしている。*/