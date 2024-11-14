package student.management.StudentManagement;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

    @Getter
    @Setter
    public class StudentsCourses {
        private int id;
        private int studentId;
        private String courseName;
        private Date startDate;
        private Date endDate;
    }
