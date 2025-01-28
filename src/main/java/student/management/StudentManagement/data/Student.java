package student.management.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;
/*lombokを使うことで、いちいちgetterとsetterを書く必要がなくなる。
 * クラス宣言の前にimport lombok.Getter,import lombok.Setter,
 * @Getter,@Setterを記述することでコードが読みやすくなる。*/

@Getter
@Setter
public class Student {
    private Integer id;
    private String studentName;
    private String furigana;
    private String nickname;
    private String email;
    private String region;
    private Integer age;
    private String gender;
    private String remark;
    private Boolean isDeleted; //論理削除
}
/*このprivate変数は、mySQLのStudentManagementテーブルから拾ってきた項目である。
 * private変数は、メソッドを経由しないと変数の値を格納できなくなる。*/