package student.management.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class StudentsCourses {
    private int id;
    private int studentId;
    private String courseName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
