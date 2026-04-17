package org.JavaProject.JavaNetflixProject.Services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.JavaProject.JavaNetflixProject.DAO.ContentDAO;
import org.JavaProject.JavaNetflixProject.Entities.Category;
import org.JavaProject.JavaNetflixProject.Entities.Content;
import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

public class ContentService {
    private final ContentDAO contentDAO = new ContentDAO();

    public List<Content> getAllContent() throws SQLException { return contentDAO.findAll(); }
    public List<Category> getAllCategories() throws SQLException{return contentDAO.findAllCategories();};
    public List<Content> getFeaturedContent() throws SQLException { return contentDAO.findFeatured(); }
    public List<Content> getFilms() throws SQLException { return contentDAO.findByType(Content.Type.FILM); }
    public List<Content> getSeries() throws SQLException { return contentDAO.findByType(Content.Type.SERIE); }
    public List<Content> getByCategory(int categoryId) throws SQLException { return contentDAO.findByCategory(categoryId); }
    public List<Content> search(String keyword) throws SQLException { return contentDAO.search(keyword); }
    public Content getById(int id) throws SQLException { return contentDAO.findById(id); }
    public List<Content> getTop5() throws SQLException { return contentDAO.getTop5MostViewed(); }
    public List<Object[]> getContentByCategory() throws SQLException { return contentDAO.getContentByCategory(); }

    public Content addContent(Content content) throws SQLException {
        validateContent(content);
        return contentDAO.save(content);
    }

    public void updateContent(Content content) throws SQLException {
        validateContent(content);
        contentDAO.update(content);
    }

    public void deleteContent(int id) throws SQLException {
        contentDAO.delete(id);
    }

    public void incrementViews(int contentId) throws SQLException {
        contentDAO.incrementViewCount(contentId);
    }

    public void refreshAvgRating(int contentId) throws SQLException {
        contentDAO.updateAvgRating(contentId);
    }

    private void validateContent(Content content) {
        if (content.getTitle() == null || content.getTitle().isBlank())
            throw new IllegalArgumentException("Le titre est requis.");
        if (content.getType() == null)
            throw new IllegalArgumentException("Le type (Film/Série) est requis.");
    }
    
    public Content getRandomContent() throws SQLException {
        return contentDAO.findRandom();
    }

 /**
  * Returns all content where the casting field contains actorName.
  * Uses the existing content table — no schema change required.
  */
    private Content mapRow(ResultSet rs) throws SQLException {
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

        // ✅ FIXED TYPE (enum)
        String type = rs.getString("type");
        if (type != null) {
            c.setType(Content.Type.valueOf(type.toUpperCase()));
        }

        return c;
    }
 public List<Content> searchByCast(String actorName) throws SQLException {
     // If your ContentService already has a search() that searches casting,
     // you can simply delegate:  return search(actorName);
     //
     // Otherwise use this direct query:
     String sql =
         "SELECT c.*, cat.name AS cat_name " +
         "FROM content c " +
         "LEFT JOIN categories cat ON cat.id = c.category_id " +
         "WHERE c.casting LIKE ? " +
         "ORDER BY c.title";

     List<Content> list = new java.util.ArrayList<>();
     try (java.sql.PreparedStatement ps =
    		 ConxDB.getConnection()
                  .prepareStatement(sql)) {
         ps.setString(1, "%" + actorName + "%");
         java.sql.ResultSet rs = ps.executeQuery();
         while (rs.next()) {
             Content content = mapRow(rs);   // reuse your existing mapRow helper
             list.add(content);
         }
     }
     return list;
 }
}
