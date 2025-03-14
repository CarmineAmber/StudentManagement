package student.management.StudentManagement.Controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.repository.StudentRepository;
import student.management.StudentManagement.service.StudentService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    /*モック(@Mock)をSpring ApplicationContextというアプリケーションの
    構成を提供する主要インターフェースに追加するためのアノテーション*/
    private StudentService service;

    @MockBean
    private StudentRepository studentRepository;

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void 受講生詳細の一覧検索が実行できて空のリストが返ってくること() throws Exception {
        mockMvc.perform(get("/studentList"))
                .andExpect(status().isOk());
        // verifyが正しく呼ばれるか確認
        verify(service, times(2)).searchStudentList();
    }

    @Test
    void 受講生詳細の受講生で適切な値を入力した時に入力チェックに異常が発生しないこと() {
        Student student = new Student(
                1,
                "Yuki",
                "ユキ",
                "Yuki",
                "yuki@example.com",
                "Saitama",
                26,
                "Female"
        );

        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        assertThat(violations).isEmpty();
    }

    @Test
    void 受講生詳細の受講生で適切な値を入力した時に入力チェックにかかること() {
        Student student = new Student(
                null,
                "Yuki",
                "ユキ",
                "Yuki",
                "yuki@example.com",
                "Saitama",
                26,
                "Female"
        );

        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        assertThat(violations.size()).isEqualTo(1);
        assertThat(violations).extracting("message").containsOnly("IDは必須です");
    }

    @Test
    void 受講生検索でIDを半角数字以外で入力した時に入力チェックにかかること() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("IDは半角数字で入力して下さい。"));
    }

    @Test
    void 受講生検索で該当のデータが存在しないというエラーメッセージを返すこと() throws Exception {
        when(service.searchStudent(anyLong())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/student/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("該当のデータが存在しません。"));
    }

    @Test
    public void 受講生を登録した際に異常が発生しないこと() throws Exception {
        String requestBody = "{ \"student\": {\"studentName\": \"John Doe\", \"furigana\": \"ジョン・ドウ\", \"nickname\": \"Johnny\", \"email\": \"john.doe@example.com\", \"region\": \"Tokyo\", \"age\": 20, \"gender\": \"Male\", \"remark\": \"No remarks\", \"isDeleted\": false }, \"studentCourseList\": [{ \"studentId\": 1, \"courseName\": \"JAVA\", \"startDate\": \"2025-03-01\", \"endDate\": \"2025-06-01\" }] }";

        MvcResult result = mockMvc.perform(post("/registerStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())  // ここで 200 OK を期待
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Response Content: " + content);
    }

    @AfterEach
    void tearDown() {
    }
}
