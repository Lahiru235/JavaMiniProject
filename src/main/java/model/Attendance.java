package model;

import java.time.LocalDate;

// -------------------------------------------------------
// Attendance – stores one session record for one student
// OOP: Encapsulation (private fields + getters/setters)
// -------------------------------------------------------
public class Attendance {

    private int       id;
    private String    studentId;    // e.g. TG1701
    private String    courseCode;   // e.g. ICT2132
    private int       sessionNo;    // 1 – 15
    private String    sessionType;  // "THEORY" or "PRACTICAL"
    private boolean   present;
    private LocalDate sessionDate;

    public Attendance(int id, String studentId, String courseCode,
                      int sessionNo, String sessionType,
                      boolean present, LocalDate sessionDate) {
        this.id          = id;
        this.studentId   = studentId;
        this.courseCode  = courseCode;
        this.sessionNo   = sessionNo;
        this.sessionType = sessionType;
        this.present     = present;
        this.sessionDate = sessionDate;
    }

    public int       getId()          { return id; }
    public String    getStudentId()   { return studentId; }
    public String    getCourseCode()  { return courseCode; }
    public int       getSessionNo()   { return sessionNo; }
    public String    getSessionType() { return sessionType; }
    public boolean   isPresent()      { return present; }
    public LocalDate getSessionDate() { return sessionDate; }

    public void setPresent(boolean present)    { this.present = present; }
    public void setSessionDate(LocalDate date) { this.sessionDate = date; }
}
