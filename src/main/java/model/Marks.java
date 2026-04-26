package model;

// -------------------------------------------------------
// Marks – CA (40) and End exam (60) for one student in one course
// OOP: Encapsulation
// -------------------------------------------------------
public class Marks {

    private int    id;
    private String studentId;
    private String courseCode;
    private double caMarks;     // Continuous Assessment (out of 40)
    private double finalMarks;  // End exam (out of 60)

    public Marks(int id, String studentId, String courseCode,
                 double caMarks, double finalMarks) {
        this.id         = id;
        this.studentId  = studentId;
        this.courseCode = courseCode;
        this.caMarks    = caMarks;
        this.finalMarks = finalMarks;
    }

    public int    getId()         { return id; }
    public String getStudentId()  { return studentId; }
    public String getCourseCode() { return courseCode; }
    public double getCaMarks()    { return caMarks; }
    public double getFinalMarks() { return finalMarks; }

    public void setCaMarks(double v)    { this.caMarks = v; }
    public void setFinalMarks(double v) { this.finalMarks = v; }

    // CA eligible = at least 40% of CA component (16/40)
    public boolean isCaEligible() { return caMarks >= 16; }
}
