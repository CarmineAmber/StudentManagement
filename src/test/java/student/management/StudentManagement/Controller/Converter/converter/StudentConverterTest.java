package student.management.StudentManagement.Controller.Converter.converter;

import org.junit.jupiter.api.Test;
import student.management.StudentManagement.Controller.converter.StudentConverter;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class StudentConverterTest {
    private final StudentConverter converter = new StudentConverter();

    @Test
    void 受講生に紐づく受講生コース情報をマッピングする() {
        Student student = new Student(1, "山田太郎", "ヤマダタロウ", "Taro", "taro@example.com", "Tokyo", 20, "Male");

        StudentsCourse course1 = new StudentsCourse(
                1,
                LocalDate.parse("2024-01-01"),
                LocalDate.parse("2024-06-01"),
                "仮申込",
                1,
                "Java基礎"
        );

        StudentsCourse course2 = new StudentsCourse(
                2,
                LocalDate.parse("2024-02-01"),
                LocalDate.parse("2024-07-01"),
                "本申込",
                1,
                "Spring Boot"
        );

        StudentsCourse courseOther = new StudentsCourse(
                3,
                LocalDate.parse("2024-03-01"),
                LocalDate.parse("2024-08-01"),
                "仮申込",
                2,
                "別の人のコース"
        );

        List<StudentDetail> result = converter.convertStudentDetails(
                Collections.singletonList(student),
                Arrays.asList(course1, course2, courseOther)
        );

        // Assert
        assertEquals(1, result.size());
        StudentDetail detail = result.get(0);
        assertEquals("山田太郎", detail.getStudent().getStudentName());
        assertEquals(2, detail.getStudentCourseList().size());
        assertTrue(detail.getStudentCourseList().stream()
                .anyMatch(course -> course.getCourseName().equals("Java基礎")));
        assertTrue(detail.getStudentCourseList().stream()
                .anyMatch(course -> course.getCourseName().equals("Spring Boot")));
    }

    @Test
    void 受講生コース情報に紐づく受講生情報をマッピングする() {
        // Arrange
        Student student = new Student(10, "鈴木花子", "スズキハナコ", "Hanako", "hana@example.com", "Osaka", 22, "Female");
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);

        // Act
        Student result = converter.convertStudentDetailToStudent(detail);

        // Assert
        assertNotNull(result);
        assertEquals("鈴木花子", result.getStudentName());
        assertEquals("Osaka", result.getRegion());
    }

    @Test
    void 受講生コース情報に紐づく受講生詳細情報をマッピングする() {
        // Arrange
        StudentsCourse course1 = new StudentsCourse(
                1,
                LocalDate.parse("2024-01-01"),
                LocalDate.parse("2024-06-01"),
                "仮申込",
                1,
                "Java基礎"
        );
        StudentsCourse course2 = new StudentsCourse(
                2,
                LocalDate.parse("2024-02-01"),
                LocalDate.parse("2024-07-01"),
                "本申込",
                1,
                "Spring Boot"
        );

        List<StudentsCourse> courseList = Arrays.asList(course1, course2);

        StudentDetail detail = new StudentDetail();
        detail.setStudentCourseList(courseList);

        // Act
        List<StudentsCourse> result = converter.convertStudentDetailToCourses(detail);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Java基礎", result.get(0).getCourseName()); // 大文字間違い修正: "JAVA基礎" → "Java基礎"
        assertEquals("Spring Boot", result.get(1).getCourseName());
    }

}
