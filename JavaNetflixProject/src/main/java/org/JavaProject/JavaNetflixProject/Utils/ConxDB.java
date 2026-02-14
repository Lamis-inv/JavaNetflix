package org.JavaProject.JavaNetflixProject.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConxDB {

    private static Connection connexion;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/database?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    // Private constructor for singleton
    private ConxDB() {
        try {
            // Optional, modern JDBC auto-loads the driver
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            connexion = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connected to database successfully!");
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    // Thread-safe singleton getter
    public static synchronized Connection getInstance() {
        if (connexion == null) {
            new ConxDB();
        }
        return connexion;
    }
}
