package org.JavaProject.JavaNetflixProject.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConxDB {

    private static Connection connection;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/jstream_db?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    // Private constructor for singleton
    private ConxDB() {
    }
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL Driver not found", e);
            }
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
        }
        return connection;
    }

    // Thread-safe singleton getter
    public static synchronized Connection getInstance() {
        if (connection == null) {
            new ConxDB();
        }
        return connection;
    }
}
