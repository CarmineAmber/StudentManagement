package student.management.StudentManagement.Controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.util.stream.Stream;

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
        verify(service, times(1)).searchStudentList();
    }

    @Test
    void 受講生詳細の受講生で適切な値を入力した時に入力チェックに異常が発生しないこと() {
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

        assertThat(violations).isEmpty();
    }

    @Test
    void 受講生詳細の受講生で入力に不備があった際に入力チェックにかかること() {
        Student student = new Student(
                null,
                "Yuki",
                "ユキ",
                "Yuki",
                "yuki@example.com",
                "",
                26,
                "Female"
        );

        Set<ConstraintViolation<Student>> violations = validator.validate(student);

        assertThat(violations).isNotEmpty();  // エラーが発生することを確認
        assertThat(violations).extracting("message").containsOnly("エラー: 地域が入力されていません。");
    }
    /*このテストは、入力で必須となっているものが入力されていない場合、エラーを吐くようにしている。
    * エラーメッセージはStudent及びStudentsCourseクラスのものと同一でなければ失敗する*/

    @Test
    void 受講生検索でIDを半角数字以外で入力した時に入力チェックにかかること() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/student/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("IDは半角数字で入力して下さい。"));
    }

    @Test
    void 受講生詳細の検索が実行できて空で返ってくること() throws Exception {
        Long id = 999L;

        // searchStudent(id) が null を返すようにモックを設定
        when(service.searchStudent(id)).thenReturn(null);

        mockMvc.perform(get("/student/{id}", id)) // ルートのスラッシュを追加
                .andExpect(status().isNotFound()); // 閉じカッコの位置修正

        verify(service, times(1)).searchStudent(id);
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
        /*リクエストデータは適切に構築し、入力チェックの検証も兼ねている。
        * 本来であれば返りは登録されたデータが入るが、モック化すると意味がないため
        * レスポンスは作らない*/
        String requestBody = "{ \"student\": {\"id\":null,\"studentName\": \"John Doe\", \"furigana\": \"ジョン・ドウ\", \"nickname\": \"Johnny\", \"email\": \"john.doe@example.com\", \"region\": \"Tokyo\", \"age\": 20, \"gender\": \"Male\", \"remark\": \"No remarks\", \"isDeleted\": false }, \"studentCourseList\": [{ \"courseId\": null,\"studentId\": null, \"courseName\": \"JAVA\", \"startDate\": \"2025-03-01\", \"endDate\": \"2025-06-01\" }] }";
        /*PostManで表示されている全ての項目がなければテストができない。また、idは
        自動的に付与されるため、nullである必要がある。*/
        MvcResult result = mockMvc.perform(post("/registerStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andReturn();  // ステータスチェックを一旦外す

        // ステータスコードを出力
        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        System.out.println("Response Status: " + status);
        System.out.println("Response Content: " + content);

// ステータスコードが 200 以外の場合、失敗させる
        assertEquals(200, status, "Unexpected response status.");
    }

    @Test
    void 受講生を更新した際に異常が発生しないこと() throws Exception {
        // リクエストボディ（更新データ）
        String requestBody = "{ \"student\": {\"id\": 1, \"studentName\": \"John Doe\", \"furigana\": \"ジョン・ドウ\", \"nickname\": \"Johnny\", \"email\": \"john.doe@example.com\", \"region\": \"Tokyo\", \"age\": 21, \"gender\": \"Male\", \"remark\": \"Updated remark\", \"isDeleted\": false }, \"studentCourseList\": [{ \"courseId\": 1, \"studentId\": 1, \"courseName\": \"JAVA\", \"startDate\": \"2025-03-01\", \"endDate\": \"2025-06-01\" }] }";

        // モックの設定（例外を投げない = 正常終了）
        doNothing().when(service).updateStudentWithCourses(any(StudentDetail.class));

        // API を実行
        MvcResult result = mockMvc.perform(post("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())  // 200 OK を期待
                .andReturn();

        // ステータスコードの確認
        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        System.out.println("Response Status: " + status);
        System.out.println("Response Content: " + content);

        // ステータスコードが 200 であることを確認
        assertEquals(200, status, "Unexpected response status.");
        assertEquals("学生情報とコース情報の更新に成功しました。", content);

        // サービスメソッドが1回呼び出されたことを確認
        verify(service, times(1)).updateStudentWithCourses(any(StudentDetail.class));
    }

    @Test
    void 受講生更新でバリデーションエラーが発生した場合に400が返ること() throws Exception {
        String invalidRequestBody = "{ \"student\": {\"id\": 1, \"studentName\": \"\", \"furigana\": \"\", \"nickname\": \"\", \"email\": \"\", \"region\": \"\", \"age\": -1, \"gender\": \"\", \"remark\": \"\", \"isDeleted\": false }, \"studentCourseList\": [] }";

        // サービスメソッドが `IllegalStateException` をスローするように設定
        doThrow(new IllegalStateException("入力値が不正です")).when(service).updateStudentWithCourses(any(StudentDetail.class));

        MvcResult result = mockMvc.perform(post("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest()) // 400 Bad Request を期待
                .andReturn();

        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        System.out.println("Response Status: " + status);
        System.out.println("Response Content: " + content);

        assertEquals(400, status, "Unexpected response status.");
        assertEquals("更新処理が失敗しました: 入力値が不正です", content);
    }

    @Test
    void 受講生更新でサーバーエラーが発生した場合に500が返ること() throws Exception {
        String validRequestBody = "{ \"student\": {\"id\": 1, \"studentName\": \"John Doe\", \"furigana\": \"ジョン・ドウ\", \"nickname\": \"Johnny\", \"email\": \"john.doe@example.com\", \"region\": \"Tokyo\", \"age\": 21, \"gender\": \"Male\", \"remark\": \"Updated remark\", \"isDeleted\": false }, \"studentCourseList\": [{ \"courseId\": 1, \"studentId\": 1, \"courseName\": \"JAVA\", \"startDate\": \"2025-03-01\", \"endDate\": \"2025-06-01\" }] }";

        // サービスメソッドが `Exception` をスローするように設定
        doThrow(new RuntimeException("予期しないエラー")).when(service).updateStudentWithCourses(any(StudentDetail.class));

        MvcResult result = mockMvc.perform(post("/student/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequestBody))
                .andExpect(status().isInternalServerError()) // 500 Internal Server Error を期待
                .andReturn();

        int status = result.getResponse().getStatus();
        String content = result.getResponse().getContentAsString();

        System.out.println("Response Status: " + status);
        System.out.println("Response Content: " + content);

        assertEquals(500, status, "Unexpected response status.");
        assertEquals("サーバーエラーが発生しました: 予期しないエラー", content);
    }

    @AfterEach
    void tearDown() {
    }
}
