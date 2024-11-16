package student.management.StudentManagement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import student.management.StudentManagement.StudentsWithCourses;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;
import student.management.StudentManagement.repository.StudentRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class StudentService {

    private StudentRepository repository;

    @Autowired

    public StudentService(StudentRepository repository) {
        this.repository = repository;
        /*本来はnewが入らないとインスタンスとして機能しないが、SpringBootの@Serviceで
        インスタンスとして呼び出すことが可能。更にAutowiredでStudentManagementApplicationの
        repositoryを呼び出せる。これを行うことで上書きが容易になる。*/
    }

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
        return repository.searchStudentsIn30s();
    }

    public List<StudentsWithCourses> searchJavaCourse() {
        return repository.searchJavaCourse();
    }

    public List<Student> fetchStudentsFromApi() {
        try {
            /* URLの設定 */
            URL url = new URL("http://localhost:8080/studentList");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            // InputStreamの読み込み
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line.trim());  // 改行やスペースを削除して結合
            }
            reader.close();

            // JSONのパース
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
}
