package student.management.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;
/*lombokを使うことで、いちいちgetterとsetterを書く必要がなくなる。
 * クラス宣言の前にimport lombok.Getter,import lombok.Setter,
 * @Getter,@Setterを記述することでコードが読みやすくなる。*/

import java.time.LocalDate;
@Getter
@Setter
public class StudentsCourse {
    private Integer id;
    private Integer studentId;
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;
}
