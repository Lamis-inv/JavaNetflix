package org.JavaProject.JavaNetflixProject.app;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.media.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.JavaProject.JavaNetflixProject.DAO.CommentDAO;
import org.JavaProject.JavaNetflixProject.DAO.EpisodeDAO;
import org.JavaProject.JavaNetflixProject.DAO.RatingDAO;
import org.JavaProject.JavaNetflixProject.DAO.SeasonDAO;
import org.JavaProject.JavaNetflixProject.DAO.WatchHistoryDAO;
import org.JavaProject.JavaNetflixProject.DAO.WatchlistDAO;
import org.JavaProject.JavaNetflixProject.Entities.Comment;
import org.JavaProject.JavaNetflixProject.Entities.Content;
import org.JavaProject.JavaNetflixProject.Entities.Episode;
import org.JavaProject.JavaNetflixProject.Entities.Season;
import org.JavaProject.JavaNetflixProject.Services.ContentService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import org.JavaProject.JavaNetflixProject.Utils.Session;

public class DetailController {

    @FXML private Label titleLabel;
    @FXML private Label yearLabel;
    @FXML private Label durationLabel;
    @FXML private Label categoryLabel;
    @FXML private Text synopsisText;
    @FXML private Label castingLabel;
    @FXML private Label ratingLabel;
    @FXML private ImageView coverImage;

    // Film player
    @FXML private VBox filmPlayerBox;
    @FXML private MediaView mediaView;
    @FXML private Slider progressSlider;
    @FXML private Slider volumeSlider;
    @FXML private Label timeLabel;
    @FXML private Button playPauseBtn;

    // Serie section
    @FXML private VBox serieBox;
    @FXML private ComboBox<Season> seasonCombo;
    @FXML private VBox episodeListBox;

    // Rating
    @FXML private HBox starsBox;

    // Comments
    @FXML private VBox commentsBox;
    @FXML private TextArea commentInput;

    // Watchlist
    @FXML private Button watchlistBtn;

    // Countdown overlay
    @FXML private VBox countdownOverlay;
    @FXML private Label countdownLabel;
    @FXML private StackPane rootStack;

    // The FXML fullscreen overlay fields are kept for FXML compatibility
    // but the actual fullscreen is handled via a separate Stage below.
    @FXML private StackPane fullscreenOverlay;
    @FXML private MediaView fullscreenMediaView;

    private Content content;
    private MediaPlayer mediaPlayer;
    private int userRating = 0;
    private boolean isInWatchlist = false;
    private Episode currentEpisode;

    // FIX: keep a reference to the countdown Timeline so we can stop it
    private Timeline countdownTimeline;

    // FIX: fullscreen Stage stored here (not as @FXML)
    private Stage fullscreenStage;

    private final ContentService contentService = new ContentService();
    private final SeasonDAO seasonDAO = new SeasonDAO();
    private final EpisodeDAO episodeDAO = new EpisodeDAO();
    private final RatingDAO ratingDAO = new RatingDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final WatchlistDAO watchlistDAO = new WatchlistDAO();
    private final WatchHistoryDAO watchHistoryDAO = new WatchHistoryDAO();

    @FXML
    public void initialize() {
        if (volumeSlider != null) volumeSlider.setValue(80);
        if (countdownOverlay != null) countdownOverlay.setVisible(false);
        // FIX: hide the unused FXML fullscreen overlay immediately
        if (fullscreenOverlay != null) {
            fullscreenOverlay.setVisible(false);
            fullscreenOverlay.setManaged(false);
        }

        rootStack.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        exitFullscreen();
                    }
                });
            }
        });
    }

    public void setContent(Content c) {
        this.content = c;
        populateDetails();

        if (c.isFilm()) {
            serieBox.setVisible(false); serieBox.setManaged(false);
            filmPlayerBox.setVisible(true); filmPlayerBox.setManaged(true);
            if (c.getVideoUrl() != null && !c.getVideoUrl().isBlank())
                loadMedia(c.getVideoUrl());
        } else {
            filmPlayerBox.setVisible(false); filmPlayerBox.setManaged(false);
            serieBox.setVisible(true); serieBox.setManaged(true);
            loadSeasons();
        }

        setupRating();
        loadComments();
        setupWatchlist();
        try { contentService.incrementViews(c.getId()); } catch (Exception ignored) {}
    }

    private void populateDetails() {
        titleLabel.setText(content.getTitle());
        yearLabel.setText(String.valueOf(content.getReleaseYear()));
        durationLabel.setText(content.getDurationMin() > 0 ? content.getDurationMin() + " min" : "");
        categoryLabel.setText(content.getCategory() != null ? content.getCategory().getName() : "");
        synopsisText.setText(content.getSynopsis() != null ? content.getSynopsis() : "");
        castingLabel.setText(content.getCasting() != null ? "Avec : " + content.getCasting() : "");
        ratingLabel.setText("⭐ " + String.format("%.1f", content.getAvgRating()));
        if (content.getCoverUrl() != null && !content.getCoverUrl().isBlank()) {
            try { coverImage.setImage(new Image(content.getCoverUrl(), true)); }
            catch (Exception ignored) {}
        }
    }

    private void loadMedia(String url) {
        // Double-click on the MediaView triggers fullscreen
        mediaView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                onFullscreen();
            }
        });

        if (mediaPlayer != null) mediaPlayer.dispose();
        try {
            String fixedUrl = url.startsWith("http") ? url : new java.io.File(url).toURI().toString();

            Media media = new Media(fixedUrl);
            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);

            mediaPlayer.currentTimeProperty().addListener((obs, o, n) -> updateProgressUI(n));
            mediaPlayer.setOnEndOfMedia(this::onMediaEnd);
            volumeSlider.valueProperty().addListener((obs, o, n) ->
                mediaPlayer.setVolume(n.doubleValue() / 100.0));

        } catch (Exception e) {
            showAlert("Impossible de charger la vidéo: " + e.getMessage());
        }
    }

    // FIX: extracted into a shared helper used by both the normal and fullscreen player
    private void updateProgressUI(Duration current) {
        if (progressSlider.isValueChanging()) return;
        Duration total = mediaPlayer.getTotalDuration();
        if (total != null && total.greaterThan(Duration.ZERO)) {
            progressSlider.setValue(current.toSeconds() / total.toSeconds() * 100);
        }
        int sec = (int) current.toSeconds();
        String formatted = formatTime(sec) + " / " + formatTime((int)(total != null ? total.toSeconds() : 0));
        timeLabel.setText(formatted);
    }

    private void onMediaEnd() {
        if (currentEpisode != null) {
            saveProgress(currentEpisode.getId(), (int)(mediaPlayer.getTotalDuration().toSeconds()), true);
            try {
                Episode next = episodeDAO.findNextEpisode(currentEpisode.getId());
                if (next != null) startCountdown(next);
            } catch (Exception ignored) {}
        } else {
            saveProgress(null, (int)(mediaPlayer.getTotalDuration().toSeconds()), true);
        }
    }

    private void startCountdown(Episode next) {
        // FIX: stop any already-running countdown before starting a new one
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }

        countdownOverlay.setVisible(true);
        int[] count = {10};
        countdownLabel.setText("Prochain épisode dans " + count[0] + "s");

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            count[0]--;
            countdownLabel.setText("Prochain épisode dans " + count[0] + "s");
            if (count[0] <= 0) {
                countdownOverlay.setVisible(false);
                playEpisode(next);
            }
        }));
        countdownTimeline.setCycleCount(10);
        countdownTimeline.play();
    }

    @FXML
    public void onCancelCountdown() {
        // FIX: actually stop the timeline, not just hide the overlay
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
        }
        countdownOverlay.setVisible(false);
    }

    @FXML
    public void onPlayPause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseBtn.setText("▶");
        } else {
            mediaPlayer.play();
            playPauseBtn.setText("⏸");
        }
    }

    @FXML
    public void onSeek() {
        if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null) {
            mediaPlayer.seek(Duration.seconds(
                progressSlider.getValue() / 100.0 * mediaPlayer.getTotalDuration().toSeconds()));
        }
    }

    /**
     * FIX: fullscreen now uses a proper separate Stage with its OWN controls HBox
     * so the original scene graph is never disturbed. The same MediaPlayer instance
     * is reused — no video restart, no re-buffering.
     */
    @FXML
    private void onFullscreen() {
        if (mediaPlayer == null) return;

        if (fullscreenStage == null) {
            fullscreenStage = new Stage();

            // --- Media view for fullscreen (shares the same MediaPlayer) ---
            MediaView fsView = new MediaView(mediaPlayer);
            fsView.setPreserveRatio(true);
            // Bind to stage size so it fills the window
            fsView.fitWidthProperty().bind(fullscreenStage.widthProperty());
            fsView.fitHeightProperty().bind(fullscreenStage.heightProperty().subtract(60));

            // --- Duplicate controls (bound to the same MediaPlayer / sliders) ---
            Button fsPlayPause = new Button(
                mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING ? "⏸" : "▶");
            fsPlayPause.getStyleClass().add("btn-player");

            Slider fsProgress = new Slider();
            fsProgress.getStyleClass().add("progress-slider");
            HBox.setHgrow(fsProgress, Priority.ALWAYS);

            Label fsTime = new Label("00:00 / 00:00");
            fsTime.getStyleClass().add("time-label");

            Slider fsVolume = new Slider(0, 100, volumeSlider.getValue());
            fsVolume.setPrefWidth(100);
            fsVolume.getStyleClass().add("volume-slider");

            Button fsExit = new Button("⛶ Quitter");
            fsExit.getStyleClass().add("btn-player");

            // Sync progress slider and time label from the already-running player
            mediaPlayer.currentTimeProperty().addListener((obs, o, n) -> {
                if (!fsProgress.isValueChanging()) {
                    Duration total = mediaPlayer.getTotalDuration();
                    if (total != null && total.greaterThan(Duration.ZERO))
                        fsProgress.setValue(n.toSeconds() / total.toSeconds() * 100);
                    int sec = (int) n.toSeconds();
                    fsTime.setText(formatTime(sec) + " / " +
                        formatTime((int)(total != null ? total.toSeconds() : 0)));
                }
            });

            // Wire up fullscreen controls
            fsPlayPause.setOnAction(e -> {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    fsPlayPause.setText("▶");
                    playPauseBtn.setText("▶");     // keep main controls in sync
                } else {
                    mediaPlayer.play();
                    fsPlayPause.setText("⏸");
                    playPauseBtn.setText("⏸");
                }
            });

            fsProgress.setOnMouseReleased(e -> {
                if (mediaPlayer.getTotalDuration() != null) {
                    mediaPlayer.seek(Duration.seconds(
                        fsProgress.getValue() / 100.0 * mediaPlayer.getTotalDuration().toSeconds()));
                    // Mirror back to main slider
                    progressSlider.setValue(fsProgress.getValue());
                }
            });

            fsVolume.valueProperty().addListener((obs, o, n) -> {
                mediaPlayer.setVolume(n.doubleValue() / 100.0);
                volumeSlider.setValue(n.doubleValue()); // keep main slider in sync
            });

            fsExit.setOnAction(e -> exitFullscreen());

            HBox controls = new HBox(12, fsPlayPause, fsProgress, fsTime,
                                     new Label("🔊"), fsVolume, fsExit);
            controls.setAlignment(Pos.CENTER_LEFT);
            controls.getStyleClass().add("player-controls");
            controls.setStyle("-fx-padding: 8 16 8 16;");

            BorderPane fsRoot = new BorderPane();
            fsRoot.setStyle("-fx-background-color: black;");
            fsRoot.setCenter(fsView);
            fsRoot.setBottom(controls);
            BorderPane.setAlignment(controls, Pos.CENTER);

            double w = Screen.getPrimary().getBounds().getWidth();
            double h = Screen.getPrimary().getBounds().getHeight();
            Scene fsScene = new Scene(fsRoot, w, h);
            fsScene.setFill(Color.BLACK);

            // Apply the same stylesheet if available
            if (!rootStack.getScene().getStylesheets().isEmpty()) {
                fsScene.getStylesheets().addAll(rootStack.getScene().getStylesheets());
            }

            fsScene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) exitFullscreen();
                if (e.getCode() == KeyCode.SPACE)  fsPlayPause.fire();
            });

            fullscreenStage.setScene(fsScene);
            fullscreenStage.setFullScreen(true);
            fullscreenStage.setFullScreenExitHint("");
            // When the window is closed with the X button, clean up too
            fullscreenStage.setOnCloseRequest(e -> exitFullscreen());
        }

        fullscreenStage.show();
        fullscreenStage.toFront();
    }

    @FXML
    private void exitFullscreen() {
        if (fullscreenStage != null && fullscreenStage.isShowing()) {
            fullscreenStage.hide();
        }
    }

    private void loadSeasons() {
        try {
            List<Season> seasons = seasonDAO.findBySerieId(content.getId());
            seasonCombo.getItems().addAll(seasons);
            seasonCombo.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
                if (n != null) loadEpisodes(n);
            });
            if (!seasons.isEmpty()) seasonCombo.getSelectionModel().selectFirst();
        } catch (Exception e) {
            showAlert("Erreur chargement saisons: " + e.getMessage());
        }
    }

    private void loadEpisodes(Season season) {
        episodeListBox.getChildren().clear();
        try {
            List<Episode> episodes = episodeDAO.findBySeasonId(season.getId());
            for (Episode ep : episodes) {
                int userId = Session.getCurrentUser().getId();
                ep.setWatched(watchHistoryDAO.isCompleted(userId, ep.getId()));
                ep.setProgressSec(watchHistoryDAO.getProgressSec(userId, ep.getId()));
                episodeListBox.getChildren().add(buildEpisodeRow(ep));
            }
        } catch (Exception e) {
            showAlert("Erreur chargement épisodes: " + e.getMessage());
        }
    }

    private HBox buildEpisodeRow(Episode ep) {
        HBox row = new HBox(12);
        row.getStyleClass().add("episode-row");
        row.setCursor(javafx.scene.Cursor.HAND);

        Label numLbl = new Label("E" + ep.getEpisodeNum());
        numLbl.getStyleClass().add("episode-num");

        VBox info = new VBox(3);
        Label titleLbl = new Label(ep.getTitle());
        titleLbl.getStyleClass().add("episode-title");

        Label synLbl = new Label(ep.getSynopsis() != null ? ep.getSynopsis() : "");
        synLbl.getStyleClass().add("episode-synopsis");
        synLbl.setWrapText(true);

        Label durLbl = new Label(ep.getDurationMin() + " min");
        durLbl.getStyleClass().add("episode-duration");

        info.getChildren().addAll(titleLbl, synLbl, durLbl);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label statusLbl = new Label(ep.isWatched() ? "✅ Vu" : (ep.getProgressSec() > 0 ? "▶ En cours" : ""));
        statusLbl.getStyleClass().add("episode-status");

        row.getChildren().addAll(numLbl, info, statusLbl);

        row.setOnMouseEntered(e -> { row.setScaleX(1.02); row.setScaleY(1.02); });
        row.setOnMouseExited(e  -> { row.setScaleX(1);    row.setScaleY(1); });
        row.setOnMouseClicked(e -> playEpisode(ep));

        return row;
    }

    private void playEpisode(Episode ep) {
        // FIX: cancel any running countdown properly
        if (countdownTimeline != null) {
            countdownTimeline.stop();
            countdownTimeline = null;
        }
        countdownOverlay.setVisible(false);
        currentEpisode = ep;

        filmPlayerBox.setVisible(true);
        filmPlayerBox.setManaged(true);

        if (mediaPlayer != null) mediaPlayer.dispose();

        // FIX: if a fullscreen stage exists, invalidate it so it's rebuilt
        // with the new MediaPlayer on the next fullscreen request
        if (fullscreenStage != null) {
            fullscreenStage.close();
            fullscreenStage = null;
        }

        String url = ep.getVideoUrl();
        if (!url.startsWith("http")) {
            url = new java.io.File(url).toURI().toString();
        }

        Media media = new Media(url);
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);

        int savedSec = ep.getProgressSec();
        mediaPlayer.setOnReady(() -> {
            if (savedSec > 0) mediaPlayer.seek(Duration.seconds(savedSec));
            mediaPlayer.play();
            playPauseBtn.setText("⏸");
        });

        mediaPlayer.currentTimeProperty().addListener((obs, o, n) -> updateProgressUI(n));
        mediaPlayer.setOnEndOfMedia(this::onMediaEnd);
    }

    private void saveProgress(Integer episodeId, int progressSec, boolean completed) {
        try {
            int userId = Session.getCurrentUser().getId();
            watchHistoryDAO.saveProgress(userId, content.getId(), episodeId, progressSec, completed);
        } catch (Exception ignored) {}
    }

    private void setupRating() {
        starsBox.getChildren().clear();
        try {
            int userId = Session.getCurrentUser().getId();
            userRating = ratingDAO.getUserRating(userId, content.getId());
        } catch (Exception ignored) {}

        for (int i = 1; i <= 5; i++) {
            final int star = i;
            Label lbl = new Label(i <= userRating ? "★" : "☆");
            lbl.getStyleClass().add("star");
            lbl.setStyle("-fx-font-size: 28px; -fx-text-fill: " + (i <= userRating ? "#f5c518" : "#888") + "; -fx-cursor: hand;");
            lbl.setOnMouseClicked(e -> onRate(star));
            lbl.setOnMouseEntered(e -> {
                for (int j = 0; j < starsBox.getChildren().size(); j++) {
                    Label s = (Label) starsBox.getChildren().get(j);
                    s.setText(j < star ? "★" : "☆");
                    s.setStyle("-fx-font-size: 28px; -fx-text-fill: " + (j < star ? "#f5c518" : "#888") + "; -fx-cursor: hand;");
                }
            });
            starsBox.getChildren().add(lbl);
        }
        starsBox.setOnMouseExited(e -> refreshStars());
    }

    private void onRate(int stars) {
        userRating = stars;
        try {
            int userId = Session.getCurrentUser().getId();
            ratingDAO.upsertRating(userId, content.getId(), stars);
            contentService.refreshAvgRating(content.getId());
            Content updated = contentService.getById(content.getId());
            ratingLabel.setText("⭐ " + String.format("%.1f", updated.getAvgRating()));
        } catch (Exception e) {
            showAlert("Erreur: " + e.getMessage());
        }
        refreshStars();
    }

    private void refreshStars() {
        for (int j = 0; j < starsBox.getChildren().size(); j++) {
            Label s = (Label) starsBox.getChildren().get(j);
            s.setText(j < userRating ? "★" : "☆");
            s.setStyle("-fx-font-size: 28px; -fx-text-fill: " + (j < userRating ? "#f5c518" : "#888") + "; -fx-cursor: hand;");
        }
    }

    private void loadComments() {
        commentsBox.getChildren().clear();
        try {
            List<Comment> comments = commentDAO.findByContentId(content.getId());
            for (Comment c : comments) {
                VBox cb = new VBox(4);
                cb.getStyleClass().add("comment-box");
                Label author = new Label(c.getUserName() + " • " +
                    (c.getCreatedAt() != null ? c.getCreatedAt().toLocalDate() : ""));
                author.getStyleClass().add("comment-author");
                Label body = new Label(c.getBody());
                body.setWrapText(true);
                body.getStyleClass().add("comment-body");

                Button flagBtn = new Button("🚩");
                flagBtn.getStyleClass().add("btn-flag");
                flagBtn.setOnAction(e -> {
                    try { commentDAO.flag(c.getId()); loadComments(); }
                    catch (Exception ignored) {}
                });

                cb.getChildren().addAll(author, body, flagBtn);
                commentsBox.getChildren().add(cb);
            }
        } catch (Exception e) {
            showAlert("Erreur commentaires: " + e.getMessage());
        }
    }

    @FXML
    public void onPostComment() {
        String text = commentInput.getText().trim();
        if (text.isEmpty()) return;
        Comment c = new Comment();
        c.setUserId(Session.getCurrentUser().getId());
        c.setContentId(content.getId());
        c.setBody(text);
        try {
            commentDAO.save(c);
            commentInput.clear();
            loadComments();
        } catch (Exception e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    private void setupWatchlist() {
        try {
            isInWatchlist = watchlistDAO.isInWatchlist(Session.getCurrentUser().getId(), content.getId());
            updateWatchlistBtn();
        } catch (Exception ignored) {}
    }

    @FXML
    public void onToggleWatchlist() {
        try {
            int userId = Session.getCurrentUser().getId();
            if (isInWatchlist) {
                watchlistDAO.remove(userId, content.getId());
                isInWatchlist = false;
            } else {
                watchlistDAO.add(userId, content.getId());
                isInWatchlist = true;
            }
            updateWatchlistBtn();
        } catch (Exception e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    private void updateWatchlistBtn() {
        watchlistBtn.setText(isInWatchlist ? "✓ Ma liste" : "+ Ma liste");
        watchlistBtn.getStyleClass().removeAll("btn-added");
        if (isInWatchlist) watchlistBtn.getStyleClass().add("btn-added");
    }

    @FXML
    public void onBack() {
        // FIX: close fullscreen stage before navigating away
        if (fullscreenStage != null) {
            fullscreenStage.close();
            fullscreenStage = null;
        }
        if (mediaPlayer != null) {
            if (currentEpisode != null)
                saveProgress(currentEpisode.getId(), (int) mediaPlayer.getCurrentTime().toSeconds(), false);
            mediaPlayer.dispose();
        }
        try { Navigator.navigateTo("/ui/home.fxml", 1280, 800); }
        catch (Exception e) { showAlert(e.getMessage()); }
    }

    private String formatTime(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.showAndWait();
    }
}