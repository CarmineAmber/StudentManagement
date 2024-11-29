package student.management.StudentManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }
}
/*クラスを分けることで、mainとなるStudentManagementApplicationクラスのコードの
 * 可読性が高くなる。*/
