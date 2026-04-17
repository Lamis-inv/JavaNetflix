package org.JavaProject.JavaNetflixProject.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import org.JavaProject.JavaNetflixProject.DAO.*;
import org.JavaProject.JavaNetflixProject.Entities.*;
import org.JavaProject.JavaNetflixProject.Services.ContentService;
import org.JavaProject.JavaNetflixProject.Utils.ConxDB;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import org.JavaProject.JavaNetflixProject.Utils.Session;

public class MyListController {

    @FXML private HBox      cwGrid;
    @FXML private FlowPane  watchlistGrid;
    @FXML private Label     usernameLabel;
    @FXML private Label     cwCountLabel;
    @FXML private Label     wlCountLabel;
    @FXML private VBox      cwEmpty;
    @FXML private VBox      wlEmpty;

    private final WatchlistDAO    watchlistDAO    = new WatchlistDAO();
    private final WatchHistoryDAO watchHistoryDAO = new WatchHistoryDAO();
    private final ContentService  contentService  = new ContentService();
    private final EpisodeDAO      episodeDAO      = new EpisodeDAO();
    private final SeasonDAO       seasonDAO       = new SeasonDAO();

    @FXML
    public void initialize() {
        User user = Session.getCurrentUser();
        usernameLabel.setText(user.getNom());
        loadContinueWatching();
        loadWatchlist();
    }

    // ═══════════════════════════════════════════════════════
    // CONTINUE WATCHING
    // ═══════════════════════════════════════════════════════

    private void loadContinueWatching() {
        cwGrid.getChildren().clear();
        int userId = Session.getCurrentUser().getId();

        try {
            // Fetch in-progress content with progress data
            List<InProgressItem> items = getInProgressItems(userId);

            cwCountLabel.setText(items.size() + (items.size() == 1 ? " item" : " items"));

            if (items.isEmpty()) {
                cwEmpty.setVisible(true);
                cwEmpty.setManaged(true);
                return;
            }

            for (InProgressItem item : items) {
                cwGrid.getChildren().add(buildCWCard(item));
            }

        } catch (Exception e) {
            System.err.println("Continue watching load error: " + e.getMessage());
        }
    }

    /**
     * Query watch_history joined with content to get in-progress items.
     * Returns content where completed=0 OR (series with at least one in-progress episode).
     */
    private List<InProgressItem> getInProgressItems(int userId) throws SQLException {
        List<InProgressItem> result = new ArrayList<>();
        Set<Integer> addedContentIds = new HashSet<>();

        // 1. Films in progress (completed=0, progress > 10 sec)
        String filmSql =
            "SELECT wh.content_id, wh.progress_sec, wh.episode_id, " +
            "       c.title, c.cover_url, c.duration_min, c.type " +
            "FROM watch_history wh " +
            "JOIN content c ON c.id = wh.content_id " +
            "WHERE wh.user_id = ? AND wh.completed = 0 AND wh.progress_sec > 10 " +
            "  AND c.type = 'FILM' " +
            "ORDER BY wh.watched_at DESC";

        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(filmSql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int contentId = rs.getInt("content_id");
                if (addedContentIds.contains(contentId)) continue;
                addedContentIds.add(contentId);

                InProgressItem item = new InProgressItem();
                item.contentId    = contentId;
                item.title        = rs.getString("title");
                item.coverUrl     = rs.getString("cover_url");
                item.progressSec  = rs.getInt("progress_sec");
                item.durationMin  = rs.getInt("duration_min");
                item.isFilm       = true;
                item.episodeId    = 0;
                item.episodeLabel = "";
                result.add(item);
            }
        }

        // 2. Series with in-progress episodes
        String serieSql =
            "SELECT wh.content_id, wh.episode_id, wh.progress_sec, wh.completed, " +
            "       c.title AS content_title, c.cover_url, " +
            "       e.title AS ep_title, e.episode_num, e.duration_min, " +
            "       s.number AS season_num " +
            "FROM watch_history wh " +
            "JOIN content c ON c.id = wh.content_id " +
            "JOIN episodes e ON e.id = wh.episode_id " +
            "JOIN seasons s ON s.id = e.season_id " +
            "WHERE wh.user_id = ? AND wh.episode_id IS NOT NULL " +
            "  AND wh.completed = 0 AND wh.progress_sec > 5 " +
            "ORDER BY wh.watched_at DESC";

        try (Connection conn = ConxDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(serieSql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int contentId = rs.getInt("content_id");
                if (addedContentIds.contains(contentId)) continue;
                addedContentIds.add(contentId);

                InProgressItem item = new InProgressItem();
                item.contentId    = contentId;
                item.title        = rs.getString("content_title");
                item.coverUrl     = rs.getString("cover_url");
                item.progressSec  = rs.getInt("progress_sec");
                item.durationMin  = rs.getInt("duration_min");
                item.isFilm       = false;
                item.episodeId    = rs.getInt("episode_id");
                item.seasonNum    = rs.getInt("season_num");
                item.episodeNum   = rs.getInt("episode_num");
                item.episodeTitle = rs.getString("ep_title");
                item.episodeLabel = "S" + item.seasonNum + " E" + item.episodeNum;
                result.add(item);
            }
        }

        return result;
    }

    /** Build a "continue watching" card with progress bar */
    private VBox buildCWCard(InProgressItem item) {
        VBox card = new VBox(0);
        card.getStyleClass().add("cw-card");
        card.setCursor(javafx.scene.Cursor.HAND);

        // ── Poster / thumbnail area ──
        StackPane posterStack = new StackPane();
        posterStack.getStyleClass().add("cw-poster-wrap");
        posterStack.setMinHeight(150); posterStack.setMaxHeight(150); posterStack.setPrefHeight(150);

        // Poster image
        ImageView img = new ImageView();
        img.setFitWidth(260); img.setFitHeight(150);
        img.setPreserveRatio(false);
        img.getStyleClass().add("cw-poster");

        // Clip image to rounded top
        Rectangle clip = new Rectangle(260, 150);
        clip.setArcWidth(12); clip.setArcHeight(12);
        img.setClip(clip);

        if (item.coverUrl != null && !item.coverUrl.isBlank()) {
            try { img.setImage(new Image(item.coverUrl, 260, 150, false, true, true)); }
            catch (Exception ignored) {}
        }

        // Dark overlay
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color:rgba(0,0,0,0.35);");
        overlay.setMinWidth(260); overlay.setMinHeight(150);

        // Play button
        Button playBtn = new Button("▶");
        playBtn.getStyleClass().add("cw-play-btn");
        playBtn.setAlignment(Pos.CENTER);

        // Episode badge (top-left)
        HBox badgeBox = new HBox();
        badgeBox.setAlignment(Pos.TOP_LEFT);
        badgeBox.setPadding(new javafx.geometry.Insets(8, 0, 0, 8));
        if (!item.isFilm && !item.episodeLabel.isEmpty()) {
            Label epBadge = new Label(item.episodeLabel);
            epBadge.getStyleClass().add("cw-episode-badge");
            badgeBox.getChildren().add(epBadge);
        }

        posterStack.getChildren().addAll(img, overlay, playBtn, badgeBox);

        // ── Content area ──
        VBox contentArea = new VBox(5);
        contentArea.getStyleClass().add("cw-content-area");

        Label titleLbl = new Label(item.title);
        titleLbl.getStyleClass().add("cw-title");

        VBox infoBox = new VBox(3);
        if (!item.isFilm && item.episodeTitle != null) {
            Label epTitleLbl = new Label(item.episodeTitle);
            epTitleLbl.getStyleClass().add("cw-ep-title");
            infoBox.getChildren().add(epTitleLbl);
        }

        // Progress bar
        StackPane progressBg = new StackPane();
        progressBg.getStyleClass().add("cw-progress-bg");
        progressBg.setMaxWidth(Double.MAX_VALUE);

        Pane progressFill = new Pane();
        progressFill.getStyleClass().add("cw-progress-fill");

        // Calculate progress percentage
        double pct = 0;
        if (item.durationMin > 0) {
            pct = Math.min(1.0, item.progressSec / (item.durationMin * 60.0));
        } else if (item.progressSec > 0) {
            pct = 0.3; // fallback estimate
        }

        // Set width proportionally once the card is shown
        final double finalPct = pct;
        progressBg.widthProperty().addListener((obs, o, n) -> {
            progressFill.setPrefWidth(n.doubleValue() * finalPct);
        });
        progressFill.setPrefHeight(3);
        progressBg.getChildren().add(progressFill);

        // Time remaining
        String timeLeft = formatTimeLeft(item.progressSec, item.durationMin);
        Label timeLbl = new Label(timeLeft);
        timeLbl.getStyleClass().add("cw-time-left");

        contentArea.getChildren().addAll(titleLbl, infoBox, progressBg, timeLbl);
        card.getChildren().addAll(posterStack, contentArea);

        // ── Click: open detail and continue from last position ──
        card.setOnMouseClicked(e -> {
			try {
				continueWatching(item);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
        playBtn.setOnAction(e -> { e.consume(); try {
			continueWatching(item);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} });

        return card;
    }

    private void continueWatching(InProgressItem item) throws SQLException {
        try {
            Content c = contentService.getById(item.contentId);
            if (c == null) return;

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Detail.fxml"));
            Parent root = loader.load();
            DetailController ctrl = loader.getController();

            if (!item.isFilm && item.episodeId > 0) {
                // For series: pass the specific episode to resume
                ctrl.setContent(c);
                ctrl.setResumeEpisodeId(item.episodeId, item.progressSec);
            } else {
                // For films: set content and resume time
                ctrl.setContent(c);
                ctrl.setResumeTime(item.progressSec);
            }

            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
            Navigator.getPrimaryStage().setScene(scene);
        } catch (IOException ex) {
            showAlert("Error: " + ex.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════
    // WATCHLIST
    // ═══════════════════════════════════════════════════════

    private void loadWatchlist() {
        watchlistGrid.getChildren().clear();
        int userId = Session.getCurrentUser().getId();

        try {
            List<Integer> ids = watchlistDAO.getWatchlistContentIds(userId);
            wlCountLabel.setText(ids.size() + " saved");

            if (ids.isEmpty()) {
                wlEmpty.setVisible(true);
                wlEmpty.setManaged(true);
                return;
            }

            for (int id : ids) {
                Content c = contentService.getById(id);
                if (c != null) watchlistGrid.getChildren().add(buildWLCard(c));
            }
        } catch (Exception e) {
            System.err.println("Watchlist load error: " + e.getMessage());
        }
    }

    private VBox buildWLCard(Content c) {
        VBox card = new VBox(0);
        card.getStyleClass().add("wl-card");
        card.setCursor(javafx.scene.Cursor.HAND);

        // Poster
        StackPane posterWrap = new StackPane();
        posterWrap.getStyleClass().add("wl-poster-wrap");

        ImageView img = new ImageView();
        img.setFitWidth(160); img.setFitHeight(220);
        img.setPreserveRatio(false);

        Rectangle clip = new Rectangle(160, 220);
        clip.setArcWidth(10); clip.setArcHeight(10);
        img.setClip(clip);

        if (c.getCoverUrl() != null && !c.getCoverUrl().isBlank()) {
            try { img.setImage(new Image(c.getCoverUrl(), 160, 220, false, true, true)); }
            catch (Exception ignored) {}
        }

        posterWrap.getChildren().add(img);

        // Info
        VBox info = new VBox(5);
        info.getStyleClass().add("wl-card-info");

        Label badge = new Label(c.isFilm() ? "FILM" : "SÉRIE");
        badge.getStyleClass().add(c.isFilm() ? "badge-film-sm" : "badge-serie-sm");

        Label title = new Label(c.getTitle());
        title.getStyleClass().add("wl-card-title");

        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);
        if (c.getReleaseYear() > 0) {
            Label year = new Label(String.valueOf(c.getReleaseYear()));
            year.getStyleClass().add("wl-card-meta");
            meta.getChildren().add(year);
        }
        if (c.getAvgRating() > 0) {
            Label rating = new Label("★ " + String.format("%.1f", c.getAvgRating()));
            rating.getStyleClass().add("wl-rating");
            meta.getChildren().add(rating);
        }

        info.getChildren().addAll(badge, title, meta);
        card.getChildren().addAll(posterWrap, info);
        card.setOnMouseClicked(e -> openDetail(c));
        return card;
    }

    private void openDetail(Content c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Detail.fxml"));
            Parent root = loader.load();
            DetailController ctrl = loader.getController();
            ctrl.setContent(c);
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1280, 800);
            scene.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
            Navigator.getPrimaryStage().setScene(scene);
        } catch (IOException ex) {
            showAlert("Error: " + ex.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════

    private String formatTimeLeft(int progressSec, int durationMin) {
        if (durationMin <= 0) return "";
        int totalSec  = durationMin * 60;
        int remaining = Math.max(0, totalSec - progressSec);
        int mins = remaining / 60;
        if (mins < 1) return "Almost done";
        if (mins < 60) return mins + " min left";
        return (mins / 60) + "h " + (mins % 60) + "m left";
    }

    @FXML
    public void onBack() {
        try { Navigator.navigateTo("/ui/home.fxml", 1280, 800); }
        catch (Exception e) { showAlert(e.getMessage()); }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    // ═══════════════════════════════════════════════════════
    // INNER CLASS: holds in-progress watch data
    // ═══════════════════════════════════════════════════════
    private static class InProgressItem {
        int    contentId;
        String title;
        String coverUrl;
        int    progressSec;
        int    durationMin;
        boolean isFilm;
        int    episodeId;
        int    seasonNum;
        int    episodeNum;
        String episodeTitle;
        String episodeLabel;
    }
}