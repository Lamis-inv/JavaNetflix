package org.JavaProject.JavaNetflixProject.Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConxDB {

  
    private static ConxDB instance;
    private Connection connexion;

   
    private final String DB_URL = "jdbc:mysql://localhost:3306/database"; 
    private final String USER = "root";
    private final String PASS = "";

   
    private ConxDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connexion = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connexion réussie !");
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Erreur de connexion : " + e.getMessage());
        }
    }

    public static ConxDB getInstance() {
        if (instance == null) {
            instance = new ConxDB();
        } else {
            try {
                if (instance.getCnx().isClosed()) {
                    instance = new ConxDB();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    
    public Connection getCnx() {
        return connexion;
    }
}