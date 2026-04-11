package org.JavaProject.JavaNetflixProject.DAO;

import org.JavaProject.JavaNetflixProject.Entities.Content;
import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Existing methods kept unchanged.
 * Added: getHistory(userId) – returns Content items the user has watched,
 * most recent first. Uses a JOIN so no schema change is needed.
 */
public class WatchHistoryDAO {

    public void saveProgress(int userId, int contentId, Integer episodeId,
                             int progressSec, boolean completed) throws SQLException {
        String sql = episodeId != null
            ? "INSERT INTO watch_history (user_id, content_id, episode_id, progress_sec, completed, watched_at) " +
              "VALUES (?,?,?,?,?, NOW()) ON DUPLICATE KEY UPDATE progress_sec=?, completed=?, watched_at=NOW()"
            : "INSERT INTO watch_history (user_id, content_id, progress_sec, completed, watched_at) " +
              "VALUES (?,?,?,?, NOW()) ON DUPLICATE KEY UPDATE progress_sec=?, completed=?, watched_at=NOW()";

        try (PreparedStatement ps = ConxDB.getConnection().prepareStatement(sql)) {
            if (episodeId != null) {
                ps.setInt(1, userId); ps.setInt(2, contentId); ps.setInt(3, episodeId);
                ps.setInt(4, progressSec); ps.setBoolean(5, completed);
                ps.setInt(6, progressSec); ps.setBoolean(7, completed);
            } else {
                ps.setInt(1, userId); ps.setInt(2, contentId);
                ps.setInt(3, progressSec); ps.setBoolean(4, completed);
                ps.setInt(5, progressSec); ps.setBoolean(6, completed);
            }
            ps.executeUpdate();
        }
    }

    public boolean isCompleted(int userId, int episodeId) throws SQLException {
        String sql = "SELECT completed FROM watch_history WHERE user_id=? AND episode_id=?";
        try (PreparedStatement ps = ConxDB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, episodeId);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getBoolean("completed");
        }
    }

    public int getProgressSec(int userId, int episodeId) throws SQLException {
        String sql = "SELECT progress_sec FROM watch_history WHERE user_id=? AND episode_id=?";
        try (PreparedStatement ps = ConxDB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, episodeId);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("progress_sec") : 0;
        }
    }

    /**
     * Returns the list of Content items watched by this user, most recent first.
     * Joins watch_history → content so no new table is needed.
     */
    public List<Content> getHistory(int userId) throws SQLException {
        String sql =
            "SELECT DISTINCT c.id, c.title, c.cover_url, c.release_year, c.duration_min, " +
            "       c.synopsis, c.casting, c.video_url, c.trailer_url, c.type, c.avg_rating, " +
            "       c.view_count, c.category_id, MAX(wh.watched_at) AS last_watched " +
            "FROM watch_history wh " +
            "JOIN content c ON c.id = wh.content_id " +
            "WHERE wh.user_id = ? " +
            "GROUP BY c.id " +
            "ORDER BY last_watched DESC";

        List<Content> list = new ArrayList<>();
        try (PreparedStatement ps = ConxDB.getConnection().prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Content c = new Content();
                c.setId(rs.getInt("id"));
                c.setTitle(rs.getString("title"));
                c.setCoverUrl(rs.getString("cover_url"));
                c.setReleaseYear(rs.getInt("release_year"));
                c.setDurationMin(rs.getInt("duration_min"));
                c.setSynopsis(rs.getString("synopsis"));
                c.setCasting(rs.getString("casting"));
                c.setVideoUrl(rs.getString("video_url"));
                c.setTrailerUrl(rs.getString("trailer_url"));
                c.setAvgRating(rs.getDouble("avg_rating"));
                c.setViewCount(rs.getInt("view_count"));
                String type = rs.getString("type");
                c.setType(Content.Type.valueOf(type.toUpperCase()));
                list.add(c);
            }
        }
        return list;
    }
}