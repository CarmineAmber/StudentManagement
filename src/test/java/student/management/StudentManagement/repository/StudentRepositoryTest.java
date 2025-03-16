package student.management.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.jdbc.core.JdbcTemplate;
import student.management.StudentManagement.data.Student;

import java.util.List;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private StudentRepository sut;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("INSERT INTO students (id, name, furigana, nickname, email, region, age, gender, remark, isdeleted) VALUES (1, 'A', 'ア', 'A', 'a@example.com', 'Tokyo', 24, 'Female', 'remarks1', false)");
        jdbcTemplate.execute("INSERT INTO students (id, name, furigana, nickname, email, region, age, gender, remark, isdeleted) VALUES (2, 'B', 'ボ', 'B', 'b@example.com', 'Osaka', 22, 'Male', 'remarks2', false)");
    }

    @Test
    void 受講生の全件検索が行えること(){
        List<Student> actual = sut.searchStudents();
        assertThat(actual.size()).isEqualTo(2);
    }

}