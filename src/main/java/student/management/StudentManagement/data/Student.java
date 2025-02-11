package student.management.StudentManagement.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
/*lombokを使うことで、いちいちgetterとsetterを書く必要がなくなる。
 * クラス宣言の前にimport lombok.Getter,import lombok.Setter,
 * @Getter,@Setterを記述することでコードが読みやすくなる。*/

@Getter
@Setter
public class Student {

    @NotBlank
    @Pattern(regexp = "^\\d+$")
    /*(regexp = "^\\d+$")とは、数値のみの文字列を検証するための正規表現。
    * ^で文字列の先頭からマッチし、\\d+で0~9までの数字を１回以上繰り返す。
    * $は文字列の末尾でマッチする。これらはユーザー入力が整数のみであることを
    * 確認する用途に使われる（主にIDや数量のバリデーション）*/
    private Integer id;

    @NotBlank
    private String studentName;

    @NotBlank
    private String furigana;

    @NotBlank
    private String nickname;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String region;

    @Pattern(regexp = "^\\d+$")
    private Integer age;

    @NotBlank
    private String gender;

    private String remark;
    private Boolean isDeleted; //論理削除
}
/*このprivate変数は、mySQLのStudentManagementテーブルから拾ってきた項目である。
 * private変数は、メソッドを経由しないと変数の値を格納できなくなる。*/