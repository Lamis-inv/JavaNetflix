package org.JavaProject.JavaNetflixProject.DAO;

import org.JavaProject.JavaNetflixProject.Utils.ConxDB;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.JavaProject.JavaNetflixProject.Entities.Category;
import org.JavaProject.JavaNetflixProject.Entities.Content;

public class ContentDAO {

    public List<Content> findAll() throws SQLException {
        return query("SELECT c.*, cat.name as cat_name FROM content c LEFT JOIN categories cat ON c.category_id=cat.id ORDER BY c.title");
    }

    public List<Content> findFeatured() throws SQLException {
        return query("SELECT c.*, cat.name as cat_name FROM content c LEFT JOIN categories cat ON c.category_id=cat.id WHERE c.is_featured=1");
    }
 // Returns a list of all categories from the database
    public List<Category> findAllCategories() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT id, name FROM categories ORDER BY name";
        try (Connection conn = ConxDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Category(rs.getInt("id"), rs.getString("name")));
            }
        }
        return list;
    }

    public List<Content> findByCategory(int categoryId) throws SQLException {
        String sql = "SELECT c.*, cat.name as cat_name FROM content c LEFT JOIN categories cat ON c.category_id=cat.id WHERE c.category_id=?";
        return queryWithParam(sql, categoryId);
    }

    public List<Content> findByType(Content.Type type) throws SQLException {
        String sql = "SELECT c.*, cat.name as cat_name FROM content c LEFT JOIN categories cat ON c.category_id=cat.id WHERE c.type=?";
        List<Content> list = new ArrayList<>();
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapContent(rs));
        }
        return list;
    }

    public List<Content> search(String keyword) throws SQLException {
        String sql = "SELECT c.*, cat.name as cat_name FROM content c LEFT JOIN categories cat ON c.category_id=cat.id " +
                     "WHERE c.title LIKE ? OR c.casting LIKE ? OR CAST(c.release_year AS CHAR) LIKE ? OR cat.name LIKE ?";
        String kw = "%" + keyword + "%";
        List<Content> list = new ArrayList<>();
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw); ps.setString(4, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapContent(rs));
        }
        return list;
    }

    public Content findById(int id) throws SQLException {
        String sql = "SELECT c.*, cat.name as cat_name FROM content c LEFT JOIN categories cat ON c.category_id=cat.id WHERE c.id=?";
        List<Content> list = queryWithParam(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Content save(Content content) throws SQLException {
        String sql = "INSERT INTO content (title, type, synopsis, release_year, cover_url, trailer_url, video_url, duration_min, casting, category_id, is_featured) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setContentParams(ps, content);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) content.setId(keys.getInt(1));
        }
        return content;
    }

    public void update(Content content) throws SQLException {
        String sql = "UPDATE content SET title=?, type=?, synopsis=?, release_year=?, cover_url=?, trailer_url=?, video_url=?, duration_min=?, casting=?, category_id=?, is_featured=? WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setContentParams(ps, content);
            ps.setInt(12, content.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM content WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void incrementViewCount(int contentId) throws SQLException {
        String sql = "UPDATE content SET view_count = view_count + 1 WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contentId);
            ps.executeUpdate();
        }
    }

    public void updateAvgRating(int contentId) throws SQLException {
        String sql = "UPDATE content SET avg_rating=(SELECT IFNULL(AVG(stars),0) FROM ratings WHERE content_id=?) WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contentId); ps.setInt(2, contentId);
            ps.executeUpdate();
        }
    }

    // Analytics: content distribution by category
    public List<Object[]> getContentByCategory() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT cat.name, COUNT(c.id) as cnt FROM content c " +
                     "JOIN categories cat ON c.category_id=cat.id GROUP BY cat.name";
        try (Connection conn = ConxDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(new Object[]{rs.getString("name"), rs.getInt("cnt")});
        }
        return list;
    }

    // Analytics: top 5 most viewed
    public List<Content> getTop5MostViewed() throws SQLException {
        return query("SELECT c.*, cat.name as cat_name FROM content c LEFT JOIN categories cat ON c.category_id=cat.id ORDER BY c.view_count DESC LIMIT 5");
    }

    // --- Helpers ---
    private void setContentParams(PreparedStatement ps, Content c) throws SQLException {
        ps.setString(1, c.getTitle());
        ps.setString(2, c.getType().name());
        ps.setString(3, c.getSynopsis());
        ps.setInt(4, c.getReleaseYear());
        ps.setString(5, c.getCoverUrl());
        ps.setString(6, c.getTrailerUrl());
        ps.setString(7, c.getVideoUrl());
        ps.setInt(8, c.getDurationMin());
        ps.setString(9, c.getCasting());
        if (c.getCategory() != null) ps.setInt(10, c.getCategory().getId());
        else ps.setNull(10, Types.INTEGER);
        ps.setInt(11, c.isFeatured() ? 1 : 0);
    }

    private List<Content> query(String sql) throws SQLException {
        List<Content> list = new ArrayList<>();
        try (Connection conn = ConxDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapContent(rs));
        }
        return list;
    }

    private List<Content> queryWithParam(String sql, Object param) throws SQLException {
        List<Content> list = new ArrayList<>();
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (param instanceof Integer) ps.setInt(1, (Integer) param);
            else ps.setString(1, param.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapContent(rs));
        }
        return list;
    }

    private Content mapContent(ResultSet rs) throws SQLException {
        Content c = new Content();
        c.setId(rs.getInt("id"));
        c.setTitle(rs.getString("title"));
        c.setType(Content.Type.valueOf(rs.getString("type")));
        c.setSynopsis(rs.getString("synopsis"));
        c.setReleaseYear(rs.getInt("release_year"));
        c.setCoverUrl(rs.getString("cover_url"));
        c.setTrailerUrl(rs.getString("trailer_url"));
        c.setVideoUrl(rs.getString("video_url"));
        c.setDurationMin(rs.getInt("duration_min"));
        c.setCasting(rs.getString("casting"));
        c.setAvgRating(rs.getDouble("avg_rating"));
        c.setViewCount(rs.getInt("view_count"));
        c.setFeatured(rs.getInt("is_featured") == 1);
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) c.setCreatedAt(ts.toLocalDateTime());
        // Category
        int catId = rs.getInt("category_id");
        if (!rs.wasNull()) {
            Category cat = new Category(catId, rs.getString("cat_name"));
            c.setCategory(cat);
        }
        return c;
    }
    public List<Content> findAllSeries() throws SQLException {
        return query("SELECT c.*, cat.name as cat_name FROM content c " +
                "LEFT JOIN categories cat ON c.category_id=cat.id " +
                "WHERE c.type='SERIE' ORDER BY c.title");
    }
    public List<Object[]> getCategoryStats() throws SQLException {
        List<Object[]> list = new ArrayList<>();

        String sql = 
            "SELECT cat.name, COUNT(c.id)"+
            "FROM content c"+
            "JOIN categories cat ON c.category_id = cat.id"+
           " GROUP BY cat.name";

        try (Connection conn = ConxDB.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Object[]{
                        rs.getString(1),
                        rs.getInt(2)
                });
            }
        }
        return list;
    }
    public int countByType(Content.Type type) throws SQLException {
        String sql = "SELECT COUNT(*) FROM content WHERE type=?";
        try (Connection c = ConxDB.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, type.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
    public int getTotalViews() throws SQLException {
        String sql = "SELECT SUM(view_count) FROM content";
        try (Connection c = ConxDB.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }
    public static int countByCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM films WHERE category_id = ?";

        try (Connection conn = ConxDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoryId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}