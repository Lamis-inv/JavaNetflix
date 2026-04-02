package org.JavaProject.JavaNetflixProject.DAO;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.JavaProject.JavaNetflixProject.Entities.Comment;
import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

public class CommentDAO {

    public List<Comment> findByContentId(int contentId) throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.name as user_name FROM comments c JOIN users u ON c.user_id=u.id WHERE c.content_id=? ORDER BY c.created_at DESC";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapComment(rs));
        }
        return list;
    }

    public List<Comment> findAll() throws SQLException {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT c.*, u.name as user_name FROM comments c JOIN users u ON c.user_id=u.id ORDER BY c.created_at DESC";
        try (Connection conn = ConxDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapComment(rs));
        }
        return list;
    }

    public void save(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (user_id, content_id, body) VALUES (?,?,?)";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, comment.getUserId()); ps.setInt(2, comment.getContentId());
            ps.setString(3, comment.getBody()); ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM comments WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    public void flag(int id) throws SQLException {
        String sql = "UPDATE comments SET flagged=1 WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    private Comment mapComment(ResultSet rs) throws SQLException {
        Comment c = new Comment();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setUserName(rs.getString("user_name"));
        c.setContentId(rs.getInt("content_id"));
        c.setBody(rs.getString("body"));
        c.setFlagged(rs.getInt("flagged") == 1);
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());
        return c;
    }
}
