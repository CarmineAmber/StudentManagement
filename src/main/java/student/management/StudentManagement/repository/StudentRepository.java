package student.management.StudentManagement.repository;

import org.apache.ibatis.annotations.*;
import student.management.StudentManagement.StudentsWithCourses;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;
import student.management.StudentManagement.domain.StudentDetail;

import java.util.List;

/*受講生情報を扱うリポジトリ。
 * 全件検索や単一条件での検索が行えるクラス。*/

@Mapper
public interface StudentRepository {

    @Select("SELECT * FROM students")
    List<Student> search();

    @Select("""
    SELECT
        id AS id,
        name AS studentName,
        furigana AS furigana,
        nickname AS nickname,
        email AS email,
        region AS region,
        age AS age,
        gender AS gender,
        remark AS remark,
        isdeleted AS isDeleted
    FROM students
    WHERE id = #{id}
""")
    Student searchStudent(Long id);

    /* 全てのコースを取得 */
    @Select("SELECT * FROM students_courses")
    List<StudentsCourses> searchAllCoursesList();

    @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
    List<StudentsCourses> searchAllCourses(Long studentId);

    @Select("SELECT id, name AS studentName, furigana, nickname AS nickName, " +
            "email, region, age, gender, remark, isdeleted AS isDeleted " +
            "FROM students WHERE isdeleted = false")
    List<Student> searchStudents();
    /*WHERE isdeleted = falseがないとリストに非表示にならない*/

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

    /*名前のみを登録する場合*/
    @Insert("""
            INSERT INTO students (name)
            VALUES (#{name})
            """)
    void insertStudentName(@Param("name") String name);

    @Select("SELECT * FROM courses")
    List<StudentsCourses> findAll();

    @Insert("""
                INSERT INTO students (name, furigana, nickname, email,
                region, age, gender, remark, isDeleted)
                VALUES (#{studentName}, #{furigana}, #{nickname}, #{email},
                #{region}, #{age}, #{gender}, #{remark}, false)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerStudent(Student student);

    @Insert("INSERT INTO students_courses(student_id, course_name, start_date, end_date)" +
            "VALUES(#{studentId}, #{courseName}, #{startDate}, #{endDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerStudentsCourses(StudentsCourses studentsCourses);

    @Select("SELECT id, name AS studentName, furigana, nickname AS nickName, email, " +
            "region, age, gender, remark FROM students WHERE id = #{id}")
    Student findStudentById(@Param("id") Long id);

    @Select("SELECT course_name AS courseName, start_date AS startDate, end_date AS endDate " +
            "FROM students_courses WHERE student_id = #{studentId}")
    List<StudentsCourses> findCoursesByStudentId(@Param("studentId") Long studentId);

    @Update("""
                UPDATE students
                SET
                    name = #{studentName},
                    furigana = #{furigana},
                    nickname = #{nickname},
                    email = #{email},
                    region = #{region},
                    age = #{age},
                    gender = #{gender},
                    remark = #{remark},
                    isdeleted = COALESCE(#{isDeleted}, false)
                WHERE
                    id = #{id}
            """)
    /*SETとWHEREをつけ、更にエイリアスをつける場合はこのように見やすくするといい*/
    int updateStudent(Student student);

    @Update("UPDATE students_courses SET course_name = #{courseName}, start_date = #{startDate}, end_date = #{endDate} WHERE id = #{id}")
    int updateStudentsCourses(StudentsCourses studentsCourses);

    @Insert("""
                INSERT INTO students_courses (student_id, course_name, start_date, end_date)
                VALUES (#{studentId}, #{courseName}, #{startDate}, #{endDate})
            """)
    void insertStudentsCourses(StudentsCourses studentsCourses);

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
                    isdeleted
                FROM
                    students
                WHERE
                    id = #{id}
            """)
    StudentDetail findStudentDetailById(@Param("id") Long id);

    @Update("UPDATE students SET isdeleted = #{isDeleted} WHERE id = #{id}")
    void updateIsDeleted(@Param("id") Long id, @Param("isDeleted") boolean isDeleted);

}
/* @Paramアノテーションを使うことで、動的にパラメータを渡すことができる。一例として、
   #{}というプレーズホルダーを使用することでSQLクエリ内で直接文字列を埋め込まないようにすることができ、
   SQLインジェクションを防げる。30代の受講生を取得するコードでは、WHERE　age BETWEEN #{ageStart}
   AND #{ageEnd}、List<Student> searchStudentsInAgeRange(@Param("ageStart") int ageStart,
   @Param("ageEnd") int ageEnd);とすることで検索対象となる特定の年齢に直接文字列を埋め込めないように
   している。
   全件検索、全てのコース名を取得、受講生とコース情報を結合して取得の場合はそもそも#{}を利用するケースが
   ないため、文字列そのものがクエリに埋め込まれることはない。*/
/*@Insert("INSERT INTO students_courses(student_id, course_name, start_date, end_date)" +
            "VALUES(#{studentId}, #{courseName}, #{startDate}, #{endDate})")は、一括でエイリアスをつけている。*/
/*chatGPTを使ってうまく動作しない場合は、リポジトリをその都度作成する必要あり？*/