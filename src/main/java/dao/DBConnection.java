package dao;

import java.sql.*;


public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3309/faculty_db";
    private static final String USER = "root";
    private static final String PASS = "1234";

    private DBConnection() {
        // Utility class
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
