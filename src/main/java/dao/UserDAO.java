package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // ── LOGIN ────────────────────────────────────────────────
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return makeUser(rs);
        }
        return null;
    }

    // ── GET ALL USERS ────────────────────────────────────────
    public List<User> getAllUsers() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY role, full_name";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) list.add(makeUser(rs));
        }
        return list;
    }

    // ── GET USERS BY ROLE (used by other members) ────────────
    public List<User> getByRole(String role) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role=? ORDER BY full_name";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(makeUser(rs));
        }
        return list;
    }

    // ── GET USER BY USERNAME ───────────────────────────────
    public User getByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return makeUser(rs);
        }
        return null;
    }

    // ── PROFILE UPDATE (LECTURER / TECH) ───────────────────
    public boolean updateStaffProfile(int id, String fullName,
                                      String email, String phone,
                                      String dept) throws SQLException {
        String sql = "UPDATE users SET full_name=?,email=?,phone=?,dept=? " +
                     "WHERE id=? AND role IN ('LECTURER','TECH')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, fullName);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setString(4, dept);
            ps.setInt(5, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── PROFILE UPDATE (STUDENT CONTACT ONLY) ──────────────
    public boolean updateStudentContact(int id, String email,
                                        String phone) throws SQLException {
        String sql = "UPDATE users SET email=?,phone=? " +
                     "WHERE id=? AND role='STUDENT'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, phone);
            ps.setInt(3, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── ADD USER ─────────────────────────────────────────────
    public void addUser(String username, String password, String fullName,
                        String email, String phone, String role,
                        String dept) throws SQLException {

        String sql = "INSERT INTO users " +
                     "(username,password,full_name,email,phone,role,dept) " +
                     "VALUES (?,?,?,?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, role);
            ps.setString(7, dept);
            ps.executeUpdate();
        }
    }

    // ── UPDATE USER ──────────────────────────────────────────
    public boolean updateUser(int id, String username, String password,
                              String fullName, String email, String phone,
                              String role, String dept) throws SQLException {

        // Keep password unchanged when the edit form leaves it blank.
        boolean updatePassword = password != null && !password.isBlank();
        String sql = updatePassword
            ? "UPDATE users SET username=?,password=?,full_name=?,email=?,phone=?,role=?,dept=? WHERE id=?"
            : "UPDATE users SET username=?,full_name=?,email=?,phone=?,role=?,dept=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, username);
            if (updatePassword) ps.setString(i++, password);
            ps.setString(i++, fullName);
            ps.setString(i++, email);
            ps.setString(i++, phone);
            ps.setString(i++, role);
            ps.setString(i++, dept);
            ps.setInt(i, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── MAIN ADMIN CHECK ─────────────────────────────────────
    public boolean isPrimaryAdmin(int id) throws SQLException {
        String sql = "SELECT MIN(id) FROM users WHERE role='ADMIN'";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int primaryAdminId = rs.getInt(1);
                if (rs.wasNull()) return false;
                return primaryAdminId == id;
            }
        }
        return false;
    }

    // ── DELETE USER ──────────────────────────────────────────
    public boolean deleteUser(int id) throws SQLException {
        if (isPrimaryAdmin(id)) return false;

        String sql = "DELETE FROM users WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    // ── USERNAME EXISTS CHECK ────────────────────────────────
    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT id FROM users WHERE username=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeQuery().next();
        }
    }

    public boolean usernameExistsForOtherUser(String username, int currentId)
            throws SQLException {
        String sql = "SELECT id FROM users WHERE username=? AND id<>?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setInt(2, currentId);
            return ps.executeQuery().next();
        }
    }

    // ── BUILD CORRECT SUBCLASS FROM ROW ─────────────────────
    private User makeUser(ResultSet rs) throws SQLException {
        int    id       = rs.getInt("id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String fullName = rs.getString("full_name");
        String email    = rs.getString("email");
        String phone    = rs.getString("phone");
        String role     = rs.getString("role");
        String dept     = rs.getString("dept");

        return switch (role) {
            case "ADMIN"    -> new Admin(id, username, password, fullName, email, phone);
            case "LECTURER" -> new Lecturer(id, username, password, fullName, email, phone, dept);
            case "STUDENT"  -> new Student(id, username, password, fullName, email, phone, dept);
            case "TECH"     -> new TechnicalOfficer(id, username, password, fullName, email, phone, dept);
            default         -> throw new SQLException("Unknown role: " + role);
        };
    }
}
