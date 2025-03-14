package student.management.StudentManagement.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import student.management.StudentManagement.Controller.converter.StudentConverter;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.repository.StudentRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
        /*MockitoExtension:テストコード内にインスタンスを自動で生成する。
         * 返り値も自動で設定できる*/
class StudentServiceTest {
    /*testのvoidは日本語で記述すること*/

    @Mock
    private StudentRepository repository;

    @Mock
    private StudentConverter converter;

    private StudentService sut;

    @BeforeEach
    void before() {
        sut = new StudentService(repository, converter);
    }
    /*BeforeEachは、コードの前に書くことでTestクラスにおける共通事項を定める。
     * つまり、このStudentServiceTestクラスにおいてsut =
     * new StudentService(repository,converter)であると定めている*/

    @Test
    void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {

        /*事前準備*/
        List<Student> studentList = new ArrayList<>();
        List<StudentsCourse> studentsCourseList = new ArrayList<>();
        /*空リストを擬似的に生成*/
        when(repository.searchAllStudents()).thenReturn(studentList);
        when(repository.searchAllCoursesList()).thenReturn(studentsCourseList);

        sut.searchStudentList();

        /*検証*/
        verify(repository, times(1)).searchAllStudents();
        verify(repository, times(1)).searchAllCoursesList();
        verify(converter, times(2)).convertStudentDetails(studentList, studentsCourseList);
        /*searchを１回、searchAllCoursesListを１回、convertStudentDetailsを２回行うということ。
         * convertStudentDetailsはstudentListとstudentsCourseListを所持しているため、
         * ２回行う必要がある。*/
    }

    @Test
    void 受講生登録_受講生登録の登録処理が適切に実行されること() {
        Student student = new Student();
        student.setId(1); // データベースで生成されたIDを模擬（手動で設定している）

        List<StudentsCourse> studentsCoursesList = new ArrayList<>();
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);
        studentDetail.setStudentCourseList(studentsCoursesList);

        doAnswer(invocation -> {
            Student argStudent = invocation.getArgument(0);
            argStudent.setId(1); // モックでIDを設定
            return null;
        }).when(repository).registerStudent(any(Student.class));
        /*このdoAnswerを使うことによって、メソッドの副作用を擬似生成している*/

        StudentDetail result = sut.registerStudent(studentDetail);

        Assertions.assertNotNull(result);
        assertEquals(1, result.getStudent().getId());
        /*戻り値がnullでないこと、そしてIDが１になっていることを確認する。*/

        verify(repository, times(1)).registerStudent(any(Student.class));
        verify(repository, times(studentsCoursesList.size())).registerStudentCourse(any(StudentsCourse.class));
        /*registerStudentが１回呼ばれ、registerStudentCourseがstudentsCoursesListがサイズ回数
         * （どれだけの数が現在リストの中にあるか）呼ばれたことを確認する。*/
    }

    @Test
    void 受講生更新_受講生情報の更新処理が適切に実行されること() {
        Student student = new Student();
        student.setId(1);
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);

        when(repository.updateStudent(any(Student.class))).thenReturn(1);

        sut.updateStudent(studentDetail);

        verify(repository, times(1)).updateStudent(any(Student.class));
    }

    @Test
    void 受講生更新_学生情報が存在しない場合に例外をスローすること() {
        Student student = new Student();
        student.setId(1);
        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);

        when(repository.updateStudent(any(Student.class))).thenReturn(0);

        Assertions.assertThrows(IllegalStateException.class, () -> sut.updateStudent(studentDetail));
        /*Assertions.assertThrows(IllegalStateException.class, () -> sut.updateStudent(studentDetail))とは、
         * 例外のスローを擬似的に行うということを検証している*/
    }

    @Test
    void 受講生とコースの更新_受講生とコース情報の更新処理が適切に実行されること() {
        Student student = new Student();
        student.setId(1);
        List<StudentsCourse> studentsCoursesList = new ArrayList<>();
        StudentsCourse course = new StudentsCourse();
        studentsCoursesList.add(course);

        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);
        studentDetail.setStudentCourseList(studentsCoursesList);

        when(repository.updateStudent(any(Student.class))).thenReturn(1);

        sut.updateStudentWithCourses(studentDetail);

        verify(repository, times(1)).updateStudent(any(Student.class));
        verify(repository, times(1)).updateStudentCourse(any(StudentsCourse.class));
    }

    @Test
    void 受講生詳細_特定の学生の詳細が正しく取得できること() {
        Student student = new Student();
        student.setId(1);
        List<StudentsCourse> studentsCoursesList = new ArrayList<>();

        when(repository.findStudentById(1L)).thenAnswer(invocation -> Optional.of(student));
        when(repository.findCoursesByStudentId(1L)).thenReturn(studentsCoursesList);
        when(converter.convertStudentDetails(anyList(), anyList()))
                .thenReturn(List.of(new StudentDetail(student, studentsCoursesList)));

        StudentDetail result = sut.getStudentDetail(1L);

        Assertions.assertNotNull(result);
        assertEquals(1, result.getStudent().getId());
        verify(repository, times(1)).findStudentById(1L);
        verify(repository, times(1)).findCoursesByStudentId(1L);
        verify(converter, times(1)).convertStudentDetails(List.of(student), studentsCoursesList);
    }

    @Test
    void 受講コース情報の初期化_適切にデータが設定されること() {
        // 準備
        StudentsCourse studentCourse = new StudentsCourse();
        Integer generatedId = 100;
        LocalDate now = LocalDate.now();

        // 実行
        sut.initStudentsCourses(studentCourse, generatedId);

        // 検証
        assertEquals(generatedId, studentCourse.getStudentId(), "学生IDが正しく設定されていること");
        assertEquals(now, studentCourse.getStartDate(), "開始日が現在の日付であること");
        assertEquals(now.plusYears(1), studentCourse.getEndDate(), "終了日が1年後の日付であること");
    }
}