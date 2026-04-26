package model;

import dao.MarksDAO;
import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------
// GradeReport – generates a text grade report for a student
// OOP: INHERITANCE (extends Report)
//      POLYMORPHISM (overrides generate())
// -------------------------------------------------------
public class GradeReport extends Report {

    private String studentId;

    public GradeReport(String studentId) {
        super("Grade Report – " + studentId);
        this.studentId = studentId;
    }

    @Override
    public List<String> generate() {
        List<String> lines = new ArrayList<>();
        try {
            MarksDAO dao = new MarksDAO();
            List<GradeResult> results = dao.getGradeResultsByStudent(studentId);
            lines.add("=== " + title + " ===");
            for (GradeResult r : results) {
                lines.add(r.getCourseCode() + "  CA:" + r.getCaMarks()
                        + "  End:" + r.getFinalMarks()
                        + "  Total:" + r.getTotalMarks()
                        + "  Grade:" + r.getGrade()
                        + "  GP:" + r.getGradePoint());
            }
            lines.add("SGPA: " + dao.calcSGPA(studentId));
        } catch (Exception e) {
            lines.add("Error: " + e.getMessage());
        }
        return lines;
    }
}
