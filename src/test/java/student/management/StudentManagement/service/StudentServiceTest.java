package student.management.StudentManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.jdbc.core.JdbcTemplate;
import student.management.StudentManagement.Controller.converter.StudentConverter;
import student.management.StudentManagement.data.CourseStatusDTO;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.exception.StudentNotFoundException;
import student.management.StudentManagement.exception.StudentUpdateException;
import student.management.StudentManagement.repository.StudentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository repository;

    @Mock
    private StudentConverter converter;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private SqlSession sqlSession;

    @InjectMocks
    private StudentService service;

    private Student student;
    private StudentsCourse course;
    private CourseStatusDTO status;

    @BeforeEach
    void setUp() {
        student = new Student();
        student.setId(1);
        student.setStudentName("Taro");
        student.setIsDeleted(false);

        course = new StudentsCourse();
        course.setId(1);
        course.setCourseName("Java");
        course.setStatus("本申込");
        course.setStartDate(LocalDate.now());
        course.setEndDate(LocalDate.now().plusMonths(3));
        course.setStudentId(1);

        status = new CourseStatusDTO();
        status.setStatus("受講中");
    }
    /*モックの初期化。テストクラスの各テストメソッドの実行前に毎回実行される。*/

    @Test
    void 全ての受講生の情報を取得する() {
        when(repository.findAllStudents()).thenReturn(List.of(student));
        when(repository.getStudentCourses(1)).thenReturn(List.of(course));
        when(repository.getCourseStatuses(1)).thenReturn(List.of(status));

        List<StudentDetail> results = service.getAllStudents();

        assertEquals(1, results.size());
        assertEquals("Taro", results.get(0).getStudent().getStudentName());
        verify(repository).findAllStudents();
    }

    @Test
    void 受講生を受講生IDから取得する() {
        when(repository.findStudentById(1L)).thenReturn(Optional.of(student));
        when(repository.getStudentCourses(1)).thenReturn(List.of(course));
        when(repository.getCourseStatuses(1)).thenReturn(List.of(status));

        StudentDetail detail = service.searchStudentById(1);

        assertNotNull(detail);
        assertEquals("Taro", detail.getStudent().getStudentName());
    }

    @Test
    void 受講生を登録する() {
        StudentDetail detail = new StudentDetail();
        student.setId(1); // モックIDセット
        course.setId(10); // コースIDもセット
        course.setStudentId(1); // 外部キーもセット

        detail.setStudent(student);
        detail.setStudentCourseList(List.of(course));

        doNothing().when(repository).registerStudent(student);  // voidメソッドはdoNothing()でモック
        doNothing().when(repository).registerStudentCourse(course);
        when(repository.findStudentById(1L)).thenReturn(Optional.of(student));
        when(repository.findStudentCoursesById(1L)).thenReturn(List.of(course));
        when(service.getStudentCourseStatus(1)).thenReturn(List.of(status));

        StudentDetail result = service.registerStudent(detail);

        assertNotNull(result);
        assertEquals("Taro", result.getStudent().getStudentName());
        verify(repository).registerStudent(student);
        verify(repository).registerStudentCourse(course);
    }

    @Test
    void 受講生情報を更新する() {
        // StudentDetailを作成
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);  // StudentをStudentDetailにセット
        detail.setStudentCourseList(List.of(course)); // 受講生のコースリストもセット

        // モックを設定（戻り値がある場合）
        when(repository.updateStudent(student)).thenReturn(1);  // int型の戻り値を返す場合
        when(repository.updateStudentCourse(course)).thenReturn(1);  // コース更新も同様

        // サービスメソッドを呼び出す
        service.updateStudentWithCourses(detail);  // voidメソッドなので戻り値は受け取らない

        // 更新が行われたことを確認
        verify(repository).updateStudent(student); // Studentの更新が呼ばれたか確認
        verify(repository, times(1)).updateStudentCourse(course); // コースの更新が1回呼ばれたか確認
    }

    @Test
    void 受講生情報を更新あるいは登録した場合に受講生名の名称がnullチェックに引っかかる() {
        // StudentDetailを作成
        StudentDetail detail = new StudentDetail();
        detail.setStudent(null);  // Studentをnullに設定

        // サービスメソッドを呼び出すと、NullPointerExceptionを投げることを期待
        assertThrows(NullPointerException.class, () -> service.updateStudentWithCourses(detail));
    }

    @Test
    void 受講生情報を更新あるいは登録した場合にコースがnullチェックに引っかかる() {
        // StudentDetailを作成
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);  // 有効な学生をセット
        detail.setStudentCourseList(null);  // コースリストをnullに設定

        // サービスメソッドを呼び出すと、IllegalStateExceptionを投げることを期待
        assertThrows(IllegalStateException.class, () -> service.updateStudentWithCourses(detail));
    }

    @Test
    void 受講生情報の更新に失敗した場合データベースに保存できない() {
        // モック設定
        when(repository.updateStudent(student)).thenThrow(new RuntimeException("更新失敗"));

        // StudentDetailを作成
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);  // 有効な学生をセット
        detail.setStudentCourseList(List.of(course));  // コースリストをセット

        // サービスメソッドを呼び出すと、RuntimeExceptionが発生することを期待
        assertThrows(RuntimeException.class, () -> service.updateStudentWithCourses(detail));
    }

    @Test
    void 受講生情報の更新に失敗した場合0を返す() {
        // モック設定
        when(repository.updateStudent(student)).thenReturn(0);  // 更新が成功しない

        // StudentDetailを作成
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);  // 有効な学生をセット
        detail.setStudentCourseList(List.of(course));  // コースリストをセット

        // サービスメソッドを呼び出すと、StudentUpdateExceptionが発生することを期待
        assertThrows(IllegalStateException.class, () -> service.updateStudentWithCourses(detail));
    }

    @Test
    void 受講生情報を更新した際にコースが空の場合エラーを返す() {
        // モック設定
        when(repository.updateStudent(student)).thenReturn(1);  // 学生更新は成功

        // StudentDetailを作成
        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);  // 有効な学生をセット
        detail.setStudentCourseList(new ArrayList<>());  // コースリストを空に設定

        // サービスメソッドを呼び出すと、コース更新が呼ばれないことを確認
        service.updateStudentWithCourses(detail);

        verify(repository).updateStudent(student);  // 学生の更新は呼ばれる
        verify(repository, never()).updateStudentCourse(any());  // コースの更新は呼ばれないことを確認
    }

    @Test
    void 受講生情報を更新した際に不正があった場合エラーを返す() {
        // 引数がnullの場合
        assertThrows(NullPointerException.class, () -> service.updateStudentWithCourses(null));
    }

    @Test
    void 性別で受講生を検索する() {
        // モックデータ
        Student student1 = new Student(1, "Taro", "タロウ", "Taro", "taro@example.com", "Tokyo", 20, "Male");
        Student student2 = new Student(2, "Yuki", "ユキ", "Yuki", "yuki@example.com", "Osaka", 22, "Female");

        List<Student> students = List.of(student1, student2);

        StudentsCourse course1 = new StudentsCourse(10, LocalDate.now(), LocalDate.now().plusMonths(3), "受講中", 1, "JAVA");
        StudentsCourse course2 = new StudentsCourse(11, LocalDate.now(), LocalDate.now().plusMonths(3), "受講中", 2, "JavaScript");

        // モック設定
        when(repository.findStudentByGender("Male")).thenReturn(students.stream().filter(s -> "Male".equals(s.getGender())).collect(Collectors.toList()));

        // サービスメソッドを呼び出す
        List<StudentDetail> result = service.searchStudentsByGender("Male");

        // 結果の検証
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Taro", result.get(0).getStudent().getStudentName());
    }

    @Test
    void 受講生コース情報の初期化が正しく行われる() {
        // 受講生コース情報のモック作成
        StudentsCourse studentCourse = new StudentsCourse();
        Integer generatedId = 1;  // 受講生のID

        // メソッド呼び出し
        service.initStudentsCourses(studentCourse, generatedId);

        // モックが期待通りに初期化されていることを確認
        assertEquals(generatedId, studentCourse.getStudentId());
        assertEquals(LocalDate.now(), studentCourse.getStartDate());
        assertEquals(LocalDate.now().plusYears(1), studentCourse.getEndDate());
    }

    @Test
    void 受講生のコース受講状況が取得できる() {
        // モックデータ
        Integer studentId = 1;
        CourseStatusDTO courseStatus = new CourseStatusDTO(1, "受講中");
        List<CourseStatusDTO> courseStatusList = List.of(courseStatus);

        // モック設定
        when(repository.findStudentCourseStatus(studentId)).thenReturn(courseStatusList);

        // サービスメソッドを呼び出す
        List<CourseStatusDTO> result = service.getStudentCourseStatus(studentId);

        // 結果の検証
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("受講中", result.get(0).getStatus());

        // モックが期待通りに呼ばれたことを確認
        verify(repository).findStudentCourseStatus(studentId);
    }

    @Test
    void 該当する性別の受講生が存在しない場合は空のリストを返す() {
        /*"Other" は正しい性別だが、該当者はいないケースと仮定されている*/
        when(repository.findStudentByGender("Other")).thenReturn(Collections.emptyList());

        List<Student> result = service.getStudentByGender("Other");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void 無効な性別を指定した場合は例外をスローする() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.getStudentByGender("InvalidGender");
        });

        assertEquals("Invalid gender value", exception.getMessage());
    }
}


