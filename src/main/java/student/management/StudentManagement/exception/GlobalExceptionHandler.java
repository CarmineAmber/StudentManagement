package student.management.StudentManagement.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/*エラーメッセージの一覧*/
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    /*@RestControllerAdviceは、エラーハンドリングを一元化するためのフレームワーク*/

    @ExceptionHandler(TestException.class)
    public ResponseEntity<String> handleTestException(TestException ex) {
        logger.error("エラー発生: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
    /*HttpStatusとは、Webサーバからのレスポンスの意味を表現する3桁の数字からなるコード。*/

    /*受講生が見つからない際の例外処理。404 Not Foundを表示させる*/
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<String> handleStudentNotFoundException(StudentNotFoundException ex) {
        logger.warn("検索エラー: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /*受講生情報更新時の例外処理。400 Bad Requestを表示させる*/
    @ExceptionHandler(StudentUpdateException.class)
    public ResponseEntity<String> handleStudentUpdateException(StudentUpdateException ex) {
        logger.error("更新エラー: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /*受講生情報登録時の例外処理。500 Internal Server Errorを表示させる*/
    @ExceptionHandler(StudentRegistrationException.class)
    public ResponseEntity<String> handleStudentRegistrationException(StudentRegistrationException ex) {
        logger.error("登録エラー: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }

    /*予期せぬエラーが発生した際の例外処理。500 Internal Server Errorを表示させる*/
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        logger.error("Unexpected Error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("処理中に予期せぬエラーが発生しました。");
    }

    /*@Validのついた箇所でエラーが発生した際の例外処理。
    * このアプリにおいては、StudentDetailクラスのStudent及びprivate List
    <@Valid StudentsCourse> studentCourseList= new ArrayList<>();が対象*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /*プログラム内部で不正な引数(このアプリにおいてはidが0未満あるいは空白)が渡された際の例外処理。
    Studentクラス内@Min(value = 1,message = "IDは1以上である必要があります。")
    *private Integer id;及び
    StudentsCourse内@Min(value = 1, message = "IDは1以上である必要があります。")
    private Integer id;、
    @Min(value = 1, message = "IDは1以上である必要があります。")
    private Integer studentId;が対象*/
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /*PostManを使用した際に表示されるフィールド名の整形メソッド*/
    private String formatFieldName(String fieldName) {
        fieldName = fieldName.replaceAll("\\[\\d+\\]", ""); // [0] を削除
        if ( fieldName.contains(".") ) {
            fieldName = fieldName.substring(fieldName.lastIndexOf(".") + 1); // 最後の.以降を取得
        }
        return fieldName;
    }
}
