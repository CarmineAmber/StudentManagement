package student.management.StudentManagement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public List<StudentsCourses> searchAllCourses() {
        return repository.searchAllCourses();
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
            /*UTF_8を設定しないと文字化けする。*/
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
    public void registerStudentName(String studentName) {
        if (studentName == null || studentName.isEmpty()) {
            throw new IllegalArgumentException("名前が空です。");
        }

        // リポジトリを使用して名前を登録
        repository.insertStudentName(studentName);
    }
}
        /*本来はnewが入らないとインスタンスとして機能しないが、SpringBootの@Serviceで
        インスタンスとして呼び出すことが可能。更にAutowiredでStudentManagementApplicationの
        repositoryを呼び出せる。これを行うことで上書きが容易になる。
        @Autowiredとは、Springフレームワークで用いるアノテーションのひとつ。これを記述するだけで
        インスタンス化を１回で行える。また、クラス内のnew演算子を消すことができる。つまりこのクラスでは
        @Autowiredを使うことで全てのpublic変数をインスタンス化させ、newをいちいち記述する必要が
        ないようにしている。*/
