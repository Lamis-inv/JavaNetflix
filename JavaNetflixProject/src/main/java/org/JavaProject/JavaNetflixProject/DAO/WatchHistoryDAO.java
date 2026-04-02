package org.JavaProject.JavaNetflixProject.DAO;


import java.sql.*;

import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

public class WatchHistoryDAO {

    public void saveProgress(int userId, Integer contentId, Integer episodeId, int progressSec, boolean completed) throws SQLException {
        String checkSql = episodeId != null
                ? "SELECT id FROM watch_history WHERE user_id=? AND episode_id=?"
                : "SELECT id FROM watch_history WHERE user_id=? AND content_id=? AND episode_id IS NULL";

        try (Connection conn = ConxDB.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, userId);
            check.setInt(2, episodeId != null ? episodeId : (contentId != null ? contentId : 0));
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                int histId = rs.getInt("id");
                String upd = "UPDATE watch_history SET progress_sec=?, completed=? WHERE id=?";
                try (PreparedStatement ups = conn.prepareStatement(upd)) {
                    ups.setInt(1, progressSec);
                    ups.setInt(2, completed ? 1 : 0);
                    ups.setInt(3, histId);
                    ups.executeUpdate();
                }
            } else {
                String ins = "INSERT INTO watch_history (user_id, content_id, episode_id, progress_sec, completed) VALUES (?,?,?,?,?)";
                try (PreparedStatement ins2 = conn.prepareStatement(ins)) {
                    ins2.setInt(1, userId);
                    if (contentId != null) ins2.setInt(2, contentId); else ins2.setNull(2, Types.INTEGER);
                    if (episodeId != null) ins2.setInt(3, episodeId); else ins2.setNull(3, Types.INTEGER);
                    ins2.setInt(4, progressSec);
                    ins2.setInt(5, completed ? 1 : 0);
                    ins2.executeUpdate();
                }
            }
        }
    }

    public int getProgressSec(int userId, int episodeId) throws SQLException {
        String sql = "SELECT progress_sec FROM watch_history WHERE user_id=? AND episode_id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, episodeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("progress_sec");
        }
        return 0;
    }

    public boolean isCompleted(int userId, int episodeId) throws SQLException {
        String sql = "SELECT completed FROM watch_history WHERE user_id=? AND episode_id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, episodeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("completed") == 1;
        }
        return false;
    }

    public Integer findFirstUnwatchedEpisodeId(int userId, int serieId) throws SQLException {
        String sql = "SELECT e.id FROM episodes e JOIN seasons s ON e.season_id=s.id " +
                "WHERE s.serie_id=? AND e.id NOT IN (" +
                "  SELECT episode_id FROM watch_history WHERE user_id=? AND episode_id IS NOT NULL AND completed=1" +
                ") ORDER BY s.number, e.episode_num LIMIT 1";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serieId); ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        return null;
    }
}
