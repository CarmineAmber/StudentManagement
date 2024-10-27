package student.management.StudentManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class StudentManagementApplication {

    private String name1 = "T";
    private String age1 = "34";
    private String name2 = "Y";
    private String age2 = "27";
    private String name3 = "F";
    private String age3 = "46";
    private String name4 = "M";
    private String age4 = "19";
    private String name5 = "O";
    private String age5 = "52";

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }

    @GetMapping("/showMap")
    public Map<String,String> getMap(){
        Map<String,String> map = new HashMap<>();
        map.put(name1, age1 + "歳");
        map.put(name2, age2 + "歳");
        map.put(name3, age3 + "歳");
        map.put(name4, age4 + "歳");
        map.put(name5, age5 + "歳");
        return map;
    }

    @PostMapping("/showMap")
        public ResponseEntity<String> updateInfoMap(@RequestBody InfoMap infoMap) {
            this.name1 = infoMap.getName1();
            this.age1 = infoMap.getAge1();
            this.name2 = infoMap.getName2();
            this.age2 = infoMap.getAge2();
            this.name3 = infoMap.getName3();
            this.age3 = infoMap.getAge3();
            this.name4 = infoMap.getName4();
            this.age4 = infoMap.getAge4();
            this.name5 = infoMap.getName5();
            this.age5 = infoMap.getAge5();

            return ResponseEntity.ok("Data updated successfully");
    }
}

