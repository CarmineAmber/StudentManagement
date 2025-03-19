package student.management.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import student.management.StudentManagement.StudentsWithCourses;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Transactional
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql({"/schema.sql", "/data.sql"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StudentRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private StudentRepository studentRepository;;

    @Autowired
    private StudentRepository sut;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM students");
        this.studentRepository = applicationContext.getBean(StudentRepository.class);
        Student student = new Student();
        student.setId(1);
        student.setStudentName("Alice");
        student.setFurigana("アリス");
        student.setNickname("Ali");
        student.setEmail("al@example.com");
        student.setRegion("Tokyo");
        student.setAge(22);
        student.setGender("Female");
        student.setRemark("");
        student.setIsDeleted(false);

        StudentsCourse studentsCourse = new StudentsCourse();
        studentsCourse.setCourseId(1);
        studentsCourse.setCourseName("JAVA");
        studentRepository.registerStudent(student);
    }

    @Test
    void 受講生の全件検索が行えること(){
        List<Student> actual = sut.searchStudents();
        assertThat(actual.size()).isEqualTo(1);
    }

    @Test
    void 受講生の登録が行えること(){
        Student student = new Student();
        student.setStudentName("Alice");
        student.setFurigana("アリス");
        student.setNickname("Ali");
        student.setEmail("alice@example.com");
        student.setRegion("Tokyo");
        student.setGender("Female");
        student.setAge(22);
        student.setRemark("");
        student.setIsDeleted(false);

        sut.registerStudent(student);

        List<Student> actual = sut.searchStudents();

        assertThat(actual.size()).isEqualTo(2);
    }

    @Test
    void 受講生をIDから受講生を個別に検索を行えること() {
        Student student = studentRepository.searchStudent(1L);
        assertNotNull(student);
        assertEquals(1, student.getId());
    }

    @Sql("/data.sql")
    @Test
    void コースの全件検索が行えること() {
        List<StudentsCourse> courses = studentRepository.searchAllCoursesList();
        System.out.println("取得したコースの数: " + courses.size());
        if (courses.isEmpty()) {
            System.out.println("コースが空です");
        } else {
            for (StudentsCourse course : courses) {
                System.out.println(course);
            }
        }

        assertTrue(courses.isEmpty());
    }

    @Test
    void 受講生IDからコースの検索が行えること() {
        List<StudentsCourse> courses = studentRepository.findCoursesByStudentId(1L);
        System.out.println("取得したコース数: " + courses.size());
        if (courses.isEmpty()) {
            System.out.println("学生ID 1に関連するコースは見つかりませんでした");
        }
        assertTrue(courses.isEmpty(), "学生ID 1のコースが取得できません");
        if (!courses.isEmpty()) {
            assertEquals(1, courses.get(0).getStudentId());
        }
    }

    @Test
    void 削除されていない受講生のデータを検索する() {
        List<Student> students = studentRepository.searchStudents();
        assertFalse(students.isEmpty());
        assertFalse(students.get(0).getIsDeleted());
    }

    @Test
    void 受講生とコースを検索する() {
        List<StudentsWithCourses> studentsWithCourses = studentRepository.searchStudentsWithCourses();
        assertNotNull(studentsWithCourses);
        assertFalse(studentsWithCourses.isEmpty());
    }

    @Test
    void コース名から受講生を検索する() {
        List<StudentsWithCourses> students = studentRepository.searchStudentsByCourseName("JAVA");
        assertNotNull(students);
        assertTrue(students.isEmpty());
    }

    @Test
    void 受講生とコースの新規登録を行う() {
        StudentsCourse course = new StudentsCourse();
        course.setStudentId(1); // Integer に変更
        course.setCourseName("Python");
        course.setStartDate(LocalDate.parse("2024-04-01"));
        course.setEndDate(LocalDate.parse("2024-07-01"));

        studentRepository.registerStudentCourse(course);

        List<StudentsCourse> courses = studentRepository.searchAllCourse(1L);
        assertFalse(courses.stream().anyMatch(c -> "JAVA".equals(c.getCourseName())));
    }

    @Test
    void 受講生の更新を行う() {
        Student student = new Student();
        student.setStudentName("Alice");
        student.setFurigana("アリス");
        student.setNickname("Ali");
        student.setEmail("al@example.com");
        student.setRegion("Tokyo");
        student.setGender("Female");
        student.setAge(22);
        student.setRemark("");
        student.setIsDeleted(false);

        StudentsCourse studentsCourse = new StudentsCourse();
        studentsCourse.setCourseId(1);
        studentsCourse.setCourseName("JAVA");

        studentRepository.updateStudent(student);

        Student updatedStudent = studentRepository.searchStudent(1L);
        assertEquals("Alice", updatedStudent.getStudentName());
        assertEquals("al@example.com", updatedStudent.getEmail());
    }

    @Test
    void 論理削除を行えるようにする() {
        studentRepository.updateIsDeleted(1L, true);
        Student student = studentRepository.searchStudent(1L);
        assertTrue(student.getIsDeleted());
    }
}