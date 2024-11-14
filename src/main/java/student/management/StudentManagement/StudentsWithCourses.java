package student.management.StudentManagement;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class StudentsWithCourses {
    private int studentId;
    private String studentName;
    private String furigana;
    private String nickName;
    private String email;
    private String region;
    private int age;
    private String gender;
    private String courseName;
    private Date startDate;
    private Date endDate;
}

