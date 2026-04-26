package dao;

import java.sql.*;

public class StudentProfileDAO {

    private static volatile boolean tableReady = false;

    public StudentProfileDAO() {
        ensureTable();
    }

    private void ensureTable() {
        if (tableReady) return;
        String sql = "CREATE TABLE IF NOT EXISTS student_profiles (" +
                "user_id INT PRIMARY KEY," +
                "profile_picture VARCHAR(500)," +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP," +
                "CONSTRAINT fk_student_profile_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement()) {
            st.execute(sql);
            tableReady = true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize student_profiles table: " + e.getMessage(), e);
        }
    }

    public String getProfilePicture(int userId) throws SQLException {
        String sql = "SELECT profile_picture FROM student_profiles WHERE user_id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString(1);
        }
        return null;
    }

    public void saveProfilePicture(int userId, String picturePath) throws SQLException {
        String sql = "INSERT INTO student_profiles (user_id,profile_picture) VALUES (?,?) " +
                     "ON DUPLICATE KEY UPDATE profile_picture=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, picturePath);
            ps.setString(3, picturePath);
            ps.executeUpdate();
        }
    }
}
