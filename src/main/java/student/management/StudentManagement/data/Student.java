package student.management.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {
    private int id;
    private String name;
    private String furigana;
    private String nickName;
    private String eMail;
    private String region;
    private int age;
    private String gender;
}