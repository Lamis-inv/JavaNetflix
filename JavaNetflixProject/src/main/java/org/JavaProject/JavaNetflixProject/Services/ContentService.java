package org.JavaProject.JavaNetflixProject.Services;

import java.sql.SQLException;
import java.util.List;

import org.JavaProject.JavaNetflixProject.DAO.ContentDAO;
import org.JavaProject.JavaNetflixProject.Entities.Category;
import org.JavaProject.JavaNetflixProject.Entities.Content;

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
}
