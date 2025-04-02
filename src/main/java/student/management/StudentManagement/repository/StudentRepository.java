package student.management.StudentManagement.repository;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.LocalDateTypeHandler;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import student.management.StudentManagement.StudentsWithCourses;
import student.management.StudentManagement.data.CourseStatusDTO;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.data.StudentsCourseWithStatus;
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
    @Select("SELECT * FROM students")
    List<Student> searchAllStudents();

    /* すべての受講生を取得する */
    @Select("SELECT id, name AS studentName, furigana, nickname, email, region, age, gender, remark, isdeleted " +
            "FROM students WHERE isdeleted = false")
    List<Student> findAll();


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
    @Select("""
                SELECT
                    sc.id AS courseId,
                    sc.course_name AS courseName,
                    sc.start_date AS startDate,
                    sc.end_date AS endDate,
                    sc.student_id AS studentId
                FROM
                    students_courses sc
                LEFT JOIN
                    students s ON sc.student_id = s.id
            """)
    List<StudentsCourse> searchAllCoursesList();

    /*受講生IDに紐づく受講生コース情報を検索する。
     *@param studentId
     *@return 受講生IDに紐づく受講生コース情報*/
    @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
    List<StudentsCourse> searchAllCourse(Long studentId);

    @Select("""
                <script>
                SELECT s.id AS studentId, s.name, s.gender, sc.id AS studentsCoursesId, sc.course_name AS courseName,
                       latest_status.status, sc.id AS courseId
                FROM students s
                LEFT JOIN students_courses sc ON s.id = sc.student_id
                LEFT JOIN (
                    SELECT scs1.students_courses_id, scs1.status
                    FROM students_courses_status scs1
                    JOIN (
                        SELECT students_courses_id, MAX(id) AS latest_id
                        FROM students_courses_status
                        GROUP BY students_courses_id
                    ) latest ON scs1.id = latest.latest_id
                ) latest_status ON sc.id = latest_status.students_courses_id
                <where>
                    <if test="studentId != null">
                        AND s.id = #{studentId}
                    </if>
                    <if test="gender != null and gender != ''">
                        AND s.gender = #{gender}
                    </if>
                    <if test="courseName != null and courseName != ''">
                        AND sc.course_name LIKE CONCAT('%', #{courseName}, '%')
                    </if>
                </where>
                ORDER BY s.id
                </script>
            """)
    List<Student> searchStudents(@Param("studentId") Integer studentId,
                                 @Param("gender") String gender,
                                 @Param("courseName") String courseName);

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

    /*コース受講状況を取得。常に最新のものを取得する*/
    @Select("""
            SELECT sc.id AS studentsCoursesId, sc.course_name AS courseName, latest_status.status, sc.id AS courseId
            FROM students_courses sc
            LEFT JOIN (
                SELECT scs1.students_courses_id, scs1.status
                FROM students_courses_status scs1
                JOIN (
                    SELECT students_courses_id, MAX(id) AS latest_id
                    FROM students_courses_status
                    GROUP BY students_courses_id
                ) latest ON scs1.id = latest.latest_id
            ) latest_status ON sc.id = latest_status.students_courses_id
            WHERE sc.student_id = #{studentId}
            """)
    List<CourseStatusDTO> findStudentCourseStatus(@Param("studentId") Integer studentId);

    /*コース受講状況を更新*/
    @Update("""
                INSERT INTO students_courses_status (students_courses_id, status)
                VALUES (#{studentsCoursesId}, #{status})
                ON DUPLICATE KEY UPDATE status = #{status}
            """)
    void updateStudentCourseStatus(@Param("studentsCoursesId") Integer studentsCoursesId,
                                   @Param("status") String status);

    /*受講生リストの取得。sはstudentテーブル、scはstudents_coursesテーブル、scsは
     * students_courses_statusテーブルのエイリアス。DISTINCT を使用して重複する行を防ぎつつ、
     * students_courses_status テーブルからのステータス情報も取得している*/
    @Select("SELECT DISTINCT " +
            "s.id AS student_id, s.furigana, s.nickname, s.email, s.region, s.age, s.gender, s.remark, s.isDeleted, " +
            "sc.id AS course_id, sc.start_date AS startDate, sc.end_date AS endDate, sc.course_name, " +
            "scs.students_courses_id, scs.status " +
            "FROM students s " +
            "LEFT JOIN students_courses sc ON s.id = sc.student_id " +
            "LEFT JOIN students_courses_status scs ON sc.id = scs.students_courses_id")
    @Results({
            @Result(property = "student.id", column = "student_id"),
            @Result(property = "student.furigana", column = "furigana"),
            @Result(property = "student.nickname", column = "nickname"),
            @Result(property = "student.email", column = "email"),
            @Result(property = "student.region", column = "region"),
            @Result(property = "student.age", column = "age"),
            @Result(property = "student.gender", column = "gender"),
            @Result(property = "student.remark", column = "remark"),
            @Result(property = "student.isDeleted", column = "isDeleted"),

            // studentCourseListを取得
            @Result(property = "studentCourseList", javaType = List.class, column = "student_id",
                    many = @Many(select = "getStudentCourses")),

            // courseStatusesを取得
            @Result(property = "courseStatuses", javaType = List.class, column = "course_id",
                    many = @Many(select = "getCourseStatuses"))
    })
    List<StudentDetail> getStudentList();

    /*全ての受講生情報を取得する*/
    @Select("""
                SELECT id, name AS studentName, furigana, nickname, email, region, age, gender, remark, isdeleted
                FROM students
            """)
    List<Student> findAllStudents();

    /*受講生の詳細を取得する*/
    @Select("""
                SELECT 
                    s.id AS student_id, s.name AS studentName, s.furigana, s.nickname, s.email, s.region, s.age, 
                    s.gender, s.remark, s.isDeleted, 
                    sc.id AS course_id, sc.start_date AS start_date, sc.end_date AS end_date, sc.course_name AS course_name, 
                    COALESCE(scs.students_courses_id, 0) AS student_course_id, 
                    COALESCE(scs.status, '未登録') AS course_status 
                FROM students s
                LEFT JOIN students_courses sc ON s.id = sc.student_id
                LEFT JOIN students_courses_status scs ON sc.id = scs.students_courses_id
                WHERE s.id = #{studentId}
            """)
    @Results({
            @Result(property = "student.id", column = "student_id"),
            @Result(property = "student.name", column = "studentName"),
            @Result(property = "student.furigana", column = "furigana"),
            @Result(property = "student.nickname", column = "nickname"),
            @Result(property = "student.email", column = "email"),
            @Result(property = "student.region", column = "region"),
            @Result(property = "student.age", column = "age"),
            @Result(property = "student.gender", column = "gender"),
            @Result(property = "student.remark", column = "remark"),
            @Result(property = "student.isDeleted", column = "isDeleted"),

            // 学生のコース情報を取得
            @Result(property = "studentCourseList", column = "student_id", javaType = List.class,
                    many = @Many(select = "getStudentCourses")),

            // コースのステータス情報を取得
            @Result(property = "courseStatuses", column = "course_id", javaType = List.class,
                    many = @Many(select = "getCourseStatuses"))
    })
    StudentDetail getStudentDetails(@Param("studentId") Integer studentId);

    /*受講生のコース情報を取得する。情報が重複する場合、１つに統一する。*/
    @Select("""
                SELECT sc.id, sc.start_date, sc.end_date, sc.student_id, sc.course_name AS courseName
                FROM students_courses sc
                WHERE sc.student_id = #{studentId}
                ORDER BY sc.id DESC LIMIT 1
            """)
    List<StudentsCourse> getStudentCourses(Integer studentId);


    /*受講生の受講状況を取得する*/
    @Select("""
            SELECT scs.students_courses_id AS studentsCoursesId, sc.course_name AS courseName, scs.status, s.name AS studentName
            FROM students_courses_status scs
            JOIN students_courses sc ON sc.id = scs.students_courses_id
            JOIN students s ON s.id = sc.student_id
            WHERE s.id = #{studentId}
            AND scs.students_courses_id = sc.id
            ORDER BY scs.id DESC  -- 最新のデータを優先
            LIMIT 1               -- 最新の 1 件のみ取得
            """)
    List<CourseStatusDTO> getCourseStatuses(@Param("studentId") Integer studentId);
    // 引数を studentId に変更

    /*受講生の最新の受講状況を取得する*/
    @Select("""
            SELECT scs.students_courses_id AS studentsCoursesId, sc.course_name AS courseName, scs.status
            FROM students_courses_status scs
            JOIN students_courses sc ON sc.id = scs.students_courses_id
            WHERE sc.student_id = #{studentId}
            ORDER BY scs.id DESC
            LIMIT 1
            """)
    List<CourseStatusDTO> getLatestCourseStatus(@Param("studentId") Integer studentId);

    /*受講生の情報を性別で検索できるようにするためのリポジトリ*/
    @Select("""
                SELECT id, name AS studentName, furigana, nickname, email, region, age, gender, remark, isdeleted
                FROM students
                WHERE LOWER(gender) = LOWER(#{gender}) AND isdeleted = 0
            """)
    List<Student> findStudentByGender(@Param("gender") String gender);

    @Select("SELECT scs.students_courses_id, scs.status " +
            "FROM students_courses_status scs " +
            "WHERE scs.students_courses_id = #{studentId}")
    List<CourseStatusDTO> getCourseStatusesByStudentId(Integer studentId);

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

    /*受講生を新規登録する。
     *IDに関しては自動採番を行う。
     *@param student 受講生*/
    @Insert("INSERT INTO students (name, furigana, nickname, email, region, age, gender, remark) " +
            "VALUES (#{studentName}, #{furigana}, #{nickname}, #{email}, #{region}, #{age}, #{gender}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void registerStudent(Student student);

    /*受講生コース情報を新規登録する。
     *IDに関しては自動採番を行う。
     *@param studentsCourses 受講生コース情報*/
    @Insert("INSERT INTO students_courses (student_id, course_name, start_date, end_date) " +
            "VALUES (#{studentId}, #{courseName}, #{startDate}, #{endDate})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    // 自動採番されたIDを取得
    void registerStudentCourse(StudentsCourse studentsCourse);

    /*受講生コース受講状況を新規登録する。*/
    @Insert("INSERT INTO students_courses_status (students_courses_id, status) VALUES (#{studentsCoursesId}, #{status})")
    void registerCourseStatus(@Param("studentsCoursesId") Integer studentsCoursesId, @Param("status") String status);

    @Select("""
            SELECT id, name AS studentName, furigana, nickname, email, region, age, gender, remark, isdeleted
            FROM students
            WHERE id = #{id} AND isdeleted = 0
            """)
    Optional<Student> findStudentById(@Param("id") Long id);


    /*受講生情報、コース情報、受講情報全てを取得する。*/
    @Query("SELECT s FROM Student s JOIN FETCH s.studentCourses sc JOIN FETCH sc.course")
    List<Student> findAllStudentsWithCourseStatus();

    /*コース情報を受講生idから取得する*/
    @Select("SELECT id AS courseId,course_name AS courseName, start_date AS startDate, end_date AS endDate " +
            "FROM students_courses WHERE student_id = #{studentId}")
    List<StudentsCourse> findCoursesByStudentId(@Param("studentId") Long studentId);

    /*受講生情報をコースidから取得する*/
    @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
    List<StudentsCourse> findStudentCoursesById(@Param("studentId") Long studentId);

    @Select("SELECT cs.status FROM students_courses sc JOIN students_courses_status cs ON sc.id = cs.students_courses_id WHERE sc.student_id = #{studentId}")
    List<CourseStatusDTO> findByStudentId(@Param("studentId") Integer studentId);

    /*コース情報と受講状況を取得する*/
    @Select("SELECT sc.id, sc.student_id, sc.course_name, sc.start_date, sc.end_date, cs.status " +
            "FROM students_courses sc " +
            "LEFT JOIN course_statuses cs ON sc.id = cs.students_courses_id " +
            "WHERE sc.student_id = #{studentId}")
    List<StudentsCourseWithStatus> findStudentCoursesWithStatusByStudentId(@Param("studentId") Integer studentId);


    /*コース受講状況を受講生idから取得する*/
    @Select("""
                SELECT scs.students_courses_id, scs.status
                FROM students_courses_status scs
                JOIN students_courses sc ON sc.id = scs.students_courses_id
                WHERE sc.student_id = #{studentId}
            """)
    List<CourseStatusDTO> findCourseStatusesByStudentId(@Param("studentId") Integer studentId);


    /*受講生情報を更新する。
     * @param student 受講生*/
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
    int updateStudent(Student student);

    /*受講生コース情報のコース名を更新する。
     * @param studentCourse 受講生コース情報*/
    @Update("""
                UPDATE students_courses
                SET
                course_name = #{courseName},
                start_date = #{startDate},
                end_date = #{endDate}
                WHERE
                id = #{courseId}
            """)
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
                    id = #{id} AND isdeleted = 0
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

