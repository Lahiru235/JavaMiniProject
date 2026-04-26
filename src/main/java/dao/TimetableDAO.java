package dao;

import model.Timetable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimetableDAO {

    private static volatile boolean tableReady = false;

    public TimetableDAO() {
        ensureTable();
    }

    private void ensureTable() {
        if (tableReady) return;
        String sql = "CREATE TABLE IF NOT EXISTS timetables (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "dept VARCHAR(40) NOT NULL," +
                "course_code VARCHAR(30) NOT NULL," +
                "day_of_week VARCHAR(15) NOT NULL," +
                "start_time VARCHAR(8) NOT NULL," +
                "end_time VARCHAR(8) NOT NULL," +
                "venue VARCHAR(80) NOT NULL," +
                "note VARCHAR(255)," +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
            tableReady = true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize timetables table: " + e.getMessage(), e);
        }
    }

    public List<Timetable> getAll() throws SQLException {
        String sql = "SELECT * FROM timetables ORDER BY dept, day_of_week, start_time, course_code";
        List<Timetable> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Timetable> getByDept(String dept) throws SQLException {
        String sql = "SELECT * FROM timetables WHERE dept=? ORDER BY day_of_week, start_time, course_code";
        List<Timetable> list = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dept);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void add(String dept, String courseCode, String dayOfWeek,
                    String startTime, String endTime, String venue,
                    String note) throws SQLException {
        String sql = "INSERT INTO timetables (dept,course_code,day_of_week,start_time,end_time,venue,note) " +
                     "VALUES (?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dept);
            ps.setString(2, courseCode);
            ps.setString(3, dayOfWeek);
            ps.setString(4, startTime);
            ps.setString(5, endTime);
            ps.setString(6, venue);
            ps.setString(7, note);
            ps.executeUpdate();
        }
    }

    public void update(int id, String dept, String courseCode,
                       String dayOfWeek, String startTime,
                       String endTime, String venue, String note)
            throws SQLException {
        String sql = "UPDATE timetables SET dept=?,course_code=?,day_of_week=?," +
                     "start_time=?,end_time=?,venue=?,note=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dept);
            ps.setString(2, courseCode);
            ps.setString(3, dayOfWeek);
            ps.setString(4, startTime);
            ps.setString(5, endTime);
            ps.setString(6, venue);
            ps.setString(7, note);
            ps.setInt(8, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM timetables WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Timetable mapRow(ResultSet rs) throws SQLException {
        return new Timetable(
                rs.getInt("id"),
                rs.getString("dept"),
                rs.getString("course_code"),
                rs.getString("day_of_week"),
                rs.getString("start_time"),
                rs.getString("end_time"),
                rs.getString("venue"),
                rs.getString("note")
        );
    }
}
