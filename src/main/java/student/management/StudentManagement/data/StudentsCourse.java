package student.management.StudentManagement.data;

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
@Getter
@Setter
public class StudentsCourse {
    @Min(value = 1, message = "IDは1以上である必要があります。")
    private Integer id;

    @Min(value = 1, message = "IDは1以上である必要があります。")
    private Integer studentId;

    @NotBlank(groups = ValidationGroups.Create.class, message = "コース名は必須です。")
    private String courseName;

    private LocalDate startDate;

    private LocalDate endDate;
}
