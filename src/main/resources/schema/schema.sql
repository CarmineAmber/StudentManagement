CREATE TABLE students_courses_status (
    id INT PRIMARY KEY AUTO_INCREMENT,
    students_courses_id INT NOT NULL,
    status ENUM('仮申込', '本申込', '受講中', '受講終了') NOT NULL,
    FOREIGN KEY (students_courses_id) REFERENCES students_courses(id) ON DELETE CASCADE
);