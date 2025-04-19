package student.management.StudentManagement.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/*受講状況に関するクラス。DTOとは、Data Transfer Objectの略である*/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatusDTO {
    private Integer studentsCoursesId;
    private String courseName;

    @JsonProperty
    private String status;
    private Integer courseId;

    // 引数を受け取るコンストラクタ
    public CourseStatusDTO(Integer studentsCoursesId, String status) {
        this.studentsCoursesId = studentsCoursesId;
        this.status = status;
    }

    /*これら２つのOverrideは、StudentsCoursesIdのみを元に判断し、studentsCourseIdが
     * 同じであれば同一とみなすためのものである。*/
    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null || getClass() != obj.getClass() ) return false;
        CourseStatusDTO that = (CourseStatusDTO) obj;
        return Objects.equals(studentsCoursesId, that.studentsCoursesId);  // Null-safe equals check
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentsCoursesId);  // Null-safe hashCode
    }
}
