-- Faculty of Technology Management System


USE faculty_db;

-- 1 admin, 5 lecturers, 4 technical officers, 20 students
-- Faculty of Technology Management System


USE faculty_db;

INSERT INTO users (username,password,full_name,email,phone,role,dept) VALUES
('admin','admin123','Rajith Kumara','admin@fot.ruh.ac.lk','0710000000','ADMIN',NULL),
('lec001','1234','Dr. Roshan Jayasuriya','lec001@fot.ruh.ac.lk','0711000001','LECTURER','ICT'),
('lec002','1234','Ms. Amara Dissanayake','lec002@fot.ruh.ac.lk','0711000002','LECTURER','ICT'),
('lec003','1234','Mr. Chaminda Karunarathne','lec003@fot.ruh.ac.lk','0711000003','LECTURER','ICT'),
('lec004','1234','Ms. Dilani Wijesinghe','lec004@fot.ruh.ac.lk','0711000004','LECTURER','ICT'),
('lec005','1234','Mr. Suresh Bandara','lec005@fot.ruh.ac.lk','0711000005','LECTURER','ICT'),
('tech001','1234','Mahesh Seneviratne','tech001@fot.ruh.ac.lk','0722000001','TECH','ICT'),
('tech002','1234','Indika Perera','tech002@fot.ruh.ac.lk','0722000002','TECH','ICT'),
('tech003','1234','Kasun Jayawardene','tech003@fot.ruh.ac.lk','0722000003','TECH','ICT'),
('tech004','1234','Nimal Gamage','tech004@fot.ruh.ac.lk','0722000004','TECH','ICT'),
('TG1701','1234','Anura Mendis','tg1701@fot.ruh.ac.lk','0750000001','STUDENT','ICT'),
('TG1702','1234','Sampath Fernandez','tg1702@fot.ruh.ac.lk','0750000002','STUDENT','ICT'),
('TG1703','1234','Chathuri Wijesinghe','tg1703@fot.ruh.ac.lk','0750000003','STUDENT','ICT'),
('TG1704','1234','Lakshan De Silva','tg1704@fot.ruh.ac.lk','0750000004','STUDENT','ICT'),
('TG1705','1234','Priyanka Jayarathne','tg1705@fot.ruh.ac.lk','0750000005','STUDENT','ICT'),
('TG1706','1234','Nishan Herath','tg1706@fot.ruh.ac.lk','0750000006','STUDENT','ICT'),
('TG1707','1234','Sandeep Kumara','tg1707@fot.ruh.ac.lk','0750000007','STUDENT','ICT'),
('TG1708','1234','Vishal Weerasinghe','tg1708@fot.ruh.ac.lk','0750000008','STUDENT','ICT'),
('TG1709','1234','Anusha Mahawatte','tg1709@fot.ruh.ac.lk','0750000009','STUDENT','ICT'),
('TG1710','1234','Deshani Ranaweera','tg1710@fot.ruh.ac.lk','0750000010','STUDENT','ICT'),
('TG1711','1234','Tharaka Wickramasooriya','tg1711@fot.ruh.ac.lk','0750000011','STUDENT','ICT'),
('TG1712','1234','Harshana Gunawardene','tg1712@fot.ruh.ac.lk','0750000012','STUDENT','ICT'),
('TG1713','1234','Madhuri Seneviratne','tg1713@fot.ruh.ac.lk','0750000013','STUDENT','ICT'),
('TG1714','1234','Ashok Jayasena','tg1714@fot.ruh.ac.lk','0750000014','STUDENT','ICT'),
('TG1715','1234','Devni Balasuriya','tg1715@fot.ruh.ac.lk','0750000015','STUDENT','ICT'),
('TG1716','1234','Naveen Dissanayake','tg1716@fot.ruh.ac.lk','0750000016','STUDENT','ICT'),
('TG1717','1234','Sushmita Abeysekera','tg1717@fot.ruh.ac.lk','0750000017','STUDENT','ICT'),
('TG1718','1234','Jayantha Kumara','tg1718@fot.ruh.ac.lk','0750000018','STUDENT','ICT'),
('TG1719','1234','Sachini Rathnayake','tg1719@fot.ruh.ac.lk','0750000019','STUDENT','ICT'),
('TG1720','1234','Vivek Perera','tg1720@fot.ruh.ac.lk','0750000020','STUDENT','ICT')
ON DUPLICATE KEY UPDATE
full_name=VALUES(full_name), password=VALUES(password), email=VALUES(email),
phone=VALUES(phone), role=VALUES(role), dept=VALUES(dept);

INSERT INTO courses (course_code,course_name,credits,lecturer_id) VALUES
('ICT2132','Object Oriented Programming Practicum',3,(SELECT id FROM users WHERE username='lec001')),
('ICT2122','Database Systems',3,(SELECT id FROM users WHERE username='lec002')),
('ICT2112','Computer Networks',3,(SELECT id FROM users WHERE username='lec003')),
('ICT2142','Web Technologies',3,(SELECT id FROM users WHERE username='lec004')),
('ICT2152','Software Engineering',3,(SELECT id FROM users WHERE username='lec005'))
ON DUPLICATE KEY UPDATE
course_name=VALUES(course_name), credits=VALUES(credits), lecturer_id=VALUES(lecturer_id);

INSERT INTO notices (title,content,created_by) VALUES
('Semester Started','Welcome to the semester. Keep your attendance above 80%.',(SELECT id FROM users WHERE username='admin')),
('Marks Upload Window','Quiz/Mid/End marks upload opens on Monday.',(SELECT id FROM users WHERE username='admin')),
('Medical Submission','Submit medical records within 3 working days.',(SELECT id FROM users WHERE username='admin'));

INSERT INTO timetables (dept,course_code,day_of_week,start_time,end_time,venue,note) VALUES
('ICT','ICT2132','Monday','08:30','10:30','Lab 01','Practical'),
('ICT','ICT2132','Wednesday','10:30','12:30','LH 02','Theory'),
('ICT','ICT2122','Tuesday','08:30','10:30','LH 03','Theory'),
('ICT','ICT2112','Thursday','08:30','10:30','LH 01','Theory'),
('ICT','ICT2142','Friday','13:30','15:30','Lab 02','Practical'),
('ICT','ICT2152','Saturday','08:30','10:30','LH 04','Theory');

-- Generic marks for all students/courses (CA out of 40, End exam out of 60)
DROP PROCEDURE IF EXISTS seed_marks;
DELIMITER //
CREATE PROCEDURE seed_marks()
BEGIN
    DECLARE s INT DEFAULT 1;
    DECLARE sid VARCHAR(20);

    WHILE s <= 20 DO
        IF s < 10 THEN
            SET sid = CONCAT('TG170', s);
        ELSE
            SET sid = CONCAT('TG17', s);
        END IF;

        INSERT INTO marks (student_id, course_code, ca_marks, final_marks)
        VALUES
            (sid, 'ICT2132', 18 + (s MOD 7) * 3, 30 + (s MOD 8) * 3),
            (sid, 'ICT2122', 16 + (s MOD 8) * 3, 27 + (s MOD 9) * 3),
            (sid, 'ICT2112', 17 + (s MOD 7) * 3, 29 + (s MOD 8) * 3),
            (sid, 'ICT2142', 18 + (s MOD 7) * 3, 31 + (s MOD 7) * 3),
            (sid, 'ICT2152', 19 + (s MOD 7) * 3, 33 + (s MOD 7) * 3)
        ON DUPLICATE KEY UPDATE
            ca_marks = VALUES(ca_marks),
            final_marks = VALUES(final_marks);

        SET s = s + 1;
    END WHILE;
END //
DELIMITER ;

CALL seed_marks();
DROP PROCEDURE IF EXISTS seed_marks;

-- Attendance for ICT2132 with theory and practical (15 sessions each)
-- Scenarios included:
-- TG1701 >80, TG1702 =80, TG1703 <80 without medical,
-- TG1704 <80 raw but >80 effective with medical,
-- TG1705 <80 even with medical.
DROP PROCEDURE IF EXISTS seed_attendance_ict2132;
DELIMITER //
CREATE PROCEDURE seed_attendance_ict2132()
BEGIN
    DECLARE s INT DEFAULT 1;
    DECLARE sess INT;
    DECLARE sid VARCHAR(20);
    DECLARE presentLimit INT;

    WHILE s <= 5 DO
        SET sid = CONCAT('TG170', s);

        IF s = 1 THEN SET presentLimit = 13; END IF;
        IF s = 2 THEN SET presentLimit = 12; END IF;
        IF s = 3 THEN SET presentLimit = 10; END IF;
        IF s = 4 THEN SET presentLimit = 11; END IF;
        IF s = 5 THEN SET presentLimit = 8;  END IF;

        SET sess = 1;
        WHILE sess <= 15 DO
            INSERT INTO attendance (student_id,course_code,session_no,session_type,is_present,session_date)
            VALUES (sid,'ICT2132',sess,'THEORY',IF(sess <= presentLimit,1,0),DATE_ADD('2026-03-02', INTERVAL (sess-1)*7 DAY))
            ON DUPLICATE KEY UPDATE is_present=VALUES(is_present), session_date=VALUES(session_date);

            INSERT INTO attendance (student_id,course_code,session_no,session_type,is_present,session_date)
            VALUES (sid,'ICT2132',sess,'PRACTICAL',IF(sess <= presentLimit,1,0),DATE_ADD('2026-03-04', INTERVAL (sess-1)*7 DAY))
            ON DUPLICATE KEY UPDATE is_present=VALUES(is_present), session_date=VALUES(session_date);

            SET sess = sess + 1;
        END WHILE;

        SET s = s + 1;
    END WHILE;
END //
DELIMITER ;

CALL seed_attendance_ict2132();
DROP PROCEDURE IF EXISTS seed_attendance_ict2132;

INSERT INTO medicals (student_id,from_date,to_date,reason,is_approved) VALUES
('TG1704','2026-05-18','2026-05-24','Approved medical leave',1),
('TG1705','2026-04-20','2026-04-24','Approved medical leave',1),
('TG1706','2026-04-10','2026-04-12','Pending medical sample',0);

INSERT INTO course_materials (course_code,title,material_type,material_link,description,uploaded_by)
VALUES
('ICT2132','OOP Intro Slides','SLIDE','https://example.com/oop-intro','Week 1 lecture slides',(SELECT id FROM users WHERE username='lec001')),
('ICT2122','Normalization Notes','DOCUMENT','https://example.com/db-notes','ER and normalization notes',(SELECT id FROM users WHERE username='lec002'));

-- Update System Admin
UPDATE users SET full_name = 'Rajith Kumara' WHERE id = 1;

UPDATE users SET full_name = 'Dr. Roshan Jayasuriya' WHERE id = 2;
UPDATE users SET full_name = 'Ms. Amara Dissanayake' WHERE id = 3;
UPDATE users SET full_name = 'Mr. Chaminda Karunarathne' WHERE id = 4;
UPDATE users SET full_name = 'Ms. Dilani Wijesinghe' WHERE id = 5;
UPDATE users SET full_name = 'Mr. Suresh Bandara' WHERE id = 6;


UPDATE users SET full_name = 'Mahesh Seneviratne' WHERE id = 7;
UPDATE users SET full_name = 'Indika Perera' WHERE id = 8;
UPDATE users SET full_name = 'Kasun Jayawardene' WHERE id = 9;
UPDATE users SET full_name = 'Nimal Gamage' WHERE id = 10;


UPDATE users SET full_name = 'Anura Mendis' WHERE id = 11;
UPDATE users SET full_name = 'Sampath Fernandez' WHERE id = 12;
UPDATE users SET full_name = 'Chathuri Wijesinghe' WHERE id = 13;
UPDATE users SET full_name = 'Lakshan De Silva' WHERE id = 14;
UPDATE users SET full_name = 'Priyanka Jayarathne' WHERE id = 15;
UPDATE users SET full_name = 'Nishan Herath' WHERE id = 16;
UPDATE users SET full_name = 'Sandeep Kumara' WHERE id = 17;
UPDATE users SET full_name = 'Vishal Weerasinghe' WHERE id = 18;
UPDATE users SET full_name = 'Anusha Mahawatte' WHERE id = 19;
UPDATE users SET full_name = 'Deshani Ranaweera' WHERE id = 20;
UPDATE users SET full_name = 'Tharaka Wickramasooriya' WHERE id = 21;
UPDATE users SET full_name = 'Harshana Gunawardene' WHERE id = 22;
UPDATE users SET full_name = 'Madhuri Seneviratne' WHERE id = 23;
UPDATE users SET full_name = 'Ashok Jayasena' WHERE id = 24;
UPDATE users SET full_name = 'Devni Balasuriya' WHERE id = 25;
UPDATE users SET full_name = 'Naveen Dissanayake' WHERE id = 26;
UPDATE users SET full_name = 'Sushmita Abeysekera' WHERE id = 27;
UPDATE users SET full_name = 'Jayantha Kumara' WHERE id = 28;
UPDATE users SET full_name = 'Sachini Rathnayake' WHERE id = 29;
UPDATE users SET full_name = 'Vivek Perera' WHERE id = 30;
UPDATE users SET full_name = 'Udesh Maduranga' WHERE id = 31;
UPDATE users SET full_name = 'Nadeesha Perera' WHERE id = 32;
UPDATE users SET full_name = 'Ravinda Mallikarachchi' WHERE id = 33;
