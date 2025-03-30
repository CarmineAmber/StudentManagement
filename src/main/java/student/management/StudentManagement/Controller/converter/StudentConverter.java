package student.management.StudentManagement.Controller.converter;

import org.springframework.stereotype.Component;
import student.management.StudentManagement.data.CourseStatusDTO;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*受講生詳細を受講生や受講生コース情報、もしくはその逆の変換を行うコンバーター*/
@Component
public class StudentConverter {
    /*受講生に紐づく受講生コース情報をマッピングする。
    * 受講生コース情報は受講生に対して複数存在するのでループを回して受講生詳細情報を組み立てる。
    * @param students 受講生一覧
    * @param studentsCourses 受講生コース情報のリスト
    * @return 受講生詳細情報のリスト*/
    public List<StudentDetail> convertStudentDetails
    (List<Student> students, List<StudentsCourse> studentsCourses) {
        List<StudentDetail> studentDetails = new ArrayList<>();
        students.forEach(student -> {
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student);

            List<StudentsCourse> convertstudentsCourseList = studentsCourses.stream()
                    .filter(studentCourse -> student.getId().equals(studentCourse.getStudentId()))
                    .collect(Collectors.toList());
            /*SQLにコース登録の失敗で積み重なったNullが表示されていたため、サーバーエラーが発生していた。
            *.filter(studentCourse -> studentCourse.getStudentId() != null)を使うことで
            *NULLのデータを除外できる*/

            studentDetail.setStudentCourseList(convertstudentsCourseList);
            studentDetails.add(studentDetail);
        });

        // デバッグ用ログ
        studentDetails.forEach(detail -> {
            System.out.println("Student: " + detail.getStudent().getId());
            detail.getStudentCourseList().forEach(course ->
                    System.out.println("Course: " + course.getCourseName()));
        });

        return studentDetails;
    }
}

/*このクラスの役割は、特定のデータ型やオブジェクトを別の型やフォーマットに変換すること。
 *例：文字列を数値に変換する、データベースエンティティ（管理すべき情報）とデータ転送オブジェクトを変換する。
 *特にデータベースエンティティとデータ転送オブジェクトを分離することでレスポンスを分離可能である。
 *また、データベースや外部システム（API、XML、JSONなど）から受け取ったデータを、
 *アプリケーションの内部モデルに変換したり、その逆を行うことが可能である。
 *尚、Spring FrameworkではConverterを使うことで特定の型変換ロジックをカプセル化する。*/