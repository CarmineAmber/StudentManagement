package student.management.StudentManagement.Repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import student.management.StudentManagement.data.*;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.repository.StudentRepository;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentRepositoryTest {

    @Mock
    private StudentRepository repository;

    private Student student1;
    private Student student2;
    private StudentsCourse course1;
    private StudentsCourse course2;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    void setUp() {
        student1 = new Student(1, "John Doe", "ジョン・ドウ", "Johnny", "john.doe@example.com", "Tokyo", 25, "Male");
        student2 = new Student(2, "Jane Smith", "ジェーン・スミス", "Janey", "jane.smith@example.com", "Osaka", 30, "Female");

        course1 = new StudentsCourse(1, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 1), "仮申込", 1, "JAVA");
        course2 = new StudentsCourse(2, LocalDate.of(2024, 2, 1), LocalDate.of(2024, 7, 1), "本申込", 1, "Spring Boot");
    }

    @Test
    void 受講生を一覧検索する() {
        when(repository.searchAllStudents()).thenReturn(List.of(student1, student2));

        List<Student> result = repository.searchAllStudents();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getStudentName());
    }

    @Test
    void すべての受講生を取得する() {
        when(repository.findAll()).thenReturn(List.of(student1, student2));

        List<Student> result = repository.findAll();

        assertEquals("Jane Smith", result.get(1).getStudentName());
    }

    @Test
    void 受講生の単一検索を行う() {
        when(repository.searchStudent(1L)).thenReturn(student1);

        Student result = repository.searchStudent(1L);

        assertEquals("John Doe", result.getStudentName());
    }

    @Test
    void  受講生のコース情報の全件検索を行う() {
        when(repository.searchAllCoursesList()).thenReturn(List.of(course1, course2));

        List<StudentsCourse> result = repository.searchAllCoursesList();

        assertEquals("JAVA", result.get(0).getCourseName());
    }

    @Test
    void 受講生IDに紐づく受講生コース情報を検索する() {
        when(repository.searchAllCourse(1L)).thenReturn(List.of(course1, course2));

        List<StudentsCourse> result = repository.searchAllCourse(1L);

        assertEquals(2, result.size());
    }

    @Test
    void 受講生とコース情報の結合結果を取得する() throws ParseException {
        StudentsWithCourses swc1 = new StudentsWithCourses();
        swc1.setStudentId(1);
        swc1.setStudentName("John Doe");
        swc1.setCourseName("JAVA");
        swc1.setStartDate(dateFormat.parse("2024-01-01"));
        swc1.setEndDate(dateFormat.parse("2024-06-01"));

        when(repository.searchStudentsWithCourses()).thenReturn(List.of(swc1));

        List<StudentsWithCourses> result = repository.searchStudentsWithCourses();

        assertEquals("JAVA", result.get(0).getCourseName());
    }

    @Test
    void コース受講状況の最新情報を検索する() {
        CourseStatusDTO status = new CourseStatusDTO();
        status.setCourseId(1);
        status.setCourseName("JAVA");
        status.setStatus("仮申込");

        when(repository.findStudentCourseStatus(1)).thenReturn(List.of(status));

        List<CourseStatusDTO> result = repository.findStudentCourseStatus(1);

        assertEquals("JAVA", result.get(0).getCourseName());
    }

    @Test
    void コース受講状況を更新する() {
        doNothing().when(repository).updateStudentCourseStatus(1, "受講中");

        assertDoesNotThrow(() -> repository.updateStudentCourseStatus(1, "受講中"));
    }

    @Test
    void 全ての受講生情報を取得する() {
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student1);
        when(repository.getStudentList()).thenReturn(List.of(detail));

        List<StudentDetail> result = repository.getStudentList();

        assertEquals(1, result.size());
    }

    @Test
    void 受講生の詳細を取得する() {
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student2);
        when(repository.getStudentDetails(2)).thenReturn(detail);

        StudentDetail result = repository.getStudentDetails(2);

        assertEquals("Jane Smith", result.getStudent().getStudentName());
    }

    @Test
    void 受講状況を受講生IDから取得する () {
        CourseStatusDTO dto = new CourseStatusDTO();
        dto.setStatus("受講中");

        when(repository.getCourseStatusesByStudentId(1)).thenReturn(List.of(dto));

        List<CourseStatusDTO> result = repository.getCourseStatusesByStudentId(1);

        assertEquals("受講中", result.get(0).getStatus());
    }

    @Test
    void  最新の受講状況を取得する() {
        CourseStatusDTO dto = new CourseStatusDTO();
        dto.setStatus("受講終了");

        when(repository.getLatestCourseStatus(1)).thenReturn(List.of(dto));

        List<CourseStatusDTO> result = repository.getLatestCourseStatus(1);

        assertEquals("受講終了", result.get(0).getStatus());
    }

    @Test
    void コース名から受講生名を取得する() {
        when(repository.findStudentsByCourseName("Java入門")).thenReturn(List.of(student1));

        List<Student> result = repository.findStudentsByCourseName("Java入門");

        assertEquals("John Doe", result.get(0).getStudentName());
    }

    @Test
    void コース名から受講生名を検索する() {
        StudentsWithCourses swc = new StudentsWithCourses();
        swc.setStudentName("John Doe");
        swc.setCourseName("Java入門");

        when(repository.searchStudentsByCourseName("Java入門")).thenReturn(List.of(swc));

        List<StudentsWithCourses> result = repository.searchStudentsByCourseName("Java入門");

        assertEquals("Java入門", result.get(0).getCourseName());
    }

    @Test
    void 受講状況を更新する () {
        when(repository.registerCourseStatus(1, "本申込")).thenReturn(1);
        assertDoesNotThrow(() -> repository.registerCourseStatus(1, "本申込"));
    }
    /*registerCourseStatusはvoidでないため、doNothingが使えない。
    * thenReturn(1)は、成功を返すという意味*/

    @Test
    void 受講生IDから受講生名とコース情報を取得する() {
        StudentsCourseWithStatus scs = new StudentsCourseWithStatus();
        scs.setCourseName("Java");

        when(repository.findStudentCoursesWithStatusByStudentId(1)).thenReturn(List.of(scs));

        List<StudentsCourseWithStatus> result = repository.findStudentCoursesWithStatusByStudentId(1);

        assertEquals("Java", result.get(0).getCourseName());
    }

    @Test
    void 受講生IDから受講状況を取得する() {
        CourseStatusDTO status = new CourseStatusDTO();
        status.setStatus("仮申込");

        when(repository.findCourseStatusesByStudentId(1)).thenReturn(List.of(status));

        List<CourseStatusDTO> result = repository.findCourseStatusesByStudentId(1);

        assertEquals("仮申込", result.get(0).getStatus());
    }

    @Test
    void コース情報を登録する() {
        StudentsCourse course = new StudentsCourse();
        course.setCourseName("Spring Boot基礎");

        when(repository.insertStudentsCourses(course)).thenReturn(1);

        int result = repository.insertStudentsCourses(course);

        assertEquals(1, result);
    }

    @Test
    void 受講生詳細情報をIDから取得する() {
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student1);

        when(repository.findStudentDetailById(1L)).thenReturn(detail);

        StudentDetail result = repository.findStudentDetailById(1L);

        assertEquals("John Doe", result.getStudent().getStudentName());
    }

    @Test
    void 受講生の論理削除を行う() {
        doNothing().when(repository).updateIsDeleted(1L, true);

        Student deletedStudent = new Student(1, "Deleted", "", "", "", "", 0, "");
        deletedStudent.setIsDeleted(true);

        when(repository.findStudentById(1L)).thenReturn(Optional.of(deletedStudent));

        repository.updateIsDeleted(1L, true);
        Optional<Student> result = repository.findStudentById(1L);

        assertTrue(result.isPresent());
        assertTrue(result.get().getIsDeleted());
    }
}
