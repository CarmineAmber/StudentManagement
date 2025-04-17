package student.management.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;
/*lombokを使うことで、いちいちgetterとsetterを書く必要がなくなる。
 * クラス宣言の前にimport lombok.Getter,import lombok.Setter,
 * @Getter,@Setterを記述することでコードが読みやすくなる。*/

import java.util.Date;

@Getter
@Setter
public class StudentsWithCourses {
    private int studentId;
    private String studentName;
    private String furigana;
    private String nickName;
    private String email;
    private String region;
    private int age;
    private String gender;
    private String courseName;
    private Date startDate;
    private Date endDate;
}
/*受講生と*/
