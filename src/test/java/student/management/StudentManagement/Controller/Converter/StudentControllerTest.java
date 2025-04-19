package student.management.StudentManagement.Controller.Converter;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import student.management.StudentManagement.Controller.StudentController;
import student.management.StudentManagement.data.CourseStatusDTO;
import student.management.StudentManagement.data.CourseStatusUpdateRequest;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.exception.StudentUpdateException;
import student.management.StudentManagement.repository.StudentRepository;
import student.management.StudentManagement.service.StudentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc  // MockMvcを自動で設定
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvcをインジェクト

    @Autowired
    private StudentRepository studentRepository;

    @MockBean  // 依存関係のモック
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // モックの初期化。テストクラスの各テストメソッドの実行前に毎回実行される。
    }

    @Test
    void 受講生の一覧検索() throws Exception {
        StudentDetail studentDetail = new StudentDetail(); // モックデータを作成
        studentDetail.setStudent(new Student(1, "Taro", "タロウ", "Taro", "taro@example.com", "Tokyo", 20, "Male"));
        List<StudentDetail> studentDetails = List.of(studentDetail);

        when(studentService.getAllStudentsWithCourseStatuses()).thenReturn(studentDetails);

        mockMvc.perform(get("/studentList"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].student.studentName").value("Taro"));
    }

    @Test
    void 受講生登録() throws Exception {
        // モックデータの作成
        Student student = new Student(1, "Taro", "タロウ", "Taro", "taro@example.com", "Tokyo", 20, "Male");
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);

        // サービスのモックを設定
        when(studentService.registerStudent(any(StudentDetail.class))).thenReturn(studentDetail);

        // MockMvcでPOSTリクエストを送信
        mockMvc.perform(post("/registerStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDetail)))
                .andExpect(status().isOk())  // ステータスコード200 OKを期待
                .andExpect(jsonPath("$.student.studentName").value("Taro"));  // student.studentNameが"Taro"であることを確認
    }

    @Test
    void 受講生更新() throws Exception {
        StudentDetail studentDetail = new StudentDetail(); // モックデータを作成
        studentDetail.setStudent(new Student(1, "Taro", "タロウ", "Taro", "taro@example.com", "Tokyo", 20, "Male"));

        mockMvc.perform(post("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentDetail)))
                .andExpect(status().isOk())
                .andExpect(content().string("学生情報とコース情報の更新に成功しました。"));
    }

    @Test
    void 受講生検索() throws Exception {
        StudentDetail studentDetail = new StudentDetail(); // モックデータを作成
        studentDetail.setStudent(new Student(1, "Taro", "タロウ", "Taro", "taro@example.com", "Tokyo", 20, "Male"));

        when(studentService.searchStudent(1, null)).thenReturn(studentDetail);

        mockMvc.perform(get("/student")
                        .param("studentId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.student.studentName").value("Taro"));
    }

    @Test
    void 性別で受講生検索() throws Exception {
        StudentDetail studentDetail = new StudentDetail(); // モックデータを作成
        studentDetail.setStudent(new Student(1, "Taro", "タロウ", "Taro", "taro@example.com", "Tokyo", 20, "Male"));
        List<StudentDetail> studentDetails = List.of(studentDetail);

        when(studentService.searchStudentsByGender("Male")).thenReturn(studentDetails);

        mockMvc.perform(get("/studentList/gender")
                        .param("gender", "Male"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].student.studentName").value("Taro"));
    }

    @Test
    void コースによる受講生検索() throws Exception {
        StudentDetail studentDetail = new StudentDetail(); // モックデータを作成
        studentDetail.setStudent(new Student(1, "Taro", "タロウ", "Taro", "taro@example.com", "Tokyo", 20, "Male"));
        List<StudentDetail> studentDetails = List.of(studentDetail);

        when(studentService.searchStudentsByCourseName("Java Basics")).thenReturn(studentDetails);

        mockMvc.perform(get("/studentList/course")
                        .param("courseName", "Java Basics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].student.studentName").value("Taro"));
    }

    @Test
    void 受講生のコース受講状況を取得() throws Exception {
        CourseStatusDTO courseStatus = new CourseStatusDTO(1, "受講中");
        List<CourseStatusDTO> courseStatusList = List.of(courseStatus);

        when(studentService.getStudentCourseStatus(1)).thenReturn(courseStatusList);

        mockMvc.perform(get("/student/{studentId}/courses/status", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("受講中"));
    }

    @Test
    void 受講生の受講状況更新() throws Exception {
        CourseStatusUpdateRequest request = new CourseStatusUpdateRequest(1, "受講中");

        mockMvc.perform(put("/courses/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("受講状況を更新しました"));
    }
}
