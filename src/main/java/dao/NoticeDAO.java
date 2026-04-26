package dao;

import model.Notice;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoticeDAO {

    public List<Notice> getAll() throws SQLException {
        List<Notice> list = new ArrayList<>();
        String sql = "SELECT n.id, n.title, n.content, " +
                     "u.full_name, n.created_at " +
                     "FROM notices n " +
                     "JOIN users u ON n.created_by = u.id " +
                     "ORDER BY n.created_at DESC";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Notice(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("content"),
                    rs.getString("full_name"),
                    rs.getString("created_at")
                ));
            }
        }
        return list;
    }

    public void add(String title, String content, int adminId)
                    throws SQLException {
        String sql = "INSERT INTO notices (title,content,created_by) " +
                     "VALUES (?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, adminId);
            ps.executeUpdate();
        }
    }

    public void update(int id, String title, String content)
                       throws SQLException {
        String sql = "UPDATE notices SET title=?,content=? WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, content);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM notices WHERE id=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
