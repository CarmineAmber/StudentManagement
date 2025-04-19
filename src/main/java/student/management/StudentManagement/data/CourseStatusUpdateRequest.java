package student.management.StudentManagement.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseStatusUpdateRequest {
    private Integer studentsCoursesId;
    private String status;
}
/*受講生の最新の受講状況を更新するためのクラス。パラメータをリクエストボディで送信するために
 * このクラスを作成している*/