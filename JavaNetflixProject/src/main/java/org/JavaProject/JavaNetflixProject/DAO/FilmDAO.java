package org.JavaProject.JavaNetflixProject.DAO;


import org.JavaProject.JavaNetflixProject.Entities.Film;
import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FilmDAO {

    private Connection conn;

    public FilmDAO() {
        conn = ConxDB.getInstance();  // Singleton connection
    }

    // Get films by genre
    public List<Film> getByGenre(String genre) {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM film WHERE genre = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genre);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Film film = new Film(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("genre"),
                        rs.getDate("date_sortie").toLocalDate(),
                        rs.getString("video_url"),
                        rs.getString("cover_url")
                );
                films.add(film);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return films;
    }

    public List<Film> getAllFilms() {
        List<Film> films = new ArrayList<>();
        String sql = "SELECT * FROM film";

        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
            	Film film = new Film(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getString("genre"),
                        rs.getDate("date_sortie").toLocalDate(),
                        rs.getString("video_url"),
                        rs.getString("cover_url")
                );
                films.add(film);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return films;
    }
}
