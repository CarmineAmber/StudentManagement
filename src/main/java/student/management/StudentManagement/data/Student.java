package student.management.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import student.management.StudentManagement.Validation.ValidationGroups;

import java.util.List;

/*lombokを使うことで、いちいちgetterとsetterを書く必要がなくなる。
 * クラス宣言の前にimport lombok.Getter,import lombok.Setter,
 * @Getter,@Setterを記述することでコードが読みやすくなる。*/

@Schema(description = "受講生")
@Getter
@Setter
public class Student {

    @Null(message = "新規登録時は id を指定しないでください。")
    @Min(value = 1,message = "IDは1以上である必要があります。")
    /*(regexp = "^\\d+$")とは、数値のみの文字列を検証するための正規表現。
    * ^で文字列の先頭からマッチし、\\d+で0~9までの数字を１回以上繰り返す。
    * $は文字列の末尾でマッチする。これらはユーザー入力が整数のみであることを
    * 確認する用途に使われる（主にIDや数量のバリデーション）*/
    private Integer id;

    /*@NotNullと@NotBlankを使用することで、nullと""の両方をバリデーションする。
    * つまり、確実にチェックに引っかかってエラーメッセージが表示されるようにしている*/
    @NotNull(message = "エラー: 名前が入力されていません。")
    @NotBlank(message = "エラー: 名前が入力されていません。")
    private String studentName;

    @NotNull(message = "エラー: フリガナが入力されていません。")
    @NotBlank(message = "エラー: フリガナが入力されていません。")
    private String furigana;

    @NotNull(message = "エラー: ニックネームが入力されていません。")
    @NotBlank(message = "エラー: ニックネームが入力されていません。")
    private String nickname;

    @NotNull(message = "エラー: メールアドレスが入力されていません。")
    @NotBlank(message = "エラー: メールアドレスが入力されていません。")
    @Email(message = "エラー: 正しいメールアドレスを入力してください。")
    private String email;

    @NotNull(message = "エラー: 地域が入力されていません。")
    @NotBlank(message = "エラー: 地域が入力されていません。")
    private String region;

    @Min(0)
    @Max(120)
    private Integer age;

    @NotNull(message = "エラー: 性別が入力されていません。")
    @NotBlank(message = "エラー: 性別が入力されていません。")
    private String gender;

    private String remark;
    private Boolean isDeleted;//論理削除

    // デフォルトコンストラクタ（必須）
    public Student() {
        /*テストコードを使用する際の引数なしのデフォルトコンストラクタ。
        * このコンストラクタは、実行される前にまず自分自身の親クラスの
        * コンストラクタを呼び出す。つまり、Studentクラスのコンストラクタ
        * 全般を呼び出すという動作をしている*/
    }

    public Student(Integer id, String studentName, String furigana, String nickname,
                   String email, String region, Integer age,String gender) {
        this.id = id;
        this.studentName = studentName;
        this.furigana = furigana;
        this.nickname = nickname;
        this.email = email;
        this.region = region;
        this.age = age;
        this.gender = gender;
    }
}
/*このprivate変数は、mySQLのStudentManagementテーブルから拾ってきた項目である。
 * private変数は、メソッドを経由しないと変数の値を格納できなくなる。*/