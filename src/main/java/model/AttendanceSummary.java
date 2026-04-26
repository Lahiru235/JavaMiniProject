package model;

// -------------------------------------------------------
// AttendanceSummary – calculated attendance for one student
// in one course.  Used to fill the summary table in the GUI.
// OOP: Encapsulation
// -------------------------------------------------------
public class AttendanceSummary {

    private String studentId;
    private String studentName;
    private String courseCode;
    private int    totalSessions;   // total sessions held
    private int    presentSessions; // sessions the student attended
    private int    medicalSessions; // absences covered by approved medical
    private double percentage;      // raw percentage
    private double effectivePct;    // percentage after adding medical sessions

    public AttendanceSummary(String studentId, String studentName,
                              String courseCode,
                              int totalSessions, int presentSessions,
                              int medicalSessions) {
        this.studentId      = studentId;
        this.studentName    = studentName;
        this.courseCode     = courseCode;
        this.totalSessions  = totalSessions;
        this.presentSessions = presentSessions;
        this.medicalSessions = medicalSessions;

        // Raw %
        this.percentage     = totalSessions == 0 ? 0
                              : (presentSessions * 100.0) / totalSessions;
        // Effective % (present + approved medical absences)
        this.effectivePct   = totalSessions == 0 ? 0
                              : ((presentSessions + medicalSessions) * 100.0) / totalSessions;
    }

    public String getStudentId()      { return studentId; }
    public String getStudentName()    { return studentName; }
    public String getCourseCode()     { return courseCode; }
    public int    getTotalSessions()  { return totalSessions; }
    public int    getPresentSessions(){ return presentSessions; }
    public int    getMedicalSessions(){ return medicalSessions; }
    public double getPercentage()     { return Math.round(percentage * 10.0) / 10.0; }
    public double getEffectivePct()   { return Math.round(effectivePct * 10.0) / 10.0; }

    // "Eligible" = effective % >= 80
    public String getEligibility() {
        return effectivePct >= 80 ? "Eligible" : "Not Eligible";
    }
}
