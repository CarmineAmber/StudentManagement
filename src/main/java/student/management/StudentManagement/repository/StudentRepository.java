package student.management.StudentManagement.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import student.management.StudentManagement.StudentsWithCourses;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;

import java.util.List;

/*受講生情報を扱うリポジトリ。
 * 全件検索や単一条件での検索が行えるクラス。*/

@Mapper
public interface StudentRepository {

    /* 全件検索を行う。 */
    @Select("""
                SELECT
                    id,
                    name AS studentName,
                    furigana,
                    nickname AS nickName,
                    email,
                    region,
                    age,
                    gender,
                    remark,
                    isdeleted AS isDeleted
                FROM
                    students
            """)
    List<Student> searchStudents();

    /* 全てのコースを取得 */
    @Select("SELECT * FROM students_courses")
    List<StudentsCourses> searchAllCourses();

    /* 受講生とコース情報を結合して取得 */
    @Select("""
                SELECT
                    s.id AS studentId,
                    s.name AS studentName,
                    s.furigana,
                    s.nickname AS nickName,
                    s.email,
                    s.region,
                    s.age,
                    s.gender,
                    sc.course_name AS courseName,
                    sc.start_date AS startDate,
                    sc.end_date AS endDate
                FROM
                    students s
                LEFT JOIN
                    students_courses sc
                ON
                    s.id = sc.student_id
            """)
    List<StudentsWithCourses> searchStudentsWithCourses();
    /* このコードでmySQLのStudentManagementからStudentWithCoursesのリストに必要なデータを
     * 引っ張ってきている。s.〜はstudentsテーブルから引っ張り出した項目、sc.はstudents_coursesから
     * 引っ張り出した項目である。FROM students s LEFT JOIN students_courses sc ON s.id=
     * sc.student_idとは、studentsテーブルのidとstudents_coursesテーブルのstudent_idを介して
     * studentsテーブルにstudents_coursesテーブルを結合させ、JAVAでStudentsWithCoursesリストと
     * して出している。
     * s.id AS studentIdのASは列に別名（エイリアス）をつけるためのキーワード。ここではmySQLにおけるidを
     * JAVAではstudentIdという別名を使っている（これがエイリアス）。これによって列名の可読性が向上し、
     * JOINを行う場合の競合を避けることが可能。また、JAVAでの処理も簡単になる。
     * 全てのリストを表示する場合、カラムにエイリアスをつけなければnullと表示されてしまうので注意すること。*/

    /* 30代の受講生を取得 */
    @Select("""
                SELECT
                    id AS Id,
                    name AS studentName,
                    furigana,
                    nickname AS nickName,
                    email,
                    region,
                    age,
                    gender
                FROM
                    students
                WHERE
                    age BETWEEN #{ageStart} AND #{ageEnd}
            """)
    List<Student> searchStudentsInAgeRange(
            @Param("ageStart") int ageStart,
            @Param("ageEnd") int ageEnd
    );

    /* 特定のコース名で受講生を取得 */
    @Select("""
                SELECT
                    s.id AS studentId,
                    s.name AS studentName,
                    s.furigana,
                    s.nickname AS nickName,
                    s.email,
                    s.region,
                    s.age,
                    s.gender,
                    sc.course_name AS courseName,
                    sc.start_date AS startDate,
                    sc.end_date AS endDate
                FROM
                    students s
                JOIN
                    students_courses sc
                ON
                    s.id = sc.student_id
                WHERE
                    sc.course_name = #{courseName}
            """)
    List<StudentsWithCourses> searchStudentsByCourseName(
            @Param("courseName") String courseName
    );
}
/* @Paramアノテーションを使うことで、動的にパラメータを渡すことができる。一例として、
   #{}というプレーズホルダーを使用することでSQLクエリ内で直接文字列を埋め込まないようにすることができ、
   SQLインジェクションを防げる。30代の受講生を取得するコードでは、WHERE　age BETWEEN #{ageStart}
   AND #{ageEnd}、List<Student> searchStudentsInAgeRange(@Param("ageStart") int ageStart,
   @Param("ageEnd") int ageEnd);とすることで検索対象となる特定の年齢に直接文字列を埋め込めないように
   している。
   全件検索、全てのコース名を取得、受講生とコース情報を結合して取得の場合はそもそも#{}を利用するケースが
   ないため、文字列そのものがクエリに埋め込まれることはない。*/