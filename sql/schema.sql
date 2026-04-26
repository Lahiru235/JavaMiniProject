-- Faculty of Technology Management System
-- MySQL schema

CREATE DATABASE IF NOT EXISTS faculty_db;
USE faculty_db;

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(40) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(30),
    role VARCHAR(20) NOT NULL,
    dept VARCHAR(40)
);

CREATE TABLE IF NOT EXISTS courses (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(30) NOT NULL UNIQUE,
    course_name VARCHAR(150) NOT NULL,
    credits INT NOT NULL,
    lecturer_id INT,
    CONSTRAINT fk_course_lecturer FOREIGN KEY (lecturer_id)
        REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS notices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(180) NOT NULL,
    content TEXT NOT NULL,
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notice_admin FOREIGN KEY (created_by)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS marks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(40) NOT NULL,
    course_code VARCHAR(30) NOT NULL,
    ca_marks DOUBLE NOT NULL,    -- out of 40 (best 2 quizzes + mid)
    final_marks DOUBLE NOT NULL, -- end exam out of 60
    UNIQUE KEY uq_marks_student_course (student_id, course_code),
    CONSTRAINT fk_marks_student FOREIGN KEY (student_id)
        REFERENCES users(username) ON DELETE CASCADE,
    CONSTRAINT fk_marks_course FOREIGN KEY (course_code)
        REFERENCES courses(course_code) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(40) NOT NULL,
    course_code VARCHAR(30) NOT NULL,
    session_no INT NOT NULL,
    session_type VARCHAR(20) NOT NULL,
    is_present TINYINT(1) NOT NULL,
    session_date DATE NOT NULL,
    UNIQUE KEY uq_attendance_session (student_id, course_code, session_no, session_type),
    CONSTRAINT fk_att_student FOREIGN KEY (student_id)
        REFERENCES users(username) ON DELETE CASCADE,
    CONSTRAINT fk_att_course FOREIGN KEY (course_code)
        REFERENCES courses(course_code) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS medicals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(40) NOT NULL,
    from_date DATE NOT NULL,
    to_date DATE NOT NULL,
    reason VARCHAR(300) NOT NULL,
    is_approved TINYINT(1) DEFAULT 0,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_med_student FOREIGN KEY (student_id)
        REFERENCES users(username) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS timetables (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dept VARCHAR(40) NOT NULL,
    course_code VARCHAR(30) NOT NULL,
    day_of_week VARCHAR(15) NOT NULL,
    start_time VARCHAR(8) NOT NULL,
    end_time VARCHAR(8) NOT NULL,
    venue VARCHAR(80) NOT NULL,
    note VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS course_materials (
    id INT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(30) NOT NULL,
    title VARCHAR(120) NOT NULL,
    material_type VARCHAR(30) NOT NULL,
    material_link VARCHAR(500),
    description VARCHAR(500),
    uploaded_by INT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_material_course (course_code),
    INDEX idx_material_uploader (uploaded_by),
    CONSTRAINT fk_material_uploader FOREIGN KEY (uploaded_by)
        REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS student_profiles (
    user_id INT PRIMARY KEY,
    profile_picture VARCHAR(500),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_student_profile_user FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE
);
