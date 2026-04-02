package org.JavaProject.JavaNetflixProject.DAO;



import java.sql.*;

import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

public class RatingDAO {

    public int getUserRating(int userId, int contentId) throws SQLException {
        String sql = "SELECT stars FROM ratings WHERE user_id=? AND content_id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, contentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("stars");
        }
        return 0;
    }

    public void upsertRating(int userId, int contentId, int stars) throws SQLException {
        String sql = "INSERT INTO ratings (user_id, content_id, stars) VALUES (?,?,?) ON DUPLICATE KEY UPDATE stars=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, contentId);
            ps.setInt(3, stars); ps.setInt(4, stars);
            ps.executeUpdate();
        }
    }
}
