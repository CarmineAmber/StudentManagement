package student.management.StudentManagement;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StudentRepository {

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
}