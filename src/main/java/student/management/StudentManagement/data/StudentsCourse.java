package student.management.StudentManagement.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    private Integer id;

    @Min(value = 1, message = "IDは1以上である必要があります。")
    @JsonProperty("studentId")
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
