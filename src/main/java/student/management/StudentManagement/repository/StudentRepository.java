package student.management.StudentManagement.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourses;
import student.management.StudentManagement.StudentsWithCourses;

import java.util.List;

/*受講生情報を扱うリポジトリ。
 * 全件検索や単一条件での検索が行えるクラス。*/

@Mapper
public interface StudentRepository {

    /*全件検索を行う。
     * @return 全件検索した受講生情報の一覧*/

    /*SQLの読み込み*/
    @Select("SELECT * FROM students")
    List<Student> searchStudents();

    @Select("SELECT * FROM students_courses")
    List<StudentsCourses> searchAllCourses();

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

    @Select("SELECT * FROM students WHERE age BETWEEN 30 AND 39")
    List<Student> searchStudentsIn30s();

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
            sc.course_name = 'JAVA'
        """)
    List<StudentsWithCourses> searchJavaCourse();
}