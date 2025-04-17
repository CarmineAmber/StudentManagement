package student.management.StudentManagement.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CourseStatusDTO {
    private Integer studentsCoursesId;
    private String courseName;

    @JsonProperty
    private String status;
    private Integer courseId;

    // 引数を受け取るコンストラクタを追加
    public CourseStatusDTO(Integer studentsCoursesId, String status) {
        this.studentsCoursesId = studentsCoursesId;
        this.status = status;
    }

    public Integer getStudentsCoursesId() {
        return studentsCoursesId;
    }

    public void setStudentsCoursesId(Integer studentsCoursesId) {
        this.studentsCoursesId = studentsCoursesId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        CourseStatusDTO that = (CourseStatusDTO) obj;
        return Objects.equals(studentsCoursesId, that.studentsCoursesId);  // Null-safe equals check
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentsCoursesId);  // Null-safe hashCode
    }
}
