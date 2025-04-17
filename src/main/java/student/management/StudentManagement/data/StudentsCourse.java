package student.management.StudentManagement.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import student.management.StudentManagement.Validation.OnCreate;
import student.management.StudentManagement.Validation.OnUpdate;
/*lombokを使うことで、いちいちgetterとsetterを書く必要がなくなる。
 * クラス宣言の前にimport lombok.Getter,import lombok.Setter,
 * @Getter,@Setterを記述することでコードが読みやすくなる。*/

import java.time.LocalDate;

@Schema(description = "受講生コース情報")
@Getter
@Setter
public class StudentsCourse {

    /*リポジトリにidが存在するため、SQLマッピングのために必要となる。*/
    @Null(groups = OnCreate.class, message = "新規登録時は studentId を指定しないでください。")
    @NotNull(groups = OnUpdate.class, message = "更新時は studentId を指定してください。")
    private Integer id;

    @Null(groups = OnCreate.class, message = "新規登録時は courseId を指定しないでください。")
    @NotNull(groups = OnUpdate.class, message = "更新時は courseId を指定してください。")
    private Integer courseId;

    @Null(groups = OnCreate.class, message = "新規登録時は studentId を指定しないでください。")
    @NotNull(groups = OnUpdate.class, message = "更新時は studentId を指定してください。")
    @JsonProperty("studentId")
    @Column(name = "student_id")
    private Integer studentId;

    @NotBlank(message = "コース名は必須です。")
    @NotNull(message = "コース名は必須です。")
    @JsonProperty("courseName")
    private String courseName;

    @JsonProperty("startDate")
    private LocalDate startDate;

    @JsonProperty("endDate")
    private LocalDate endDate;

    /*startDateとendDateをString形式で出力するためのカスタムシリアライズ処理*/
    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate getStartDate() {
        return startDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd")
    public LocalDate getEndDate() {
        return endDate;
    }

    public StudentsCourse(){
    }

    public StudentsCourse(Integer id, LocalDate startDate, LocalDate endDate, String status, Integer studentId, String courseName) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.studentId = studentId;
        this.courseName = courseName;
    }


    private String status;

    // 状態を取得
    public String getStatus() {
        return status;
    }

    // 状態を設定
    public void setStatus(String status) {
        this.status = status;
    }
}
