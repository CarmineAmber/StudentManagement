package student.management.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;
/*lombokを使うことで、いちいちgetterとsetterを書く必要がなくなる。
 * クラス宣言の前にimport lombok.Getter,import lombok.Setter,
 * @Getter,@Setterを記述することでコードが読みやすくなる。*/

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class StudentsCourses {
    private int id;
    private String studentId;
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;
}
