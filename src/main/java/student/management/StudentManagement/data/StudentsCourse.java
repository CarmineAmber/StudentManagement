package student.management.StudentManagement.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import student.management.StudentManagement.Validation.ValidationGroups;
/*lombokを使うことで、いちいちgetterとsetterを書く必要がなくなる。
 * クラス宣言の前にimport lombok.Getter,import lombok.Setter,
 * @Getter,@Setterを記述することでコードが読みやすくなる。*/

import java.time.LocalDate;
@Schema(description = "受講生コース情報")
@Getter
@Setter
public class StudentsCourse {
    @Min(value = 1, message = "IDは1以上である必要があります。")
    @Null(message = "新規登録時は courseId を指定しないでください。")
    private Integer courseId;

    @Min(value = 1, message = "IDは1以上である必要があります。")
    @Null(message = "新規登録時は studentId を指定しないでください。")
    @JsonProperty("studentId")
    @Column(name = "student_id")
    private Integer studentId;

    @NotBlank(message = "コース名は必須です。")
    @NotNull(message = "コース名は必須です。")
    @JsonProperty("courseName")
    private String courseName;

    private LocalDate startDate;

    private LocalDate endDate;

    public StudentsCourse(){

    }

    public StudentsCourse(Integer studentId, String courseName){
        this.studentId = studentId;
        this.courseName = courseName;
    }
}
