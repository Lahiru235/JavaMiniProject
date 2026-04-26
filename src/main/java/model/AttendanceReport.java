package model;

import dao.AttendanceDAO;
import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------
// AttendanceReport – generates text attendance report
// OOP: INHERITANCE (extends Report)
//      POLYMORPHISM (overrides generate())
// -------------------------------------------------------
public class AttendanceReport extends Report {

    private String studentId;
    private String courseCode;

    public AttendanceReport(String studentId, String courseCode) {
        super("Attendance Report – " + studentId + " / " + courseCode);
        this.studentId  = studentId;
        this.courseCode = courseCode;
    }

    @Override
    public List<String> generate() {
        List<String> lines = new ArrayList<>();
        try {
            AttendanceDAO dao = new AttendanceDAO();
            AttendanceSummary s = dao.getSummary(studentId, courseCode, "ALL");
            lines.add("=== " + title + " ===");
            lines.add("Present: " + s.getPresentSessions() + " / " + s.getTotalSessions());
            lines.add("Raw %: " + s.getPercentage());
            lines.add("Effective %: " + s.getEffectivePct());
            lines.add("Status: " + s.getEligibility());
        } catch (Exception e) {
            lines.add("Error: " + e.getMessage());
        }
        return lines;
    }
}
