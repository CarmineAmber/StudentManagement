package student.management.StudentManagement.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;
import student.management.StudentManagement.data.CourseStatusDTO;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;

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
    @JsonProperty("courseStatuses")
    @Valid
    private List<CourseStatusDTO> courseStatuses;

    /*検索やフィルター用の条件保持用フィールド。
    * 性別、コース名、受講状況による絞り込みのために使用する*/
    private String gender;
    private String courseName;
    private String courseStatus;

    /*主要なフィールド（Student,StudentsCourse,CourseStatusDTOを初期化するためのコンストラクタ*/
    public StudentDetail(Student student, List<StudentsCourse> studentCourseList, List<CourseStatusDTO> courseStatuses) {
        this.student = student;
        this.studentCourseList = studentCourseList;
        this.courseStatuses = courseStatuses;
    }
}
