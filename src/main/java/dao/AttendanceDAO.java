package dao;

import model.Attendance;
import model.AttendanceSummary;
import model.Medical;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// -------------------------------------------------------
// AttendanceDAO – all database operations for attendance
// OOP: Database Handling, Encapsulation
// -------------------------------------------------------
public class AttendanceDAO {

    // ── ADD / UPDATE a single session ──────────────────────
    public void saveAttendance(String studentId, String courseCode,
                               int sessionNo, String sessionType,
                               boolean present, LocalDate date) throws SQLException {
        String sql = "INSERT INTO attendance " +
                     "(student_id, course_code, session_no, session_type, is_present, session_date) " +
                     "VALUES (?,?,?,?,?,?) " +
                     "ON DUPLICATE KEY UPDATE is_present=?, session_date=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseCode);
            ps.setInt(3, sessionNo);
            ps.setString(4, sessionType);
            ps.setInt(5, present ? 1 : 0);
            ps.setDate(6, Date.valueOf(date));
            ps.setInt(7, present ? 1 : 0);
            ps.setDate(8, Date.valueOf(date));
            ps.executeUpdate();
        }
    }

    // ── GET all attendance for one student in one course ───
    public List<Attendance> getByStudentAndCourse(String studentId,
                                                   String courseCode,
                                                   String type) throws SQLException {
        // type can be "THEORY", "PRACTICAL", or "ALL"
        String sql = "SELECT * FROM attendance WHERE student_id=? AND course_code=?"
                   + (type.equals("ALL") ? "" : " AND session_type=?")
                   + " ORDER BY session_type, session_no";

        List<Attendance> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setString(2, courseCode);
            if (!type.equals("ALL")) ps.setString(3, type);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Attendance(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getString("course_code"),
                    rs.getInt("session_no"),
                    rs.getString("session_type"),
                    rs.getInt("is_present") == 1,
                    rs.getDate("session_date").toLocalDate()
                ));
            }
        }
        return list;
    }

    // ── SUMMARY for one student + one course ───────────────
    public AttendanceSummary getSummary(String studentId, String courseCode,
                                        String type) throws SQLException {
        // Count present sessions
        String countSql = "SELECT COUNT(*) FROM attendance " +
                          "WHERE student_id=? AND course_code=? AND is_present=1"
                        + (type.equals("ALL") ? "" : " AND session_type=?");

        String totalSql = "SELECT COUNT(*) FROM attendance " +
                          "WHERE student_id=? AND course_code=?"
                        + (type.equals("ALL") ? "" : " AND session_type=?");

        String nameSql  = "SELECT full_name FROM users WHERE username=?";

        int present = 0, total = 0;
        String name = studentId;

        try (Connection con = DBConnection.getConnection()) {
            // Get student name
            try (PreparedStatement ps = con.prepareStatement(nameSql)) {
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) name = rs.getString(1);
            }

            // Total sessions
            try (PreparedStatement ps = con.prepareStatement(totalSql)) {
                ps.setString(1, studentId);
                ps.setString(2, courseCode);
                if (!type.equals("ALL")) ps.setString(3, type);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) total = rs.getInt(1);
            }

            // Present sessions
            try (PreparedStatement ps = con.prepareStatement(countSql)) {
                ps.setString(1, studentId);
                ps.setString(2, courseCode);
                if (!type.equals("ALL")) ps.setString(3, type);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) present = rs.getInt(1);
            }
        }

        // Count approved medical days that overlap absent sessions
        int medDays = countApprovedMedicalSessions(studentId, courseCode, type);

        return new AttendanceSummary(studentId, name, courseCode,
                                     total, present, medDays);
    }

    // Count how many absent sessions are covered by approved medicals
    private int countApprovedMedicalSessions(String studentId,
                                              String courseCode,
                                              String type) throws SQLException {
        String sql = "SELECT a.session_date FROM attendance a " +
                     "WHERE a.student_id=? AND a.course_code=? AND a.is_present=0"
                   + (type.equals("ALL") ? "" : " AND a.session_type=?");

        String medSql = "SELECT from_date, to_date FROM medicals " +
                        "WHERE student_id=? AND is_approved=1";

        int covered = 0;
        try (Connection con = DBConnection.getConnection()) {
            // Get absent dates
            List<LocalDate> absentDates = new ArrayList<>();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, studentId);
                ps.setString(2, courseCode);
                if (!type.equals("ALL")) ps.setString(3, type);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) absentDates.add(rs.getDate(1).toLocalDate());
            }

            // Get approved medical ranges
            try (PreparedStatement ps = con.prepareStatement(medSql)) {
                ps.setString(1, studentId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    LocalDate from = rs.getDate("from_date").toLocalDate();
                    LocalDate to   = rs.getDate("to_date").toLocalDate();
                    for (LocalDate d : absentDates) {
                        if (!d.isBefore(from) && !d.isAfter(to)) covered++;
                    }
                }
            }
        }
        return covered;
    }

    // ── BATCH SUMMARY for a whole course ──────────────────
    public List<AttendanceSummary> getBatchSummary(String courseCode,
                                                    String type) throws SQLException {
        String studentSql = "SELECT DISTINCT username FROM users WHERE role='STUDENT'";
        List<AttendanceSummary> list = new ArrayList<>();
        List<String> studentIds = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(studentSql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                studentIds.add(rs.getString(1));
            }
        }

        for (String sid : studentIds) {
            list.add(getSummary(sid, courseCode, type));
        }
        return list;
    }

    // ── MEDICAL CRUD ───────────────────────────────────────
    public void saveMedical(String studentId, LocalDate from,
                            LocalDate to, String reason) throws SQLException {
        String sql = "INSERT INTO medicals (student_id, from_date, to_date, reason) " +
                     "VALUES (?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            ps.setString(4, reason);
            ps.executeUpdate();
        }
    }

    public void approveMedical(int medicalId, boolean approve) throws SQLException {
        String sql = "UPDATE medicals SET is_approved=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, approve ? 1 : 0);
            ps.setInt(2, medicalId);
            ps.executeUpdate();
        }
    }

    public List<Medical> getAllMedicals() throws SQLException {
        String sql = "SELECT * FROM medicals ORDER BY submitted_at DESC";
        List<Medical> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Medical(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getDate("from_date").toLocalDate(),
                    rs.getDate("to_date").toLocalDate(),
                    rs.getString("reason"),
                    rs.getInt("is_approved") == 1
                ));
            }
        }
        return list;
    }

    public List<Medical> getMedicalsByStudent(String studentId) throws SQLException {
        String sql = "SELECT * FROM medicals WHERE student_id=? ORDER BY from_date DESC";
        List<Medical> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Medical(
                    rs.getInt("id"),
                    rs.getString("student_id"),
                    rs.getDate("from_date").toLocalDate(),
                    rs.getDate("to_date").toLocalDate(),
                    rs.getString("reason"),
                    rs.getInt("is_approved") == 1
                ));
            }
        }
        return list;
    }
}
