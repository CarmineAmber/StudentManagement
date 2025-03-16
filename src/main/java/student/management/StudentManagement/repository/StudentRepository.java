package student.management.StudentManagement.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import student.management.StudentManagement.StudentsWithCourses;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;

import java.util.List;
import java.util.Optional;

@Mapper
public interface StudentRepository {

    List<Student> searchAllStudents();

    Student searchStudent(Long id);

    List<StudentsCourse> searchAllCoursesList();

    List<StudentsCourse> searchAllCourse(Long studentId);

    List<Student> searchStudents();

    List<StudentsWithCourses> searchStudentsWithCourses();

    List<Student> searchStudentsInAgeRange(@Param("ageStart") int ageStart, @Param("ageEnd") int ageEnd);

    List<StudentsWithCourses> searchStudentsByCourseName(@Param("courseName") String courseName);

    void insertStudentName(@Param("name") String name);

    List<StudentsCourse> findAll();

    void registerStudent(Student student);

    void registerStudentCourse(StudentsCourse studentsCourse);

    Optional<Student> findStudentById(@Param("id") Long id);

    List<StudentsCourse> findCoursesByStudentId(@Param("studentId") Long studentId);

    List<StudentsCourse> findStudentCoursesById(@Param("studentId") Long studentId);

    int updateStudent(Student student);

    int updateStudentCourse(StudentsCourse studentsCourse);

    int insertStudentsCourses(StudentsCourse studentsCourse);

    StudentDetail findStudentDetailById(@Param("id") Long id);

    void updateIsDeleted(@Param("id") Long id, @Param("isDeleted") boolean isDeleted);
}
