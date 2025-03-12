package student.management.StudentManagement.repository;

import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import student.management.StudentManagement.StudentsWithCourses;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;

import java.util.List;
import java.util.Optional;

/*受講生テーブルと受講生コース情報テーブル(mySQL データベース名StudentManagement)と紐づくリポジトリ*/

@Mapper
@Repository
public interface StudentRepository {

    /*受講生一覧検索機能。
     * 全件検索を行うため、条件指定は行わない。
     * @return 受講生一覧（全件検索）*/
    @Select("SELECT id, name AS studentName, furigana, nickname, email, region, age, gender, remark, isdeleted FROM students")
    List<Student> searchAllStudents();

    /*受講生検索。
     * IDに紐づく任意の受講生の情報を取得する。
     * @param id 受講生ID
     * @return 受講生*/
    @Select("""
    SELECT id, name AS studentName, furigana, nickname, email, region, age, gender, remark, isdeleted
    FROM students
    WHERE id = #{id}
""")
    Student searchStudent(Long id);

    /*受講生のコース情報の全件検索を行う。
    *@return 受講生のコース情報（全件）*/
    @Select("SELECT * FROM students_courses")
    List<StudentsCourse> searchAllCoursesList();

    /*受講生IDに紐づく受講生コース情報を検索する。
    *@param studentId
    *@return 受講生IDに紐づく受講生コース情報*/
    @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
    List<StudentsCourse> searchAllCourse(Long studentId);

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
    List<StudentsCourse> findAll();

    /*受講生を新規登録する。
    *IDに関しては自動採番を行う。
    *@param student 受講生*/
    @Insert("INSERT INTO students (name, furigana, nickname, email, region, age, gender, remark) " +
            "VALUES (#{studentName}, #{furigana}, #{nickname}, #{email}, #{region}, #{age}, #{gender}, #{remark})")
    void registerStudent(Student student);

    /*受講生コース情報を新規登録する。
    *IDに関しては自動採番を行う。
    *@param studentsCourses 受講生コース情報*/
    @Insert("INSERT INTO students_courses (student_id, course_name, start_date, end_date) " +
            "VALUES (#{studentId}, #{courseName}, #{startDate}, #{endDate})")
    void registerStudentCourse(StudentsCourse studentsCourse);

    @Select("SELECT id, name AS studentName, furigana, nickname AS nickName, email, " +
            "region, age, gender, remark FROM students WHERE id = #{id}")
    Optional<Student> findStudentById(@Param("id") Long id);


    @Select("SELECT course_name AS courseName, start_date AS startDate, end_date AS endDate " +
            "FROM students_courses WHERE student_id = #{studentId}")
    List<StudentsCourse> findCoursesByStudentId(@Param("studentId") Long studentId);

    @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
    List<StudentsCourse> findStudentCoursesById(@Param("studentId") Long studentId);

    /*受講生情報を更新する。
    * @param student 受講生*/
    int updateStudent(Student student);

    /*受講生コース情報のコース名を更新する。
    * @param studentCourse 受講生コース情報*/
    int updateStudentCourse(StudentsCourse studentsCourse);

    @Insert("""
                INSERT INTO students_courses (student_id, course_name, start_date, end_date)
                VALUES (#{studentId}, #{courseName}, #{startDate}, #{endDate})
            """)
    int insertStudentsCourses(StudentsCourse studentsCourse);

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