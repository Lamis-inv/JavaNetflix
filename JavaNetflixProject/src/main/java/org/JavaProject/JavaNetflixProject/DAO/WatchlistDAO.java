package org.JavaProject.JavaNetflixProject.DAO;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

public class WatchlistDAO {

    public boolean isInWatchlist(int userId, int contentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM watchlist WHERE user_id=? AND content_id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, contentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        }
        return false;
    }

    public void add(int userId, int contentId) throws SQLException {
        String sql = "INSERT IGNORE INTO watchlist (user_id, content_id) VALUES (?,?)";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, contentId); ps.executeUpdate();
        }
    }

    public void remove(int userId, int contentId) throws SQLException {
        String sql = "DELETE FROM watchlist WHERE user_id=? AND content_id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, contentId); ps.executeUpdate();
        }
    }

    public List<Integer> getWatchlistContentIds(int userId) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT content_id FROM watchlist WHERE user_id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) ids.add(rs.getInt("content_id"));
        }
        return ids;
    }
}
