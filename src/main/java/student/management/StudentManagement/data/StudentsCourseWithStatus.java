package student.management.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/*コース情報と受講状況を組み合わせたクラス*/
@Getter
@Setter
public class StudentsCourseWithStatus {
    private Integer id;
    private Integer studentId;
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}

