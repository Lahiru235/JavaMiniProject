package model;

public class EligibilityResult {

    private String studentId;
    private String studentName;
    private String courseCode;
    private double attendancePct;
    private double caMarks;
    private boolean eligible;

    public EligibilityResult(String studentId, String studentName,
                             String courseCode, double attendancePct,
                             double caMarks, boolean eligible) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.attendancePct = attendancePct;
        this.caMarks = caMarks;
        this.eligible = eligible;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getCourseCode() { return courseCode; }
    public double getAttendancePct() { return attendancePct; }
    public double getCaMarks() { return caMarks; }
    public String getStatus() { return eligible ? "Eligible" : "Not Eligible"; }
    public boolean isEligible() { return eligible; }
}
