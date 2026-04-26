package dao;

import model.Course;
import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getAll() throws SQLException {
        List<Course> list = new ArrayList<>();
        String sql = "SELECT c.id, c.course_code, c.course_name, " +
                     "c.credits, c.lecturer_id, u.full_name " +
                     "FROM courses c " +
                     "LEFT JOIN users u ON c.lecturer_id = u.id " +
                     "ORDER BY c.course_code";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String lname = rs.getString("full_name");
                list.add(new Course(
                    rs.getInt("id"),
                    rs.getString("course_code"),
                    rs.getString("course_name"),
                    rs.getInt("credits"),
                    rs.getInt("lecturer_id"),
                    lname != null ? lname : "Not Assigned"
                ));
            }
        }
        return list;
    }

    public void add(String code, String name, int credits,
                    int lecturerId) throws SQLException {
        String sql = "INSERT INTO courses " +
                     "(course_code,course_name,credits,lecturer_id) " +
                     "VALUES (?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, name);
            ps.setInt(3, credits);
            if (lecturerId == 0) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, lecturerId);
            ps.executeUpdate();
        }
    }

    public void update(int id, String code, String name, int credits,
                       int lecturerId) throws SQLException {
        String sql = "UPDATE courses SET course_code=?,course_name=?," +
                     "credits=?,lecturer_id=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code);
            ps.setString(2, name);
            ps.setInt(3, credits);
            if (lecturerId == 0) ps.setNull(4, Types.INTEGER);
            else ps.setInt(4, lecturerId);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM courses WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // For the lecturer dropdown
    public List<User> getLecturers() throws SQLException {
        return new UserDAO().getByRole("LECTURER");
    }
}
