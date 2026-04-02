package org.JavaProject.JavaNetflixProject.DAO;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.JavaProject.JavaNetflixProject.Entities.Episode;
import org.JavaProject.JavaNetflixProject.Utils.ConxDB;

public class EpisodeDAO {

    public List<Episode> findBySeasonId(int seasonId) throws SQLException {
        List<Episode> list = new ArrayList<>();
        String sql = "SELECT * FROM episodes WHERE season_id=? ORDER BY episode_num";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, seasonId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapEpisode(rs));
        }
        return list;
    }

    public Episode findById(int id) throws SQLException {
        String sql = "SELECT * FROM episodes WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapEpisode(rs);
        }
        return null;
    }

    public Episode findNextEpisode(int currentEpisodeId) throws SQLException {
        String sql = "SELECT e2.* FROM episodes e1 " +
                     "JOIN episodes e2 ON e1.season_id=e2.season_id AND e2.episode_num=e1.episode_num+1 " +
                     "WHERE e1.id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentEpisodeId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapEpisode(rs);
        }
        return null;
    }

    public Episode save(Episode e) throws SQLException {
        String sql = "INSERT INTO episodes (season_id, episode_num, title, synopsis, duration_min, video_url, thumbnail_url) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getSeasonId()); ps.setInt(2, e.getEpisodeNum());
            ps.setString(3, e.getTitle()); ps.setString(4, e.getSynopsis());
            ps.setInt(5, e.getDurationMin()); ps.setString(6, e.getVideoUrl());
            ps.setString(7, e.getThumbnailUrl());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) e.setId(keys.getInt(1));
        }
        return e;
    }

    public void update(Episode e) throws SQLException {
        String sql = "UPDATE episodes SET season_id=?, episode_num=?, title=?, synopsis=?, duration_min=?, video_url=?, thumbnail_url=? WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, e.getSeasonId()); ps.setInt(2, e.getEpisodeNum());
            ps.setString(3, e.getTitle()); ps.setString(4, e.getSynopsis());
            ps.setInt(5, e.getDurationMin()); ps.setString(6, e.getVideoUrl());
            ps.setString(7, e.getThumbnailUrl()); ps.setInt(8, e.getId());
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM episodes WHERE id=?";
        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    private Episode mapEpisode(ResultSet rs) throws SQLException {
        Episode e = new Episode();
        e.setId(rs.getInt("id"));
        e.setSeasonId(rs.getInt("season_id"));
        e.setEpisodeNum(rs.getInt("episode_num"));
        e.setTitle(rs.getString("title"));
        e.setSynopsis(rs.getString("synopsis"));
        e.setDurationMin(rs.getInt("duration_min"));
        e.setVideoUrl(rs.getString("video_url"));
        e.setThumbnailUrl(rs.getString("thumbnail_url"));
        return e;
    }
}
