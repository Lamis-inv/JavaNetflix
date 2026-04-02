package org.JavaProject.JavaNetflixProject.DAO;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.JavaProject.JavaNetflixProject.Entities.Category;
import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

public class CategoryDAO {

    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY name";
        try (Connection conn = ConxDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(new Category(rs.getInt("id"), rs.getString("name")));
        }
        return list;
    }

    public Category save(Category c) throws SQLException {
        String sql = "INSERT INTO categories (name) VALUES (?)";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getName()); ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) c.setId(keys.getInt(1));
        }
        return c;
    }

    public void update(Category c) throws SQLException {
        String sql = "UPDATE categories SET name=? WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getName()); ps.setInt(2, c.getId()); ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM categories WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }
}
