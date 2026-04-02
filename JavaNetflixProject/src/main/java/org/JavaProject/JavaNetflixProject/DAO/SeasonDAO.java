package org.JavaProject.JavaNetflixProject.DAO;




import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.JavaProject.JavaNetflixProject.Entities.Season;
import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

public class SeasonDAO {

    public List<Season> findBySerieId(int serieId) throws SQLException {
        List<Season> list = new ArrayList<>();
        String sql = "SELECT * FROM seasons WHERE serie_id=? ORDER BY number";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, serieId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapSeason(rs));
        }
        return list;
    }

    public Season findById(int id) throws SQLException {
        String sql = "SELECT * FROM seasons WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapSeason(rs);
        }
        return null;
    }

    public Season save(Season s) throws SQLException {
        String sql = "INSERT INTO seasons (serie_id, number, title) VALUES (?,?,?)";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, s.getSerieId()); ps.setInt(2, s.getNumber()); ps.setString(3, s.getTitle());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) s.setId(keys.getInt(1));
        }
        return s;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM seasons WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    private Season mapSeason(ResultSet rs) throws SQLException {
        return new Season(rs.getInt("id"), rs.getInt("serie_id"), rs.getInt("number"), rs.getString("title"));
    }
}
