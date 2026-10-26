-- Student Management System schema (MySQL 8+)
CREATE DATABASE IF NOT EXISTS student_mgmt;
USE student_mgmt;

CREATE TABLE IF NOT EXISTS faculty (
    faculty_id  INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(120) NOT NULL UNIQUE,
    phone       VARCHAR(20),
    department  VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS course (
    course_id   INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20)  NOT NULL UNIQUE,
    title       VARCHAR(120) NOT NULL,
    credits     INT NOT NULL CHECK (credits > 0),
    faculty_id  INT NULL,
    CONSTRAINT fk_course_faculty FOREIGN KEY (faculty_id)
        REFERENCES faculty(faculty_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS student (
    student_id  INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    email       VARCHAR(120) NOT NULL UNIQUE,
    phone       VARCHAR(20),
    dob         DATE,
    department  VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS enrollment (
    student_id  INT NOT NULL,
    course_id   INT NOT NULL,
    enrolled_on DATE NOT NULL DEFAULT (CURRENT_DATE),
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES course(course_id)  ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS exam (
    exam_id     INT AUTO_INCREMENT PRIMARY KEY,
    course_id   INT NOT NULL,
    exam_name   VARCHAR(100) NOT NULL,
    exam_date   DATE NOT NULL,
    max_marks   INT NOT NULL CHECK (max_marks > 0),
    FOREIGN KEY (course_id) REFERENCES course(course_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS exam_result (
    exam_id         INT NOT NULL,
    student_id      INT NOT NULL,
    marks_obtained  DECIMAL(6,2) NOT NULL CHECK (marks_obtained >= 0),
    PRIMARY KEY (exam_id, student_id),
    FOREIGN KEY (exam_id)    REFERENCES exam(exam_id)       ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
);

-- Optional sample data
INSERT IGNORE INTO faculty (name, email, phone, department) VALUES
 ('Dr. Anita Rao',  'anita.rao@college.edu',  '9800000001', 'Computer Science'),
 ('Prof. Vikram Shah', 'vikram.shah@college.edu', '9800000002', 'Mathematics');

INSERT IGNORE INTO course (course_code, title, credits, faculty_id) VALUES
 ('CS101', 'Introduction to Programming', 4, 1),
 ('MA101', 'Engineering Mathematics',     3, 2);

INSERT IGNORE INTO student (name, email, phone, dob, department) VALUES
 ('Rahul Verma', 'rahul.verma@college.edu', '9811111111', '2005-03-14', 'Computer Science'),
 ('Sneha Iyer',  'sneha.iyer@college.edu',  '9822222222', '2005-07-21', 'Computer Science');
