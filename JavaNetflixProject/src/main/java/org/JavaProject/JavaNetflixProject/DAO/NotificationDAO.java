package org.JavaProject.JavaNetflixProject.DAO;

import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads from the 'content' table to build "new content" notifications.
 * No schema change required: we simply query content added in the last 14 days.
 *
 * To mark notifications as "seen" we use a simple in-memory set per session.
 * If you later want persistence, add a user_notifications table.
 */
public class NotificationDAO {

    /** Notifications already dismissed this session (content IDs) */
    private static final java.util.Set<Integer> seenIds =
        new java.util.concurrent.CopyOnWriteArraySet<>();

    public static class Notification {
        public final int     contentId;
        public final String  title;
        public final String  coverUrl;
        public final boolean isFilm;
        public final LocalDateTime addedAt;

        public Notification(int id, String title, String coverUrl,
                            boolean isFilm, LocalDateTime addedAt) {
            this.contentId = id;
            this.title     = title;
            this.coverUrl  = coverUrl;
            this.isFilm    = isFilm;
            this.addedAt   = addedAt;
        }

        public boolean isSeen() { return seenIds.contains(contentId); }
    }

    /** Returns content added in the last 14 days, newest first */
    public List<Notification> getRecent() throws SQLException {
        // created_at column used if present; fall back to id DESC as a proxy
        String sql =
            "SELECT id, title, cover_url, type, " +
            "  COALESCE(created_at, NOW()) AS added_at " +
            "FROM content " +
            "WHERE created_at >= DATE_SUB(NOW(), INTERVAL 14 DAY) " +
            "ORDER BY created_at DESC " +
            "LIMIT 30";

        List<Notification> list = new ArrayList<>();
        try (Statement st = ConxDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Notification(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("cover_url"),
                    "film".equalsIgnoreCase(rs.getString("type")),
                    rs.getTimestamp("added_at").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            // If created_at column doesn't exist, fall back to last 10 by id
            if (e.getErrorCode() == 1054 || e.getSQLState().equals("42S22")) {
                return getFallback();
            }
            throw e;
        }
        return list;
    }

    private List<Notification> getFallback() throws SQLException {
        String sql = "SELECT id, title, cover_url, type FROM content ORDER BY id DESC LIMIT 10";
        List<Notification> list = new ArrayList<>();
        try (Statement st = ConxDB.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Notification(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("cover_url"),
                    "film".equalsIgnoreCase(rs.getString("type")),
                    LocalDateTime.now()
                ));
            }
        }
        return list;
    }

    public int countUnseen() {
        try {
            return (int) getRecent().stream().filter(n -> !n.isSeen()).count();
        } catch (Exception e) { return 0; }
    }

    public void markAllSeen() {
        try {
            getRecent().forEach(n -> seenIds.add(n.contentId));
        } catch (Exception ignored) {}
    }
}