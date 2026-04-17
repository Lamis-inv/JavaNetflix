package org.JavaProject.JavaNetflixProject.app;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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
import javafx.scene.web.WebView;
import javafx.stage.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.JavaProject.JavaNetflixProject.DAO.*;
import org.JavaProject.JavaNetflixProject.Entities.*;
import org.JavaProject.JavaNetflixProject.Services.ContentService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import org.JavaProject.JavaNetflixProject.Utils.Session;

public class DetailController {

    // ── FXML ─────────────────────────────────────────────────────────────────
    @FXML private Label     titleLabel, yearLabel, durationLabel, categoryLabel, ratingLabel, castingLabel;
    @FXML private Text      synopsisText;
    @FXML private ImageView coverImage;

    // Player
    @FXML private VBox     filmPlayerBox;
    @FXML private MediaView mediaView;
    @FXML private Slider   progressSlider, volumeSlider;
    @FXML private Label    timeLabel;
    @FXML private Button   playPauseBtn;

    // Serie
    @FXML private VBox              serieBox;
    @FXML private ComboBox<Season>  seasonCombo;
    @FXML private VBox              episodeListBox;

    // Rating / comments / watchlist
    @FXML private HBox     starsBox;
    @FXML private VBox     commentsBox;
    @FXML private TextArea commentInput;
    @FXML private Button   watchlistBtn;

    // Countdown
    @FXML private VBox     countdownOverlay;
    @FXML private Label    countdownLabel;
    @FXML private StackPane rootStack;

    // Trailer button (#1)
    @FXML private Button trailerBtn;

    // Cast row (#6) — clickable actor chips
    @FXML private FlowPane castRow;

    // Unused FXML overlay — kept for FXML compatibility
    @FXML private StackPane fullscreenOverlay;
    @FXML private MediaView fullscreenMediaView;

    // ── State ─────────────────────────────────────────────────────────────────
    private Content      content;
    private MediaPlayer  mediaPlayer;
    private int          userRating    = 0;
    private boolean      isInWatchlist = false;
    private Episode      currentEpisode;
    private Timeline     countdownTimeline;
    private Stage        fullscreenStage;

    private final ContentService   contentService   = new ContentService();
    private final SeasonDAO        seasonDAO        = new SeasonDAO();
    private final EpisodeDAO       episodeDAO       = new EpisodeDAO();
    private final RatingDAO        ratingDAO        = new RatingDAO();
    private final CommentDAO       commentDAO       = new CommentDAO();
    private final WatchlistDAO     watchlistDAO     = new WatchlistDAO();
    private final WatchHistoryDAO  watchHistoryDAO  = new WatchHistoryDAO();

    // ── Init ──────────────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        if (volumeSlider != null) volumeSlider.setValue(80);
        if (countdownOverlay != null) countdownOverlay.setVisible(false);
        if (fullscreenOverlay != null) {
            fullscreenOverlay.setVisible(false);
            fullscreenOverlay.setManaged(false);
        }
        rootStack.sceneProperty().addListener((obs, o, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) exitFullscreen();
                    if (e.getCode() == KeyCode.SPACE && mediaPlayer != null) onPlayPause();
                });
            }
        });

        // FIX #8: bind MediaView width to its parent StackPane on layout
        Platform.runLater(this::bindPlayerToParent);
    }

    private void bindPlayerToParent() {
    	if (mediaView != null && mediaView.getParent() instanceof StackPane) {
    	    StackPane sp = (StackPane) mediaView.getParent();

    	    mediaView.fitWidthProperty().bind(sp.widthProperty());
    	    mediaView.fitHeightProperty().bind(sp.widthProperty().multiply(9.0 / 16.0));
    	}
    }
    private int resumeTimeSec = 0;

    public void setResumeTime(int seconds) {
        this.resumeTimeSec = seconds;

        // If player already loaded → seek immediately
        if (mediaPlayer != null) {
            mediaPlayer.setOnReady(() -> {
                if (resumeTimeSec > 0) {
                    mediaPlayer.seek(Duration.seconds(resumeTimeSec));
                }
            });
        }
    }
    private int resumeEpisodeId = -1;
    private int resumeEpisodeTime = 0;

    public void setResumeEpisodeId(int episodeId, int seconds) {
        this.resumeEpisodeId = episodeId;
        this.resumeEpisodeTime = seconds;
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

        // FIX #1: show/hide trailer button based on trailer_url
        if (trailerBtn != null) {
            boolean hasTrailer = c.getTrailerUrl() != null && !c.getTrailerUrl().isBlank();
            trailerBtn.setVisible(hasTrailer);
            trailerBtn.setManaged(hasTrailer);
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
        ratingLabel.setText("\u2605 " + String.format("%.1f", content.getAvgRating()));

        if (content.getCoverUrl() != null && !content.getCoverUrl().isBlank()) {
            try { coverImage.setImage(new Image(content.getCoverUrl(), true)); }
            catch (Exception ignored) {}
        }

        // FIX #6: build clickable actor chips instead of a plain label
        buildCastChips(content.getCasting());
    }

    /**
     * FIX #6: Splits the casting string by comma and creates a clickable
     * chip per actor. Clicking opens a search-results dialog showing all
     * content that actor appears in (using the existing search() method —
     * no DB change needed).
     */
    private void buildCastChips(String casting) {
        if (castRow == null) return;
        castRow.getChildren().clear();
        if (casting == null || casting.isBlank()) {
            castingLabel.setText("");
            return;
        }
        castingLabel.setText(""); // we replace the label with chips

        List<String> actors = Arrays.stream(casting.split(","))
            .map(String::trim).filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

        for (String actor : actors) {
            Button chip = new Button(actor);
            chip.getStyleClass().add("actor-chip");
            chip.setOnAction(e -> openActorSearch(actor));
            castRow.getChildren().add(chip);
        }
    }

    /** Opens a modal showing all content the actor appears in */
    private void openActorSearch(String actorName) {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Filmographie : " + actorName);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(560); dialog.setHeight(480);

        VBox list = new VBox(10);
        list.setPadding(new Insets(14));
        list.setStyle("-fx-background-color:#111;");

        try {
            // Reuse existing ContentService.search() — searches title + casting
            List<Content> results = contentService.searchByCast(actorName);
            if (results.isEmpty()) {
                Label empty = new Label("Aucun résultat trouvé pour " + actorName + ".");
                empty.setStyle("-fx-text-fill:#888;-fx-font-size:13px;");
                list.getChildren().add(empty);
            }
            for (Content c : results) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8));
                row.setStyle("-fx-background-color:#1c1c1c;-fx-background-radius:6px;-fx-cursor:hand;");

                ImageView cover = new ImageView();
                cover.setFitWidth(44); cover.setFitHeight(62); cover.setPreserveRatio(false);
                if (c.getCoverUrl() != null && !c.getCoverUrl().isBlank())
                    cover.setImage(new Image(c.getCoverUrl(), 44, 62, false, true, true));

                VBox info = new VBox(3);
                Label tl = new Label(c.getTitle());
                tl.setStyle("-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;");
                Label ml = new Label(c.getReleaseYear() + (c.isFilm() ? "  •  Film" : "  •  Série"));
                ml.setStyle("-fx-text-fill:#888;-fx-font-size:11px;");
                info.getChildren().addAll(tl, ml);
                HBox.setHgrow(info, Priority.ALWAYS);

                row.getChildren().addAll(cover, info);
                row.setOnMouseClicked(e -> { dialog.close(); openDetailPage(c); });
                list.getChildren().add(row);
            }
        } catch (Exception e) {
            list.getChildren().add(new Label("Erreur: " + e.getMessage()));
        }

        ScrollPane sp = new ScrollPane(list);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#111;-fx-background-color:#111;");
        dialog.setScene(new Scene(sp));
        dialog.show();
    }

    private void openDetailPage(Content c) {
        try {
            javafx.fxml.FXMLLoader loader =
                new javafx.fxml.FXMLLoader(getClass().getResource("/ui/Detail.fxml"));
            Parent root = loader.load();
            DetailController ctrl = loader.getController();
            ctrl.setContent(c);
            Stage stage = Navigator.getPrimaryStage();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        } catch (IOException e) { showAlert(e.getMessage()); }
    }

    // ── Trailer (#1) ──────────────────────────────────────────────────────────

    @FXML
    public void onPlayTrailer() {
        String path = content.getTrailerUrl(); // this should point to your local file
        if (path == null || path.isBlank()) return;

        try {
            // Convert file path to URI
            String uri = path.startsWith("http") ? path : new java.io.File(path).toURI().toString();

            Media media = new Media(uri);
            MediaPlayer trailerPlayer = new MediaPlayer(media);
            trailerPlayer.setAutoPlay(true);   // autoplay
            trailerPlayer.setVolume(0.8);      // optional volume

            MediaView trailerView = new MediaView(trailerPlayer);
            trailerView.setFitWidth(896);
            trailerView.setFitHeight(504);
            trailerView.setPreserveRatio(true);

            Button closeBtn = new Button("Fermer");
            closeBtn.getStyleClass().add("btn-secondary");

            VBox root = new VBox(8, trailerView, closeBtn);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(10));
            root.setStyle("-fx-background-color:#111;");

            Stage dialog = new Stage(StageStyle.UTILITY);
            dialog.setTitle("Bande-annonce — " + content.getTitle());
            dialog.initModality(Modality.APPLICATION_MODAL);

            closeBtn.setOnAction(e -> {
                trailerPlayer.stop();
                dialog.close();
            });

            dialog.setOnCloseRequest(e -> trailerPlayer.stop());
            dialog.setScene(new Scene(root));
            dialog.show();

        } catch (Exception ex) {
            showAlert("Impossible de lire la bande-annonce : " + ex.getMessage());
        }
    }

    // ── Media Player ──────────────────────────────────────────────────────────

    private void loadMedia(String url) {
        mediaView.setOnMouseClicked(e -> { if (e.getClickCount() == 2) onFullscreen(); });
        if (mediaPlayer != null) mediaPlayer.dispose();
        try {
            String fixedUrl = url.startsWith("http") ? url : new java.io.File(url).toURI().toString();
            mediaPlayer = new MediaPlayer(new Media(fixedUrl));
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

    private void updateProgressUI(Duration current) {
        if (progressSlider.isValueChanging()) return;
        Duration total = mediaPlayer.getTotalDuration();
        if (total != null && total.greaterThan(Duration.ZERO))
            progressSlider.setValue(current.toSeconds() / total.toSeconds() * 100);
        int sec = (int) current.toSeconds();
        timeLabel.setText(formatTime(sec) + " / " + formatTime((int)(total != null ? total.toSeconds() : 0)));
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
        if (countdownTimeline != null) countdownTimeline.stop();
        countdownOverlay.setVisible(true);
        int[] count = {10};
        countdownLabel.setText("Prochain épisode dans " + count[0] + "s");
        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            count[0]--;
            countdownLabel.setText("Prochain épisode dans " + count[0] + "s");
            if (count[0] <= 0) { countdownOverlay.setVisible(false); playEpisode(next); }
        }));
        countdownTimeline.setCycleCount(10);
        countdownTimeline.play();
    }

    @FXML public void onCancelCountdown() {
        if (countdownTimeline != null) { countdownTimeline.stop(); countdownTimeline = null; }
        countdownOverlay.setVisible(false);
    }

    @FXML public void onPlayPause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause(); playPauseBtn.setText("▶");
        } else {
            mediaPlayer.play();  playPauseBtn.setText("⏸");
        }
    }

    @FXML public void onSeek() {
        if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null)
            mediaPlayer.seek(Duration.seconds(
                progressSlider.getValue() / 100.0 * mediaPlayer.getTotalDuration().toSeconds()));
    }

    @FXML
    private void onFullscreen() {
        if (mediaPlayer == null) return;
        if (fullscreenStage == null) {
            fullscreenStage = new Stage();

            MediaView fsView = new MediaView(mediaPlayer);
            fsView.setPreserveRatio(true);
            fsView.fitWidthProperty().bind(fullscreenStage.widthProperty());
            fsView.fitHeightProperty().bind(fullscreenStage.heightProperty().subtract(56));

            Button fsPlayPause = new Button(
                mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING ? "⏸" : "▶");
            fsPlayPause.getStyleClass().add("btn-player");

            Slider fsProgress = new Slider();
            HBox.setHgrow(fsProgress, Priority.ALWAYS);

            Label fsTime = new Label("00:00 / 00:00");
            fsTime.getStyleClass().add("time-label");

            Slider fsVolume = new Slider(0, 100, volumeSlider.getValue());
            fsVolume.setPrefWidth(100);

            Button fsExit = new Button("✕ Quitter");
            fsExit.getStyleClass().add("btn-player");
            fsExit.setOnAction(e -> exitFullscreen());

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

            fsPlayPause.setOnAction(e -> {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause(); fsPlayPause.setText("▶"); playPauseBtn.setText("▶");
                } else {
                    mediaPlayer.play();  fsPlayPause.setText("⏸"); playPauseBtn.setText("⏸");
                }
            });
            fsProgress.setOnMouseReleased(e -> {
                if (mediaPlayer.getTotalDuration() != null) {
                    mediaPlayer.seek(Duration.seconds(
                        fsProgress.getValue() / 100.0 * mediaPlayer.getTotalDuration().toSeconds()));
                    progressSlider.setValue(fsProgress.getValue());
                }
            });
            fsVolume.valueProperty().addListener((obs, o, n) -> {
                mediaPlayer.setVolume(n.doubleValue() / 100.0);
                volumeSlider.setValue(n.doubleValue());
            });

            HBox controls = new HBox(12, fsPlayPause, fsProgress, fsTime,
                new Label("🔊"), fsVolume, fsExit);
            controls.setAlignment(Pos.CENTER_LEFT);
            controls.setStyle("-fx-background-color:rgba(0,0,0,0.7);-fx-padding:8 16 8 16;");

            BorderPane fsRoot = new BorderPane();
            fsRoot.setStyle("-fx-background-color:black;");
            fsRoot.setCenter(fsView);
            fsRoot.setBottom(controls);
            BorderPane.setAlignment(controls, Pos.CENTER);

            double w = Screen.getPrimary().getBounds().getWidth();
            double h = Screen.getPrimary().getBounds().getHeight();
            Scene fsScene = new Scene(fsRoot, w, h, Color.BLACK);
            if (!rootStack.getScene().getStylesheets().isEmpty())
                fsScene.getStylesheets().addAll(rootStack.getScene().getStylesheets());

            fsScene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ESCAPE) exitFullscreen();
                if (e.getCode() == KeyCode.SPACE)  fsPlayPause.fire();
            });

            fullscreenStage.setScene(fsScene);
            fullscreenStage.setFullScreen(true);
            fullscreenStage.setFullScreenExitHint("");
            fullscreenStage.setOnCloseRequest(e -> exitFullscreen());
        }
        fullscreenStage.show();
        fullscreenStage.toFront();
    }

    @FXML private void exitFullscreen() {
        if (fullscreenStage != null && fullscreenStage.isShowing()) fullscreenStage.hide();
    }

    // ── Seasons / Episodes ────────────────────────────────────────────────────

    private void loadSeasons() {
        try {
            List<Season> seasons = seasonDAO.findBySerieId(content.getId());
            seasonCombo.getItems().addAll(seasons);
            seasonCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> { if (n != null) loadEpisodes(n); });
            if (!seasons.isEmpty()) seasonCombo.getSelectionModel().selectFirst();
        } catch (Exception e) { showAlert("Erreur saisons: " + e.getMessage()); }
    }

    private void loadEpisodes(Season season) {
        episodeListBox.getChildren().clear();
        try {
            List<Episode> episodes = episodeDAO.findBySeasonId(season.getId());

            for (Episode ep : episodes) {
                int uid = Session.getCurrentUser().getId();
                ep.setWatched(watchHistoryDAO.isCompleted(uid, ep.getId()));
                ep.setProgressSec(watchHistoryDAO.getProgressSec(uid, ep.getId()));

                episodeListBox.getChildren().add(buildEpisodeRow(ep));

                if (ep.getId() == resumeEpisodeId) {
                    ep.setProgressSec(resumeEpisodeTime);
                    Platform.runLater(() -> playEpisode(ep));
                }
            }
        } catch (Exception e) {
            showAlert("Erreur épisodes: " + e.getMessage());
        }
    }

    private HBox buildEpisodeRow(Episode ep) {
        HBox row = new HBox(12);
        row.getStyleClass().add("episode-row");
        row.setCursor(javafx.scene.Cursor.HAND);

        Label num = new Label("E" + ep.getEpisodeNum()); num.getStyleClass().add("episode-num");

        VBox info = new VBox(3);
        Label tl = new Label(ep.getTitle()); tl.getStyleClass().add("episode-title");
        Label sl = new Label(ep.getSynopsis() != null ? ep.getSynopsis() : "");
        sl.getStyleClass().add("episode-synopsis"); sl.setWrapText(true);
        Label dl = new Label(ep.getDurationMin() + " min"); dl.getStyleClass().add("episode-duration");
        info.getChildren().addAll(tl, sl, dl);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label status = new Label(ep.isWatched() ? "✅ Vu" : ep.getProgressSec() > 0 ? "▶ En cours" : "");
        status.getStyleClass().add("episode-status");

        row.getChildren().addAll(num, info, status);
        row.setOnMouseEntered(e -> { row.setScaleX(1.02); row.setScaleY(1.02); });
        row.setOnMouseExited(e  -> { row.setScaleX(1);    row.setScaleY(1); });
        row.setOnMouseClicked(e -> playEpisode(ep));
        return row;
    }

    private void playEpisode(Episode ep) {
        if (countdownTimeline != null) { countdownTimeline.stop(); countdownTimeline = null; }
        countdownOverlay.setVisible(false);
        currentEpisode = ep;
        filmPlayerBox.setVisible(true); filmPlayerBox.setManaged(true);
        if (mediaPlayer != null) mediaPlayer.dispose();
        if (fullscreenStage != null) { fullscreenStage.close(); fullscreenStage = null; }

        String url = ep.getVideoUrl();
        if (!url.startsWith("http")) url = new java.io.File(url).toURI().toString();

        mediaPlayer = new MediaPlayer(new Media(url));
        mediaView.setMediaPlayer(mediaPlayer);
        mediaPlayer.setVolume(volumeSlider.getValue() / 100.0);
        int saved = ep.getProgressSec();
        mediaPlayer.setOnReady(() -> {
            if (saved > 0) mediaPlayer.seek(Duration.seconds(saved));
            mediaPlayer.play(); playPauseBtn.setText("⏸");
        });
        mediaPlayer.currentTimeProperty().addListener((obs, o, n) -> updateProgressUI(n));
        mediaPlayer.setOnEndOfMedia(this::onMediaEnd);
    }

    private void saveProgress(Integer episodeId, int progressSec, boolean completed) {
        try { watchHistoryDAO.saveProgress(Session.getCurrentUser().getId(),
            content.getId(), episodeId, progressSec, completed); }
        catch (Exception ignored) {}
    }

    // ── Rating ────────────────────────────────────────────────────────────────

    private void setupRating() {
        starsBox.getChildren().clear();
        try { userRating = ratingDAO.getUserRating(Session.getCurrentUser().getId(), content.getId()); }
        catch (Exception ignored) {}

        for (int i = 1; i <= 5; i++) {
            final int star = i;
            Label lbl = new Label(i <= userRating ? "★" : "☆");
            lbl.getStyleClass().add("star");
            lbl.setStyle("-fx-font-size:28px;-fx-text-fill:" + (i <= userRating ? "#f5c518" : "#888") + ";-fx-cursor:hand;");
            lbl.setOnMouseClicked(e -> onRate(star));
            lbl.setOnMouseEntered(e -> {
                for (int j = 0; j < starsBox.getChildren().size(); j++) {
                    Label s = (Label) starsBox.getChildren().get(j);
                    s.setText(j < star ? "★" : "☆");
                    s.setStyle("-fx-font-size:28px;-fx-text-fill:" + (j < star ? "#f5c518" : "#888") + ";-fx-cursor:hand;");
                }
            });
            starsBox.getChildren().add(lbl);
        }
        starsBox.setOnMouseExited(e -> refreshStars());
    }

    private void onRate(int stars) {
        userRating = stars;
        try {
            ratingDAO.upsertRating(Session.getCurrentUser().getId(), content.getId(), stars);
            contentService.refreshAvgRating(content.getId());
            Content updated = contentService.getById(content.getId());
            ratingLabel.setText("\u2605 " + String.format("%.1f", updated.getAvgRating()));
        } catch (Exception e) { showAlert("Erreur: " + e.getMessage()); }
        refreshStars();
    }

    private void refreshStars() {
        for (int j = 0; j < starsBox.getChildren().size(); j++) {
            Label s = (Label) starsBox.getChildren().get(j);
            s.setText(j < userRating ? "★" : "☆");
            s.setStyle("-fx-font-size:28px;-fx-text-fill:" + (j < userRating ? "#f5c518" : "#888") + ";-fx-cursor:hand;");
        }
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    private void loadComments() {
        commentsBox.getChildren().clear();
        try {
            for (Comment c : commentDAO.findByContentId(content.getId())) {
                VBox cb = new VBox(4); cb.getStyleClass().add("comment-box");
                Label author = new Label(c.getUserName() + " • " +
                    (c.getCreatedAt() != null ? c.getCreatedAt().toLocalDate() : ""));
                author.getStyleClass().add("comment-author");
                Label body = new Label(c.getBody());
                body.setWrapText(true); body.getStyleClass().add("comment-body");
                Button flag = new Button("🚩"); flag.getStyleClass().add("btn-flag");
                flag.setOnAction(e -> { try { commentDAO.flag(c.getId()); loadComments(); } catch (Exception ignored) {} });
                cb.getChildren().addAll(author, body, flag);
                commentsBox.getChildren().add(cb);
            }
        } catch (Exception e) { showAlert("Erreur commentaires: " + e.getMessage()); }
    }

    @FXML public void onPostComment() {
        String text = commentInput.getText().trim();
        if (text.isEmpty()) return;
        Comment c = new Comment();
        c.setUserId(Session.getCurrentUser().getId());
        c.setContentId(content.getId());
        c.setBody(text);
        try { commentDAO.save(c); commentInput.clear(); loadComments(); }
        catch (Exception e) { showAlert("Erreur: " + e.getMessage()); }
    }

    // ── Watchlist ──────────────────────────────────────────────────────────────

    private void setupWatchlist() {
        try { isInWatchlist = watchlistDAO.isInWatchlist(Session.getCurrentUser().getId(), content.getId()); updateWatchlistBtn(); }
        catch (Exception ignored) {}
    }

    @FXML public void onToggleWatchlist() {
        try {
            int uid = Session.getCurrentUser().getId();
            if (isInWatchlist) { watchlistDAO.remove(uid, content.getId()); isInWatchlist = false; }
            else               { watchlistDAO.add(uid, content.getId());    isInWatchlist = true;  }
            updateWatchlistBtn();
        } catch (Exception e) { showAlert("Erreur: " + e.getMessage()); }
    }

    private void updateWatchlistBtn() {
        watchlistBtn.setText(isInWatchlist ? "✓ Ma liste" : "+ Ma liste");
        watchlistBtn.getStyleClass().removeAll("btn-added");
        if (isInWatchlist) watchlistBtn.getStyleClass().add("btn-added");
    }

    // ── Back ──────────────────────────────────────────────────────────────────

    @FXML public void onBack() {
        if (fullscreenStage != null) { fullscreenStage.close(); fullscreenStage = null; }
        if (mediaPlayer != null) {
            if (currentEpisode != null)
                saveProgress(currentEpisode.getId(), (int) mediaPlayer.getCurrentTime().toSeconds(), false);
            mediaPlayer.dispose();
        }
        try { Navigator.navigateTo("/ui/home.fxml"); }
        catch (Exception e) { showAlert(e.getMessage()); }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String formatTime(int s) { return String.format("%02d:%02d", s / 60, s % 60); }
    private void showAlert(String msg) { new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait(); }
}