package student.management.StudentManagement.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;
import student.management.StudentManagement.data.CourseStatusDTO;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;

import java.util.ArrayList;
import java.util.List;

@Schema(description = "受講生詳細")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class StudentDetail {
    @JsonProperty("student")
    @Valid
    private Student student;

    @Setter
    @JsonProperty("studentCourseList")
    @Valid
    private List<StudentsCourse> studentCourseList;

    @Setter
    @JsonProperty("courseStatuses") // Add new property for course statuses
    @Valid
    private List<CourseStatusDTO> courseStatuses;

    // 既存のコンストラクタ
    public StudentDetail(Student student, List<StudentsCourse> studentCourseList, List<CourseStatusDTO> courseStatuses) {
        this.student = student;
        this.studentCourseList = studentCourseList;
        this.courseStatuses = courseStatuses;
    }
    /*studentとstudentCoursesの２つのクラスに表記されているものを
    * 繋ぎ合わせ、StudentDetailを作っている。
    * 尚、リストがnullになることによるサーバーエラーを防ぐために
    * new ArrayList<>を表示して初期化している。*/
    /*ValidをStudentsCoursesにも適用することで、登録チェック漏れを防いでいる*/
}
