package dao;

import model.GradeResult;
import model.GpaSummary;
import model.Marks;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------
// MarksDAO – all DB operations for marks & grading
// OOP: Database Handling
// -------------------------------------------------------
public class MarksDAO {

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private static double[] sanitizeStoredMarksForView(double caMarks, double endExamMarks) {
        return new double[]{
            clamp(caMarks, 0, CaCalculator.CA_MAX),
            clamp(endExamMarks, 0, GradeCalculator.END_EXAM_MAX)
        };
    }

    // ── SAVE / UPDATE marks ────────────────────────────────
    public void saveMarks(String studentId, String courseCode,
                          double quiz1, double quiz2, double quiz3,
                          double midMarks, double endExamMarks) throws SQLException {
        double caMarks = CaCalculator.calculateCaMarks(quiz1, quiz2, quiz3, midMarks);
        GradeCalculator.validateStoredMarks(caMarks, endExamMarks);
        
        String sql = "INSERT INTO marks (student_id, course_code, ca_marks, final_marks) " +
                     "VALUES (?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE ca_marks=?, final_marks=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseCode);
            ps.setDouble(3, caMarks);
            ps.setDouble(4, endExamMarks);
            ps.setDouble(5, caMarks);
            ps.setDouble(6, endExamMarks);
            ps.executeUpdate();
        }
    }

    // ── GET marks for one student ──────────────────────────
    public List<Marks> getMarksByStudent(String studentId) throws SQLException {
        String sql = "SELECT * FROM marks WHERE student_id=?";
        List<Marks> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Marks(rs.getInt("id"),
                                   rs.getString("student_id"),
                                   rs.getString("course_code"),
                                   rs.getDouble("ca_marks"),
                                   rs.getDouble("final_marks")));
            }
        }
        return list;
    }

    // ── GET all grade results for whole batch ──────────────
    public List<GradeResult> getAllGradeResults() throws SQLException {
        String sql =
            "SELECT m.student_id, u.full_name, m.course_code, " +
            "       c.credits, m.ca_marks, m.final_marks " +
            "FROM marks m " +
            "JOIN users u ON u.username = m.student_id " +
            "JOIN courses c ON c.course_code = m.course_code " +
            "ORDER BY m.student_id, m.course_code";

        List<GradeResult> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                double[] sanitized = sanitizeStoredMarksForView(
                    rs.getDouble("ca_marks"),
                    rs.getDouble("final_marks")
                );
                double ca = sanitized[0];
                double endExam = sanitized[1];
                double total = GradeCalculator.calculateTotalMarks(ca, endExam);
                
                // Use GradeCalculator for grade determination
                String grade = GradeCalculator.determineGrade(ca, endExam);
                double gp = GradeCalculator.determineGradePoint(ca, endExam);

                list.add(new GradeResult(
                    rs.getString("student_id"),
                    rs.getString("full_name"),
                    rs.getString("course_code"),
                    rs.getInt("credits"),
                    ca, endExam, total, grade, gp
                ));
            }
        }
        return list;
    }

    // ── GET grade results for ONE student ─────────────────
    public List<GradeResult> getGradeResultsByStudent(String studentId) throws SQLException {
        String sql =
            "SELECT m.student_id, u.full_name, m.course_code, " +
            "       c.credits, m.ca_marks, m.final_marks " +
            "FROM marks m " +
            "JOIN users u ON u.username = m.student_id " +
            "JOIN courses c ON c.course_code = m.course_code " +
            "WHERE m.student_id=? ORDER BY m.course_code";

        List<GradeResult> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                double[] sanitized = sanitizeStoredMarksForView(
                    rs.getDouble("ca_marks"),
                    rs.getDouble("final_marks")
                );
                double ca = sanitized[0];
                double endExam = sanitized[1];
                double total = GradeCalculator.calculateTotalMarks(ca, endExam);
                
                // Use GradeCalculator for grade determination
                String grade = GradeCalculator.determineGrade(ca, endExam);
                double gp = GradeCalculator.determineGradePoint(ca, endExam);
                
                list.add(new GradeResult(
                    rs.getString("student_id"),
                    rs.getString("full_name"),
                    rs.getString("course_code"),
                    rs.getInt("credits"),
                    ca, endExam, total, grade, gp
                ));
            }
        }
        return list;
    }

    // ── SGPA for one student ───────────────────────────────
    public double calcSGPA(String studentId) throws SQLException {
        List<GradeResult> results = getGradeResultsByStudent(studentId);
        return GpaCalculator.calculateSGPA(results);
    }

    // ── CGPA for one student (current dataset scope) ───────
    public double calcCGPA(String studentId) throws SQLException {
        // With current project data model, CGPA is computed across all
        // available marks records for the student.
        List<GradeResult> results = getGradeResultsByStudent(studentId);
        return GpaCalculator.calculateCGPA(results);
    }

    // ── CA marks for one student/course ────────────────────
    public double getCaMarks(String studentId, String courseCode) throws SQLException {
        String sql = "SELECT ca_marks FROM marks WHERE student_id=? AND course_code=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble(1);
        }
        return 0;
    }

    // ── SGPA/CGPA for whole batch ──────────────────────────
    public List<GpaSummary> getBatchGpaSummary() throws SQLException {
        String sql = "SELECT username, full_name FROM users WHERE role='STUDENT' ORDER BY username";
        List<GpaSummary> list = new ArrayList<>();
        List<String[]> students = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                students.add(new String[]{
                    rs.getString("username"),
                    rs.getString("full_name")
                });
            }
        }

        for (String[] student : students) {
            String sid = student[0];
            String name = student[1];
            double sgpa = calcSGPA(sid);
            double cgpa = calcCGPA(sid);
            list.add(new GpaSummary(sid, name, sgpa, cgpa));
        }
        return list;
    }
}