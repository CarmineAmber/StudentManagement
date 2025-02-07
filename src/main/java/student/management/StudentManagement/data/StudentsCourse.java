package student.management.StudentManagement.data;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
/*lombokを使うことで、いちいちgetterとsetterを書く必要がなくなる。
 * クラス宣言の前にimport lombok.Getter,import lombok.Setter,
 * @Getter,@Setterを記述することでコードが読みやすくなる。*/

import java.time.LocalDate;
@Getter
@Setter
public class StudentsCourse {
    @NotBlank
    private Integer id;

    @NotBlank
    private Integer studentId;

    @NotBlank
    private String courseName;

    private LocalDate startDate;
    private LocalDate endDate;
}
