package student.management.StudentManagement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import student.management.StudentManagement.Controller.converter.StudentConverter;
import student.management.StudentManagement.data.CourseStatusDTO;
import student.management.StudentManagement.data.Student;
import student.management.StudentManagement.data.StudentsCourse;
import student.management.StudentManagement.domain.StudentDetail;
import student.management.StudentManagement.repository.StudentRepository;
import org.apache.ibatis.session.SqlSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/*受講生情報を取り扱うサービス。
 * 受講生の検索や登録・更新処理を行う*/

@Service
@Slf4j
/* ログを確認してデバッグを行うこと*/
public class StudentService {

    private final StudentRepository repository;
    private StudentConverter converter;
    /*finalを宣言すると、そのクラスを継承したクラスにおいてそのメソッドを
     * オーバーライドできなくなる。コンストラクタの宣言にfinalを使用することはない*/

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private SqlSession sqlsession;

    public StudentService(StudentRepository repository, StudentConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }
    /*本来はnewが入らないとインスタンスとして機能しないが、SpringBootの@Serviceで
    インスタンスとして呼び出すことが可能。更にAutowiredでStudentManagementApplicationの
    repositoryを呼び出せる。これを行うことで上書きが容易になる。*/

    /*受講生一覧詳細検索機能。
     * 全件検索を行うため、条件指定は行わない。
     * @return 受講生一覧（全件検索）*/
    public List<StudentDetail> getAllStudents() {
        // 全学生情報を取得
        List<Student> students = repository.findAllStudents();

        // 学生ごとに詳細情報をセット
        List<StudentDetail> studentDetails = new ArrayList<>();

        for (Student student : students) {
            // 受講情報（仮申込）を取得
            List<StudentsCourse> studentsCourses = repository.getStudentCourses(student.getId());

            // 最新の受講ステータス（本申込）を取得
            List<CourseStatusDTO> courseStatuses = repository.getCourseStatuses(student.getId());

            // StudentDetail を作成
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student);
            studentDetail.setStudentCourseList(studentsCourses);
            studentDetail.setCourseStatuses(courseStatuses);

            studentDetails.add(studentDetail);
        }

        return studentDetails;
    }

    /*受講生の全ての情報を取得する。ただしisDeletedがtrueの受講生情報は除外する*/
    public List<StudentDetail> getAllStudentsWithCourseStatuses() {
        List<Student> students = repository.findAllStudents();

        List<StudentDetail> studentDetails = new ArrayList<>();

        for (Student student : students) {
            // 'isDeleted' が true の場合はその学生を除外
            if ( student.getIsDeleted() ) {
                continue; // この学生をスキップ
            }

            // 学生のコース情報を取得
            List<StudentsCourse> studentsCourses = repository.getStudentCourses(student.getId());

            // 最新の受講ステータスを取得
            List<CourseStatusDTO> courseStatuses = repository.getLatestCourseStatus(student.getId());

            // StudentDetail を作成
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student); // 学生情報をセット
            studentDetail.setStudentCourseList(studentsCourses); // コース情報をセット
            studentDetail.setCourseStatuses(courseStatuses); // 最新の受講ステータスをセット

            studentDetails.add(studentDetail);
        }

        return studentDetails;
    }

    /*性別から受講生を検索する*/
    public List<Student> getStudentByGender(String gender) {
        log.info("Searching students with gender: {}", gender);  // genderパラメータのログ
        if ( gender == null || gender.isEmpty() || (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female") && !gender.equalsIgnoreCase("Other")) ) {
            throw new IllegalArgumentException("Invalid gender value");
        }
        return repository.findStudentByGender(gender);
    }

    /*受講生詳細検索。
     * IDに紐づく任意の受講生の情報を取得する。
     * @param id 受講生ID
     * @return 受講生詳細*/
    public StudentDetail searchStudent(Integer studentId, String gender) {
        List<Student> students = new ArrayList<>();

        if ( studentId != null ) {
            Long studentIdLong = studentId.longValue();
            Optional<Student> optionalStudent = repository.findStudentById(studentIdLong);
            optionalStudent.ifPresent(students::add);
        } else if ( gender != null ) {
            students = repository.findStudentByGender(gender);
        } else {
            throw new IllegalArgumentException("Either studentId or gender must be provided.");
        }

        if ( students.isEmpty() ) {
            return null;
        }

        // ログを追加して studentName を確認
        log.info("Student name: {}", students.get(0).getStudentName());

        List<StudentsCourse> studentsCourses = repository.getStudentCourses(students.get(0).getId());
        List<CourseStatusDTO> courseStatuses = repository.getLatestCourseStatus(students.get(0).getId());

        // StudentDetail を作成
        StudentDetail studentDetail = new StudentDetail(students.get(0), studentsCourses, courseStatuses);  // 名前もセットされる

        return studentDetail;
    }

    /*特定の性別の受講生情報を全て取得する*/
    public List<StudentDetail> searchStudentsByGender(String gender) {
        // 性別が無効な場合はエラーをスロー
        if ( gender == null || gender.isEmpty() || (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female") && !gender.equalsIgnoreCase("Other")) ) {
            throw new IllegalArgumentException("Invalid gender value");
        }

        List<StudentDetail> studentDetails = new ArrayList<>();

        // 性別で学生を検索
        List<Student> students = repository.findStudentByGender(gender);

        if ( students.isEmpty() ) {
            throw new IllegalArgumentException("No students found for the given gender.");
        }

        // 各学生に対して、コース情報と受講ステータスを取得
        for (Student student : students) {
            List<StudentsCourse> studentsCourses = repository.getStudentCourses(student.getId());
            List<CourseStatusDTO> courseStatuses = repository.getLatestCourseStatus(student.getId());

            // StudentDetail を作成
            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student); // 学生情報をセット
            studentDetail.setStudentCourseList(studentsCourses); // コース情報をセット
            studentDetail.setCourseStatuses(courseStatuses); // 最新の受講ステータスをセット

            studentDetails.add(studentDetail);
        }

        return studentDetails;
    }

    public StudentDetail searchStudentById(Integer studentId) {
        // 学生をIDで検索
        Student student = repository.findStudentById(studentId.longValue())
                .orElseThrow(() -> new NoSuchElementException("Student not found with ID: " + studentId));

        // 学生の受講コースを取得して変換
        List<StudentsCourse> studentCourseList = repository.getStudentCourses(student.getId()).stream()
                .map(dto -> new StudentsCourse(
                        dto.getId(),
                        dto.getStartDate(),  // String型の日付
                        dto.getEndDate(),    // String型の日付
                        dto.getStatus(),
                        dto.getStudentId(),
                        dto.getCourseName()))
                .collect(Collectors.toList());  // List<StudentsCourse>に変換

        // コースステータスを取得
        List<CourseStatusDTO> courseStatusList = repository.getCourseStatuses(student.getId());

        // StudentDetailを返す
        return new StudentDetail(student, studentCourseList, courseStatusList);
    }

    public List<CourseStatusDTO> getCourseStatuses(Integer studentId) {
        if ( studentId == null ) {
            throw new IllegalArgumentException("studentId cannot be null");
        }

        // sqlSession を使ってクエリを実行
        return sqlsession.selectList("StudentManagementMapper.getCourseStatuses", studentId);
    }

    /*特定のコース名の受講生情報を全て取得する*/
    public List<StudentDetail> searchStudentsByCourseName(String courseName) {
        List<Student> students = repository.findStudentsByCourseName(courseName);
        List<StudentDetail> studentDetails = new ArrayList<>();

        for (Student student : students) {
            List<StudentsCourse> studentsCourses = repository.getStudentCourses(student.getId());

            log.info("Student Courses for {}: {}", student.getStudentName(), studentsCourses);

            List<CourseStatusDTO> courseStatuses = repository.getLatestCourseStatus(student.getId());

            StudentDetail studentDetail = new StudentDetail();
            studentDetail.setStudent(student);
            studentDetail.setStudentCourseList(studentsCourses);
            studentDetail.setCourseStatuses(courseStatuses);

            studentDetails.add(studentDetail);
        }

        return studentDetails;
    }

    public StudentDetail getStudentDetail(Long studentId) {
        Student student = repository.findStudentById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<StudentsCourse> courses = repository.findCoursesByStudentId(studentId);
        List<StudentDetail> details = converter.convertStudentDetails(List.of(student), courses);

        if ( details.isEmpty() ) {
            throw new IllegalStateException("Conversion to StudentDetail failed");
        }

        return details.get(0);
    }

    public List<Student> fetchStudentsFromApi() {
        try {
            URL url = new URL("http://localhost:8080/studentList");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line.trim());
            }
            reader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return objectMapper.readValue(response.toString(), new TypeReference<List<Student>>() {
            });
        } catch (Exception e) {
            log.error("Failed to fetch students from API", e);
            throw new RuntimeException("Unable to fetch students from API.", e);
        }
    }

    /*受講生詳細の登録を行う。
     *受講生と受講生コース情報を個別に登録し、受講生コース情報には受講生情報を
     *紐づける値とコース開始日、コース終了日を設定する。
     *@param studentDetail 受講生詳細
     *@return 登録情報を付与した受講生詳細*/
    @Transactional
    public StudentDetail registerStudent(@Valid StudentDetail studentDetail) {
        try {
            if ( studentDetail == null || studentDetail.getStudent() == null ) {
                throw new IllegalArgumentException("StudentDetail or Student cannot be null.");
            }

            // 学生情報を登録
            Student student = studentDetail.getStudent();
            repository.registerStudent(student);

            Integer generatedId = student.getId();
            if ( generatedId == null ) {
                throw new IllegalStateException("Student ID was not generated after registration.");
            }

            Long generatedIdLong = generatedId.longValue();

            // 受講コース情報とステータスを登録
            if ( studentDetail.getStudentCourseList() != null ) {
                studentDetail.getStudentCourseList().forEach(studentsCourses -> {
                    initStudentsCourses(studentsCourses, generatedId);
                    repository.registerStudentCourse(studentsCourses);
                    log.info("Registered course ID: {}", studentsCourses.getId());

                    // 受講状況（CourseStatusDTO）の登録
                    if ( studentsCourses.getId() != null && studentsCourses.getStatus() != null ) {
                        log.info("Registering course status for course ID {}: {}", studentsCourses.getId(), studentsCourses.getStatus());
                        repository.registerCourseStatus(studentsCourses.getId(), studentsCourses.getStatus());
                        log.info("Registered status for course ID {}: {}", studentsCourses.getId(), studentsCourses.getStatus());
                    } else {
                        log.warn("Status is null for course ID {}", studentsCourses.getId());
                    }
                });
            }

            // 最新のStudentを取得し、詳細を返す
            Optional<Student> savedStudentOptional = repository.findStudentById(generatedIdLong);
            if ( savedStudentOptional.isPresent() ) {
                Student savedStudent = savedStudentOptional.get();
                List<StudentsCourse> studentCourses = repository.findStudentCoursesById(generatedIdLong);

                // StudentDetailの返却
                List<CourseStatusDTO> courseStatuses = getStudentCourseStatus(generatedId); // ステータスも取得
                return new StudentDetail(savedStudent, studentCourses, courseStatuses);
            } else {
                throw new IllegalStateException("Failed to retrieve the registered student.");
            }
        } catch (Exception e) {
            log.error("Unexpected error occurred during student registration: {}", e.getMessage(), e);
            throw new RuntimeException("Unexpected error during registration.", e);
        }
    }

    /*受講生コース情報を登録する際の初期情報を設定する。
     *@param studentsCourses 受講生コース情報
     *@param student 受講生*/
    public void initStudentsCourses(StudentsCourse studentCourse, Integer generatedId) {
        studentCourse.setStudentId(generatedId);  // そのまま Integer をセット
        LocalDate now = LocalDate.now();
        studentCourse.setStartDate(now);
        studentCourse.setEndDate(now.plusYears(1));
    }

    /*受講生のコース受講状況を取得する*/
    public List<CourseStatusDTO> getStudentCourseStatus(Integer studentId) {
        return repository.findStudentCourseStatus(studentId);
    }

    /*受講生のコース受講状況を更新する*/
    @Transactional
    public void updateStudentCourseStatus(Integer studentsCoursesId, String status) {
        repository.updateStudentCourseStatus(studentsCoursesId, status);
    }

    public List<CourseStatusDTO> getCourseStatusesByStudentId(Integer studentId) {
        return repository.getCourseStatusesByStudentId(studentId);
    }

    public void markAsDeleted(Long studentId) {
        log.debug("Marking student as deleted with ID: {}", studentId);
        repository.updateIsDeleted(studentId, true);
    }
    /*lombokを使用している場合、import lombok.extern.slf4j.Slf4j; @Slf4jを
     * 使うことでログを表示できる。主にデバッグで使用する*/

    /*受講生詳細の更新を行う。
     * 受講生と受講生コース情報をそれぞれ更新する。
     * @param studentDetail 受講生詳細*/
    @Transactional
    public void updateStudent(StudentDetail studentDetail) {
        if ( studentDetail.getStudent() == null ) {
            throw new IllegalArgumentException("Student object cannot be null.");
        }

        int updatedRows = repository.updateStudent(studentDetail.getStudent());
        if ( updatedRows == 0 ) {
            throw new IllegalStateException("Failed to update student. Student with ID "
                    + studentDetail.getStudent().getId() + " not found.");
        }
    }

    @Validated
    @Transactional
    public void updateStudentWithCourses(@Valid StudentDetail studentDetail) {
        int updatedRows = repository.updateStudent(studentDetail.getStudent());
        if ( updatedRows == 0 ) {
            throw new IllegalStateException("Failed to update student. Student with ID "
                    + studentDetail.getStudent().getId() + " not found.");
        }

        studentDetail.getStudentCourseList().forEach(studentsCourses -> {
            studentsCourses.setStudentId(studentDetail.getStudent().getId());
            repository.updateStudentCourse(studentsCourses);
        });
    }
}
/*@Transactionalをメソッドやクラスに付与すると、その範囲内でのデータベース操作がトランザクションとして
 * 扱われる。メソッドの実行開始時にトランザクションが行われ、正常に終了するとコミットし、例外が発生すると
 * 自動的にロールバックする。このロールバック対象の例外を自由にカスタマイズすることが可能。データの変更を
 * 行わない場合、読み取り専用モードにも設定できる*/
        /*本来はnewが入らないとインスタンスとして機能しないが、SpringBootの@Serviceで
        インスタンスとして呼び出すことが可能。更にAutowiredでStudentManagementApplicationの
        repositoryを呼び出せる。これを行うことで上書きが容易になる。
        @Autowiredとは、Springフレームワークで用いるアノテーションのひとつ。これを記述するだけで
        インスタンス化を１回で行える。また、クラス内のnew演算子を消すことができる。つまりこのクラスでは
        @Autowiredを使うことで全てのpublic変数をインスタンス化させ、newをいちいち記述する必要が
        ないようにしている。*/