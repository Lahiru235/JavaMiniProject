package model;

// -------------------------------------------------------
// GradeResult – computed grade info for one student/course
// OOP: Encapsulation
// -------------------------------------------------------
public class GradeResult {

    private String studentId;
    private String studentName;
    private String courseCode;
    private int    credits;
    private double caMarks;
    private double finalMarks;  // End exam marks (out of 60)
    private double totalMarks;  // CA + End exam (out of 100)
    private String grade;       // A+, A, B+, … E
    private double gradePoint;  // 4.0, 3.7, …

    public GradeResult(String studentId, String studentName,
                       String courseCode, int credits,
                       double caMarks, double finalMarks, double totalMarks,
                       String grade, double gradePoint) {
        this.studentId   = studentId;
        this.studentName = studentName;
        this.courseCode  = courseCode;
        this.credits     = credits;
        this.caMarks     = caMarks;
        this.finalMarks  = finalMarks;
        this.totalMarks  = totalMarks;
        this.grade       = grade;
        this.gradePoint  = gradePoint;
    }

    public String getStudentId()   { return studentId; }
    public String getStudentName() { return studentName; }
    public String getCourseCode()  { return courseCode; }
    public int    getCredits()     { return credits; }
    public double getCaMarks()     { return caMarks; }
    public double getFinalMarks()  { return finalMarks; }
    public double getTotalMarks()  { return totalMarks; }
    public String getGrade()       { return grade; }
    public double getGradePoint()  { return gradePoint; }
    public String getCaStatus()    { return caMarks >= 16 ? "Pass" : "Fail"; }
}
