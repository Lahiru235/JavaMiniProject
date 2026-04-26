package model;

public class Course {

    private int    id;
    private String courseCode;
    private String courseName;
    private int    credits;
    private int    lecturerId;
    private String lecturerName;

    public Course(int id, String courseCode, String courseName,
                  int credits, int lecturerId, String lecturerName) {
        this.id           = id;
        this.courseCode   = courseCode;
        this.courseName   = courseName;
        this.credits      = credits;
        this.lecturerId   = lecturerId;
        this.lecturerName = lecturerName;
    }

    public int    getId()           { return id; }
    public String getCourseCode()   { return courseCode; }
    public String getCourseName()   { return courseName; }
    public int    getCredits()      { return credits; }
    public int    getLecturerId()   { return lecturerId; }
    public String getLecturerName() { return lecturerName; }

    public void setCourseCode(String v)   { courseCode = v; }
    public void setCourseName(String v)   { courseName = v; }
    public void setCredits(int v)         { credits = v; }
    public void setLecturerId(int v)      { lecturerId = v; }
    public void setLecturerName(String v) { lecturerName = v; }
}
