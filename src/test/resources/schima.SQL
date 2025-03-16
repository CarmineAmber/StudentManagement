CREATE TABLE IF NOT EXISTS students
(
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    furigana VARCHAR(255),
    nickname VARCHAR(50),
    email VARCHAR(255),
    region VARCHAR(100),
    age INT,
    gender VARCHAR(50),
    remark VARCHAR(255),
    isdeleted BOOLEAN
);

CREATE TABLE IF NOT EXISTS students_courses
(
    id INT PRIMARY KEY,
    student_id INT NOT NULL,
    course_name VARCHAR(100),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id)  -- 外部キー制約
);
