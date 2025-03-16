package student.management.StudentManagement.Controller.Converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import student.management.StudentManagement.Controller.converter.StudentConverter;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StudentConverterTest {

    private StudentConverter converter;

    @BeforeEach
    void setUp() {
        converter = new StudentConverter();
    }

    @Test
    void 受講生と受講生コースが正しく紐づくこと() {
        // 受講生データ
        Student student1 = new Student(1, "John Doe", "ジョン・ドウ", "Johnny",
                "john@example.com", "Tokyo", 20, "Male");

        Student student2 = new Student(2, "Jane Doe", "ジェーン・ドウ", "Janey",
                "jane@example.com", "Osaka", 22, "Female");
        /*public Student(Integer id, String studentName, String furigana, String nickname,
          String email, String region, Integer age,String gender)の（）以外を入れてしまうと
          失敗する。つまりremarksとisDeletedが入ってしまうとエラーが出ることになる*/

        // 受講生コースデータ
        StudentsCourse course1 = new StudentsCourse(1, "JAVA");
        StudentsCourse course2 = new StudentsCourse(1, "Excel Master");
        StudentsCourse course3 = new StudentsCourse(2,"Web Design");
        /*public StudentsCourse(Integer studentId, String courseName)の()以外を入れてしまうと
        * 失敗する*/

        List<StudentDetail> result = converter.convertStudentDetails(
                List.of(student1, student2),
                List.of(course1, course2, course3)
        );

        assertThat(result).hasSize(2);

        // Student 1 の検証
        assertThat(result.get(0).getStudent().getId()).isEqualTo(1);
        assertThat(result.get(0).getStudentCourseList()).hasSize(2);
        assertThat(result.get(0).getStudentCourseList())
                .extracting("courseName")
                .containsExactlyInAnyOrder("JAVA", "Excel Master");
        /*containsExactlyInAnyOrderを使用することで、コース名の順番に関係なく、期待されるコース名が
        一致していればテストが成功する。このテストケースでは、コース名の順番を気にせず、各学生に対して
        正しいコースが関連付けられているかを確認するために、containsExactlyInAnyOrderを使用した。
        そのため一致させる必要はなく、順番が違っていてもテストは成功する*/

        // Student 2 の検証
        assertThat(result.get(1).getStudent().getId()).isEqualTo(2);
        assertThat(result.get(1).getStudentCourseList()).hasSize(1);
        assertThat(result.get(1).getStudentCourseList())
                .extracting("courseName")
                .containsExactly("Web Design");
    }

    @Test
    void 受講生コースがない場合_空リストが返ること() {
        Student student = new Student(1, "John Doe", "ジョン・ドウ", "Johnny", "john@example.com", "Tokyo", 20, "Male");

        List<StudentDetail> result = converter.convertStudentDetails(List.of(student), List.of());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStudentCourseList()).isEmpty();
    }

    @Test
    void 受講生リストが空の場合_空リストが返ること() {
        List<StudentDetail> result = converter.convertStudentDetails(List.of(), List.of());
        assertThat(result).isEmpty();
    }

    @Test
    void 受講生コースのStudentIdがnullの場合_convertStudentDetailsメソッドが適切に動作すること() {
        Student student = new Student(1, "John Doe", "ジョン・ドウ", "Johnny", "john@example.com", "Tokyo", 20, "Male");
        StudentsCourse course = new StudentsCourse(null, "JAVA");; // StudentIdがnull

        List<StudentDetail> result = converter.convertStudentDetails(List.of(student), List.of(course));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStudentCourseList()).isEmpty(); // 紐づかないので空のはず
    }
}

