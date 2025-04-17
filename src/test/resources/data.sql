DELETE FROM students_courses;
DELETE FROM students;
-- studentsテーブルにデータを挿入
INSERT INTO students (id, name, furigana, nickname, email, region, age, gender, remark, isdeleted)
VALUES
(1, 'A', 'ア', 'A', 'a@example.com', 'Tokyo', 24, 'Female', 'remarks1', false),
(2, 'B', 'ボ', 'B', 'b@example.com', 'Osaka', 22, 'Male', 'remarks2', false);

-- students_coursesテーブルにデータを挿入
INSERT INTO students_courses (id, student_id, course_name, start_date, end_date) 
VALUES 
(1, 1, 'Python', '2024-11-10 00:00:00', '2025-11-09 00:00:00'),
(2, 2, 'JAVA', '2024-11-10 00:00:00', '2025-11-09 00:00:00');
