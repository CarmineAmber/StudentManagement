package student.management.StudentManagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("student.management.StudentManagement.repository")
@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "受講生管理システム"))
public class StudentManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }
}
/*クラスを分けることで、mainとなるStudentManagementApplicationクラスのコードの
 * 可読性が高くなる。*/
