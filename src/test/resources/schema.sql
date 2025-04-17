-- students_coursesテーブルとstudentsテーブルの削除（順番通り）
DROP TABLE IF EXISTS students_courses;
DROP TABLE IF EXISTS students;

-- studentsテーブルの作成
CREATE TABLE IF NOT EXISTS students
(
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    furigana VARCHAR(255) NOT NULL,
    nickname VARCHAR(255),
    email VARCHAR(255) NOT NULL UNIQUE,
    region VARCHAR(255),
    age INT,
    gender VARCHAR(50),
    remark TEXT,
    isdeleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- students_coursesテーブルの作成（ON DELETE CASCADEを追加）
CREATE TABLE IF NOT EXISTS students_courses
(
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT NOT NULL,
    course_name VARCHAR(100),
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE  -- 外部キー制約（削除時に関連データも削除）
);
