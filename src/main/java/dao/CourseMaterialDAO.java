package dao;

import model.CourseMaterial;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseMaterialDAO {

    private static volatile boolean tableReady = false;

    public CourseMaterialDAO() {
        ensureTable();
    }

    private void ensureTable() {
        if (tableReady) return;
        String sql = "CREATE TABLE IF NOT EXISTS course_materials (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "course_code VARCHAR(30) NOT NULL," +
                "title VARCHAR(120) NOT NULL," +
                "material_type VARCHAR(30) NOT NULL," +
                "material_link VARCHAR(500)," +
                "description VARCHAR(500)," +
                "uploaded_by INT NOT NULL," +
                "uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "INDEX idx_material_course (course_code)," +
                "INDEX idx_material_uploader (uploaded_by)," +
                "CONSTRAINT fk_material_uploader FOREIGN KEY (uploaded_by) REFERENCES users(id) ON DELETE CASCADE" +
                ")";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
            tableReady = true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize course_materials table: " + e.getMessage(), e);
        }
    }

    public List<CourseMaterial> getAll() throws SQLException {
        String sql = "SELECT m.id,m.course_code,m.title,m.material_type,m.material_link,m.description," +
                     "m.uploaded_by,m.uploaded_at,u.full_name " +
                     "FROM course_materials m JOIN users u ON u.id=m.uploaded_by " +
                     "ORDER BY m.course_code,m.uploaded_at DESC";

        List<CourseMaterial> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<CourseMaterial> getByLecturer(int lecturerId) throws SQLException {
        String sql = "SELECT m.id,m.course_code,m.title,m.material_type,m.material_link,m.description," +
                     "m.uploaded_by,m.uploaded_at,u.full_name " +
                     "FROM course_materials m JOIN users u ON u.id=m.uploaded_by " +
                     "WHERE m.uploaded_by=? ORDER BY m.uploaded_at DESC";

        List<CourseMaterial> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lecturerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<CourseMaterial> getByCourseCodes(List<String> courseCodes) throws SQLException {
        if (courseCodes == null || courseCodes.isEmpty()) {
            return Collections.emptyList();
        }

        String placeholders = String.join(",", Collections.nCopies(courseCodes.size(), "?"));
        String sql = "SELECT m.id,m.course_code,m.title,m.material_type,m.material_link,m.description," +
                     "m.uploaded_by,m.uploaded_at,u.full_name " +
                     "FROM course_materials m JOIN users u ON u.id=m.uploaded_by " +
                     "WHERE m.course_code IN (" + placeholders + ") " +
                     "ORDER BY m.course_code,m.uploaded_at DESC";

        List<CourseMaterial> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            for (int i = 0; i < courseCodes.size(); i++) {
                ps.setString(i + 1, courseCodes.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void add(String courseCode, String title, String type,
                    String link, String description, int uploadedBy)
            throws SQLException {
        String sql = "INSERT INTO course_materials " +
                     "(course_code,title,material_type,material_link,description,uploaded_by) " +
                     "VALUES (?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ps.setString(2, title);
            ps.setString(3, type);
            ps.setString(4, link);
            ps.setString(5, description);
            ps.setInt(6, uploadedBy);
            ps.executeUpdate();
        }
    }

    public boolean updateForLecturer(int id, int lecturerId,
                                     String courseCode, String title,
                                     String type, String link,
                                     String description) throws SQLException {
        String sql = "UPDATE course_materials SET course_code=?,title=?,material_type=?," +
                     "material_link=?,description=? WHERE id=? AND uploaded_by=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ps.setString(2, title);
            ps.setString(3, type);
            ps.setString(4, link);
            ps.setString(5, description);
            ps.setInt(6, id);
            ps.setInt(7, lecturerId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteForLecturer(int id, int lecturerId) throws SQLException {
        String sql = "DELETE FROM course_materials WHERE id=? AND uploaded_by=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, lecturerId);
            return ps.executeUpdate() > 0;
        }
    }

    private CourseMaterial mapRow(ResultSet rs) throws SQLException {
        return new CourseMaterial(
                rs.getInt("id"),
                rs.getString("course_code"),
                rs.getString("title"),
                rs.getString("material_type"),
                rs.getString("material_link"),
                rs.getString("description"),
                rs.getInt("uploaded_by"),
                rs.getString("full_name"),
                rs.getString("uploaded_at")
        );
    }
}
