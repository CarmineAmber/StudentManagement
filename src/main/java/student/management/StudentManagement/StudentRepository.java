package student.management.StudentManagement;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentRepository {

    @Select("SELECT * FROM student WHERE name = #{name}")
    Student searchByName(String name);

    @Insert("INSERT INTO student (name, age) VALUES (#{name}, #{age})")
    void registerStudent(String name, int age);
    /*void：何も返さないということ。*/

    @Update("UPDATE student SET age = #{age} WHERE name = #{name}")
    void updateStudent(String name, int age);

    @Delete("DELETE FROM student WHERE name = #{name}")
    void deleteStudent(String name);

    @Select("SELECT * FROM student")
    List<Student> getAllStudents();
}
/*このインターフェースがデータベースの根幹となる。*/