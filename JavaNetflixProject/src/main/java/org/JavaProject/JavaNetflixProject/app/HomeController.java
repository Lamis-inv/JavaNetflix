package org.JavaProject.JavaNetflixProject.app;

import javafx.animation.*;
import org.JavaProject.JavaNetflixProject.Utils.ThemeManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import org.JavaProject.JavaNetflixProject.DAO.NotificationDAO;
import org.JavaProject.JavaNetflixProject.DAO.UserDAO;
import org.JavaProject.JavaNetflixProject.DAO.WatchHistoryDAO;
import org.JavaProject.JavaNetflixProject.DAO.WatchlistDAO;
import org.JavaProject.JavaNetflixProject.Entities.Category;
import org.JavaProject.JavaNetflixProject.Entities.Content;
import org.JavaProject.JavaNetflixProject.Services.ContentService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import org.JavaProject.JavaNetflixProject.Utils.Session;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController {

    // ── FXML ─────────────────────────────────────────────────────────────────
    @FXML private VBox      categoriesContainer;
    @FXML private Label     usernameLabel;
    @FXML private TextField searchField;

    // Banner
    @FXML private StackPane featuredBanner;
    @FXML private ImageView featuredCover;
    @FXML private Label     featuredTitle;
    @FXML private Label     featuredSynopsis;
    @FXML private Label     featuredMeta;
    @FXML private HBox      bannerDots;
    @FXML private Button    bannerPrev;
    @FXML private Button    bannerNext;

    // Filter bar
    @FXML private HBox      filterBar;
    @FXML private ComboBox<String> filterType;
    @FXML private ComboBox<String> filterGenre;
    @FXML private ComboBox<String> filterYear;

    // Top bar
    @FXML private Button    notifBtn;
    @FXML private Label     notifBadge;
    @FXML private Button    historyBtn;

    // Three-dot menu
    @FXML private Button    threeDotBtn;
    @FXML private VBox      dropdownMenu;
    @FXML private Label     dropdownGreeting;
    @FXML private Label     dropdownUsername;
    @FXML private Button    dropdownDashboard;

    // Hover overlay
    @FXML private Pane      hoverOverlay;
    @FXML private ScrollPane mainScroll;

    // ── State ─────────────────────────────────────────────────────────────────
    private final ContentService  contentService  = new ContentService();
    private final WatchHistoryDAO watchHistoryDAO = new WatchHistoryDAO();
    private final NotificationDAO notifDAO        = new NotificationDAO();
    private final UserDAO         userDAO         = new UserDAO();

    private List<Content> featuredList  = new ArrayList<>();
    private List<Content> allContent    = new ArrayList<>();
    private int           featuredIndex = 0;
    private Timeline      bannerSlider;
    private boolean       dropdownVisible = false;

    // Popup
    private StackPane   activePopup;
    private Timeline    popupHideTimer;
    private Timeline    popupShowTimer;

    // ── Init ──────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        var user = Session.getCurrentUser();

        // Setup dropdown greeting
        dropdownGreeting.setText("Bonjour,");
        dropdownUsername.setText(user.getNom());

        // Show dashboard button only for admins
        if ("ADMIN".equals(user.getRole())) {
            dropdownDashboard.setVisible(true);
            dropdownDashboard.setManaged(true);
        }

        // Close dropdown when clicking anywhere else
        Platform.runLater(() -> {
            setupResponsiveStage();
            if (mainScroll.getScene() != null) {
                mainScroll.getScene().setOnMousePressed(e -> {
                    if (dropdownVisible && !dropdownMenu.getBoundsInParent().contains(e.getX(), e.getY())) {
                        hideDropdown();
                    }
                });
            }
        });

        loadFeatured();
        loadAllContent();
        populateFilterOptions();
        refreshNotifBadge();

        Platform.runLater(() ->
            ThemeManager.setDefaultTheme(Navigator.getPrimaryStage().getScene())
        );
    }

    // ── Responsive Stage ──────────────────────────────────────────────────────
    private void setupResponsiveStage() {
        Stage stage = Navigator.getPrimaryStage();
        if (stage == null) return;

        javafx.geometry.Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setX(screen.getMinX());
        stage.setY(screen.getMinY());
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
        stage.setMaximized(true);
        stage.setResizable(true);
        // Enforce minimum size
        stage.setMinWidth(960);
        stage.setMinHeight(540);

        if (featuredCover != null && featuredBanner != null) {
            featuredCover.fitWidthProperty().bind(featuredBanner.widthProperty());
            featuredCover.fitHeightProperty().bind(featuredBanner.heightProperty());
        }

        // Vertical scroll forwarding
        if (mainScroll != null && stage.getScene() != null) {
            stage.getScene().setOnScroll(e -> {
                if (dropdownVisible) return;
                double delta = e.getDeltaY() / mainScroll.getContent().getBoundsInLocal().getHeight();
                mainScroll.setVvalue(mainScroll.getVvalue() - delta);
            });
        }
    }

    // ── THREE-DOT MENU ────────────────────────────────────────────────────────
    @FXML
    public void onThreeDotMenu() {
        if (dropdownVisible) {
            hideDropdown();
        } else {
            showDropdown();
        }
    }

    private void showDropdown() {
        dropdownVisible = true;
        dropdownMenu.setVisible(true);
        dropdownMenu.setManaged(true);
        dropdownMenu.setOpacity(0);
        dropdownMenu.setTranslateY(-8);

        FadeTransition ft = new FadeTransition(Duration.millis(160), dropdownMenu);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(160), dropdownMenu);
        tt.setToY(0);
        new ParallelTransition(ft, tt).play();
    }

    private void hideDropdown() {
        dropdownVisible = false;
        FadeTransition ft = new FadeTransition(Duration.millis(120), dropdownMenu);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            dropdownMenu.setVisible(false);
            dropdownMenu.setManaged(false);
        });
        ft.play();
    }

    @FXML
    public void onGoToDashboard() {
        hideDropdown();
        dismissPopupNow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/AdminDashboard.fxml"));
            Parent root = loader.load();
            Stage stage = Navigator.getPrimaryStage();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            stage.setTitle("Notflix Admin");
        } catch (IOException e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    @FXML
    public void onHelp() {
        hideDropdown();
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Aide & Support");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(440);
        dialog.setHeight(360);

        VBox box = new VBox(18);
        box.setPadding(new Insets(28));
        box.setStyle("-fx-background-color:#0a0a0a;");

        Label title = new Label("❓  Aide & Support");
        title.setStyle("-fx-text-fill:#e5e5e5;-fx-font-size:18px;-fx-font-weight:bold;");

        String[][] items = {
            {"🔍 Recherche", "Utilisez la barre de recherche pour trouver films et séries."},
            {"♡ Ma Liste", "Ajoutez des contenus à votre liste personnelle."},
            {"⏱ Historique", "Retrouvez tout ce que vous avez regardé."},
            {"🎲 Surprise", "Laissez Notflix choisir pour vous !"},
            {"🔔 Notifications", "Soyez informé des nouvelles sorties."}
        };

        for (String[] item : items) {
            VBox row = new VBox(3);
            Label h = new Label(item[0]);
            h.setStyle("-fx-text-fill:#d0d0d0;-fx-font-size:13px;-fx-font-weight:bold;");
            Label d = new Label(item[1]);
            d.setStyle("-fx-text-fill:#555;-fx-font-size:12px;");
            d.setWrapText(true);
            row.getChildren().addAll(h, d);
            box.getChildren().add(row);
        }

        ScrollPane sp = new ScrollPane(box);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background:#0a0a0a;-fx-background-color:#0a0a0a;");
        dialog.setScene(new Scene(sp));
        dialog.show();
    }

    @FXML
    public void onDeleteAccount() {
        hideDropdown();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer le compte");
        confirm.setHeaderText("Supprimer définitivement votre compte ?");
        confirm.setContentText("Cette action est irréversible. Toutes vos données seront perdues.");
        confirm.getDialogPane().setStyle("-fx-background-color:#111;");

        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                try {
                    int uid = Session.getCurrentUser().getId();
                    userDAO.delete(uid);
                    Session.logout();
                    Navigator.navigateTo("/ui/LoginPage.fxml", 1100, 700);
                } catch (Exception e) {
                    showAlert("Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    // ── Featured Banner ───────────────────────────────────────────────────────
    private void loadFeatured() {
        try {
            featuredList = contentService.getFeaturedContent();
            if (featuredList == null || featuredList.isEmpty()) return;
            buildBannerDots();
            applyBannerContent(featuredList.get(0));
            startBannerSlider();
            featuredBanner.setOnMouseClicked(e -> openDetail(featuredList.get(featuredIndex)));
            featuredBanner.setCursor(javafx.scene.Cursor.HAND);
        } catch (SQLException e) {
            showAlert("Erreur bandeau: " + e.getMessage());
        }
    }

    private void buildBannerDots() {
        bannerDots.getChildren().clear();
        for (int i = 0; i < featuredList.size(); i++) {
            javafx.scene.shape.Rectangle dot =
                new javafx.scene.shape.Rectangle(i == featuredIndex ? 22 : 7, 4);
            dot.setArcWidth(4); dot.setArcHeight(4);
            dot.getStyleClass().add(i == featuredIndex ? "banner-dot-active" : "banner-dot");
            final int idx = i;
            dot.setOnMouseClicked(e -> { e.consume(); goToBanner(idx); });
            bannerDots.getChildren().add(dot);
        }
    }

    private void updateFeaturedBanner(boolean animate) {
        Content c = featuredList.get(featuredIndex);
        if (animate) {
            FadeTransition out = new FadeTransition(Duration.millis(220), featuredBanner);
            out.setFromValue(1); out.setToValue(0);
            out.setOnFinished(ev -> {
                applyBannerContent(c);
                FadeTransition in = new FadeTransition(Duration.millis(220), featuredBanner);
                in.setFromValue(0); in.setToValue(1);
                in.play();
            });
            out.play();
        } else {
            applyBannerContent(c);
        }
        buildBannerDots();
    }

    private void applyBannerContent(Content c) {
        featuredTitle.setText(c.getTitle());
        featuredMeta.setText(
            c.getReleaseYear() +
            (c.getDurationMin() > 0 ? "  \u2022  " + c.getDurationMin() + " min" : "") +
            (c.getCategory() != null ? "  \u2022  " + c.getCategory().getName() : "")
        );
        featuredSynopsis.setText(
            c.getSynopsis() != null && !c.getSynopsis().isBlank()
                ? c.getSynopsis().substring(0, Math.min(180, c.getSynopsis().length())) + "\u2026"
                : ""
        );
        if (c.getCoverUrl() != null && !c.getCoverUrl().isBlank()) {
            try { featuredCover.setImage(new Image(c.getCoverUrl(), true)); }
            catch (Exception ignored) {}
        }
    }

    private void startBannerSlider() {
        if (bannerSlider != null) bannerSlider.stop();
        bannerSlider = new Timeline(new KeyFrame(Duration.seconds(6),
            e -> goToBanner((featuredIndex + 1) % featuredList.size())));
        bannerSlider.setCycleCount(Timeline.INDEFINITE);
        bannerSlider.play();
    }

    private void goToBanner(int idx) {
        featuredIndex = idx;
        updateFeaturedBanner(true);
        if (bannerSlider != null) { bannerSlider.stop(); bannerSlider.play(); }
    }

    @FXML public void onBannerPrev() {
        if (featuredList.isEmpty()) return;
        goToBanner((featuredIndex - 1 + featuredList.size()) % featuredList.size());
    }
    @FXML public void onBannerNext() {
        if (featuredList.isEmpty()) return;
        goToBanner((featuredIndex + 1) % featuredList.size());
    }

    // ── Content loading & filtering ───────────────────────────────────────────
    private void loadAllContent() {
        try {
            allContent.clear();
            List<Category> categories = contentService.getAllCategories();
            for (Category cat : categories) {
                List<Content> items = contentService.getByCategory(cat.getId());
                if (items != null) allContent.addAll(items);
            }
            renderContent(allContent);
        } catch (SQLException e) {
            showAlert("Erreur chargement: " + e.getMessage());
        }
    }

    private void populateFilterOptions() {
        filterType.getItems().setAll("Tous", "Films", "Séries");
        filterType.getSelectionModel().selectFirst();

        List<String> years = allContent.stream()
            .map(c -> String.valueOf(c.getReleaseYear()))
            .filter(y -> !"0".equals(y))
            .distinct().sorted(java.util.Comparator.reverseOrder())
            .collect(Collectors.toList());
        years.add(0, "Toutes années");
        filterYear.getItems().setAll(years);
        filterYear.getSelectionModel().selectFirst();

        try {
            List<String> genres = contentService.getAllCategories()
                .stream().map(Category::getName).collect(Collectors.toList());
            genres.add(0, "Tous genres");
            filterGenre.getItems().setAll(genres);
            filterGenre.getSelectionModel().selectFirst();
        } catch (Exception ignored) {}

        filterType.setOnAction(e -> applyFilters());
        filterGenre.setOnAction(e -> applyFilters());
        filterYear.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        String type  = filterType.getValue();
        String genre = filterGenre.getValue();
        String year  = filterYear.getValue();

        List<Content> filtered = allContent.stream()
            .filter(c -> {
                if ("Films".equals(type) && !c.isFilm()) return false;
                if ("Séries".equals(type) && c.isFilm()) return false;
                if (genre != null && !"Tous genres".equals(genre)
                    && (c.getCategory() == null || !genre.equals(c.getCategory().getName())))
                    return false;
                if (year != null && !"Toutes années".equals(year)
                    && !year.equals(String.valueOf(c.getReleaseYear())))
                    return false;
                return true;
            })
            .collect(Collectors.toList());

        Scene scene = Navigator.getPrimaryStage().getScene();
        if (genre == null || genre.equals("Tous genres")) ThemeManager.setDefaultTheme(scene);
        else ThemeManager.setThemeByGenre(genre, scene);

        renderContent(filtered);
    }

    /**
     * Renders content as wrapped FlowPane rows per category — NO horizontal scrollbars.
     * Cards wrap to next line on resize.
     */
    private void renderContent(List<Content> items) {
        categoriesContainer.getChildren().clear();

        java.util.Map<String, List<Content>> byCategory = new java.util.LinkedHashMap<>();
        for (Content c : items) {
            String key = c.getCategory() != null ? c.getCategory().getName() : "Autres";
            byCategory.computeIfAbsent(key, k -> new ArrayList<>()).add(c);
        }

        for (java.util.Map.Entry<String, List<Content>> entry : byCategory.entrySet()) {
            Label catLabel = new Label(entry.getKey());
            catLabel.getStyleClass().add("category-title");

            // FlowPane wraps cards — no horizontal scroll needed
            FlowPane flow = new FlowPane();
            flow.setHgap(14);
            flow.setVgap(14);
            flow.setPadding(new Insets(4, 0, 4, 0));
            flow.setPrefWrapLength(Double.MAX_VALUE);

            for (Content c : entry.getValue()) {
                flow.getChildren().add(buildCard(c));
            }

            VBox section = new VBox(10, catLabel, flow);
            section.getStyleClass().add("category-section");
            categoriesContainer.getChildren().add(section);
        }
    }

    // ── Card + Hover Popup ────────────────────────────────────────────────────
    private StackPane buildCard(Content c) {
        ImageView img = new ImageView();
        img.setFitWidth(155); img.setFitHeight(225);
        img.setPreserveRatio(false);
        if (c.getCoverUrl() != null && !c.getCoverUrl().isBlank()) {
            try {
                img.setImage(new Image(c.getCoverUrl(), 155, 225, false, true, true));
            } catch (Exception ignored) {}
        }
        Label titleLbl = new Label(c.getTitle());
        titleLbl.getStyleClass().add("card-title");
        titleLbl.setWrapText(true);
        titleLbl.setMaxWidth(145);
        StackPane.setAlignment(titleLbl, Pos.BOTTOM_LEFT);
        StackPane.setMargin(titleLbl, new Insets(0, 0, 8, 8));

        StackPane card = new StackPane(img, titleLbl);
        card.getStyleClass().add("content-card");
        card.setPrefWidth(155); card.setPrefHeight(225);

        card.setOnMouseEntered(e -> schedulePopup(card, c));
        card.setOnMouseExited(e -> schedulePopupHide());
        card.setOnMouseClicked(e -> { e.consume(); openDetail(c); });
        card.setCursor(javafx.scene.Cursor.HAND);
        return card;
    }

    private void schedulePopup(StackPane card, Content c) {
        cancelPopupHide();
        if (popupShowTimer != null) popupShowTimer.stop();
        if (activePopup != null) dismissPopupNow();

        popupShowTimer = new Timeline(new KeyFrame(Duration.millis(450),
            ev -> showPopupFor(card, c)));
        popupShowTimer.play();
    }

    private void showPopupFor(StackPane card, Content c) {
        if (activePopup != null) dismissPopupNow();

        StackPane popup = buildHoverPopup(c);
        activePopup = popup;
        hoverOverlay.setMouseTransparent(false);
        hoverOverlay.getChildren().add(popup);

        Platform.runLater(() -> {
            Bounds b  = card.localToScene(card.getBoundsInLocal());
            Bounds ob = hoverOverlay.localToScene(hoverOverlay.getBoundsInLocal());

            double pw = 255, ph = 320;
            double x = (b.getMinX() - ob.getMinX()) + (b.getWidth() - pw) / 2.0;
            double y = (b.getMinY() - ob.getMinY()) - 50;

            x = Math.max(8, Math.min(x, hoverOverlay.getWidth()  - pw - 8));
            y = Math.max(8, Math.min(y, hoverOverlay.getHeight() - ph - 8));

            popup.setLayoutX(x);
            popup.setLayoutY(y);

            popup.setOpacity(0); popup.setScaleX(0.93); popup.setScaleY(0.93);
            FadeTransition ft = new FadeTransition(Duration.millis(150), popup);
            ft.setToValue(1);
            ScaleTransition st = new ScaleTransition(Duration.millis(150), popup);
            st.setToX(1); st.setToY(1);
            new ParallelTransition(ft, st).play();
        });

        popup.setOnMouseEntered(e -> cancelPopupHide());
        popup.setOnMouseExited(e  -> schedulePopupHide());
        popup.setOnMouseClicked(e -> { e.consume(); openDetail(c); });
        popup.setCursor(javafx.scene.Cursor.HAND);
    }

    private StackPane buildHoverPopup(Content c) {
        StackPane preview = new StackPane();
        preview.setPrefHeight(148);
        preview.setMaxHeight(148);

        if (c.getTrailerUrl() != null && !c.getTrailerUrl().isBlank()) {
            try {
                Media media = new Media(new File(c.getTrailerUrl()).toURI().toString());
                MediaPlayer player = new MediaPlayer(media);
                player.setMute(true);
                player.setAutoPlay(true);
                MediaView mediaView = new MediaView(player);
                mediaView.setFitWidth(255); mediaView.setFitHeight(148);
                mediaView.setPreserveRatio(false);
                preview.getChildren().add(mediaView);
            } catch (Exception ex) {
                addThumbToPreview(preview, c, 255, 148);
            }
        } else {
            addThumbToPreview(preview, c, 255, 148);
        }

        Label titleLbl = new Label(c.getTitle());
        titleLbl.getStyleClass().add("popup-title");
        titleLbl.setWrapText(true);

        int fullStars = (int) c.getAvgRating();
        HBox stars = new HBox(3);
        for (int i = 1; i <= 5; i++) {
            Label s = new Label(i <= fullStars ? "\u2605" : "\u2606");
            s.setStyle("-fx-text-fill:#f5c518;-fx-font-size:13px;");
            stars.getChildren().add(s);
        }
        Label ratingVal = new Label(String.format("%.1f", c.getAvgRating()));
        ratingVal.getStyleClass().add("popup-rating");
        HBox ratingRow = new HBox(6, stars, ratingVal);
        ratingRow.setAlignment(Pos.CENTER_LEFT);

        Label meta = new Label(
            c.getReleaseYear() +
            (c.getDurationMin() > 0 ? "  \u2022  " + c.getDurationMin() + " min" : "") +
            (c.getCategory() != null ? "  \u2022  " + c.getCategory().getName() : "")
        );
        meta.getStyleClass().add("popup-meta");

        // Watchlist button
        boolean[] inList = {false};
        try { inList[0] = new WatchlistDAO().isInWatchlist(Session.getCurrentUser().getId(), c.getId()); }
        catch (Exception ignored) {}

        Button wlBtn = new Button(inList[0] ? "\u2713 Ma liste" : "+ Ma liste");
        wlBtn.getStyleClass().add("popup-watchlist-btn");
        if (inList[0]) wlBtn.getStyleClass().add("btn-added");
        wlBtn.setOnAction(e -> {
            e.consume();
            try {
                WatchlistDAO dao = new WatchlistDAO();
                int uid = Session.getCurrentUser().getId();
                if (inList[0]) {
                    dao.remove(uid, c.getId()); inList[0] = false;
                    wlBtn.setText("+ Ma liste"); wlBtn.getStyleClass().remove("btn-added");
                } else {
                    dao.add(uid, c.getId()); inList[0] = true;
                    wlBtn.setText("\u2713 Ma liste"); wlBtn.getStyleClass().add("btn-added");
                }
            } catch (Exception ignored) {}
        });

        Button playBtn = new Button("\u25B6 Voir");
        playBtn.getStyleClass().add("popup-play-btn");
        playBtn.setOnAction(e -> { e.consume(); openDetail(c); });

        // ── SHARE BUTTON ──
        Button shareBtn = new Button("↗ Partager");
        shareBtn.getStyleClass().add("popup-share-btn");
        shareBtn.setOnAction(e -> {
            e.consume();
            shareContent(c);
        });

        HBox actions = new HBox(6, playBtn, wlBtn, shareBtn);
        actions.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(6, titleLbl, ratingRow, meta, actions);
        info.getStyleClass().add("popup-info");
        info.setPadding(new Insets(10, 12, 12, 12));

        VBox content = new VBox(preview, info);
        content.getStyleClass().add("hover-popup");

        StackPane popup = new StackPane(content);
        popup.setPickOnBounds(false);
        popup.setPrefWidth(255);
        popup.setMaxWidth(255);
        return popup;
    }

    private void addThumbToPreview(StackPane preview, Content c, int w, int h) {
        if (c.getCoverUrl() != null && !c.getCoverUrl().isBlank()) {
            try {
                ImageView thumb = new ImageView(new Image(c.getCoverUrl(), w, h, false, true, true));
                thumb.setPreserveRatio(false);
                preview.getChildren().add(thumb);
            } catch (Exception ignored) {}
        }
    }

    // ── Share content ─────────────────────────────────────────────────────────
    private void shareContent(Content c) {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Partager — " + c.getTitle());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(380);
        dialog.setHeight(220);

        VBox box = new VBox(16);
        box.setPadding(new Insets(24));
        box.setStyle("-fx-background-color:#111;");

        Label title = new Label("↗  Partager \"" + c.getTitle() + "\"");
        title.setStyle("-fx-text-fill:#e5e5e5;-fx-font-size:15px;-fx-font-weight:bold;");

        // Share link (simulated deep link)
        String shareUrl = "notflix://watch/" + c.getId() + "/" +
            c.getTitle().toLowerCase().replaceAll("\\s+", "-");

        HBox linkRow = new HBox(8);
        TextField linkField = new TextField(shareUrl);
        linkField.setEditable(false);
        linkField.setStyle("-fx-background-color:#1a1a1a;-fx-text-fill:#888;-fx-border-color:#2a2a2a;-fx-border-radius:6px;-fx-background-radius:6px;-fx-padding:8 10;-fx-font-size:11px;");
        HBox.setHgrow(linkField, Priority.ALWAYS);

        Button copyBtn = new Button("Copier");
        copyBtn.setStyle("-fx-background-color:#e5e5e5;-fx-text-fill:#000;-fx-font-weight:bold;-fx-background-radius:6px;-fx-padding:8 14;-fx-cursor:hand;");
        copyBtn.setOnAction(e -> {
            ClipboardContent cc = new ClipboardContent();
            cc.putString(shareUrl);
            Clipboard.getSystemClipboard().setContent(cc);
            copyBtn.setText("✓ Copié!");
            copyBtn.setStyle("-fx-background-color:#22c55e;-fx-text-fill:#fff;-fx-font-weight:bold;-fx-background-radius:6px;-fx-padding:8 14;-fx-cursor:hand;");
            new Timeline(new KeyFrame(Duration.seconds(1.5), ev -> {
                copyBtn.setText("Copier");
                copyBtn.setStyle("-fx-background-color:#e5e5e5;-fx-text-fill:#000;-fx-font-weight:bold;-fx-background-radius:6px;-fx-padding:8 14;-fx-cursor:hand;");
            })).play();
        });

        linkRow.getChildren().addAll(linkField, copyBtn);

        Label hint = new Label("Partagez ce lien avec vos amis pour qu'ils regardent aussi !");
        hint.setStyle("-fx-text-fill:#404040;-fx-font-size:11px;");
        hint.setWrapText(true);

        box.getChildren().addAll(title, linkRow, hint);
        dialog.setScene(new Scene(box));
        dialog.show();
    }

    @FXML
    public void onRandomPlay() {
        try {
            Content random = contentService.getRandomContent();
            if (random != null) openDetail(random);
        } catch (Exception e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    private void schedulePopupHide() {
        cancelPopupHide();
        popupHideTimer = new Timeline(new KeyFrame(Duration.millis(280), e -> dismissPopupNow()));
        popupHideTimer.play();
    }

    private void cancelPopupHide() {
        if (popupHideTimer != null) { popupHideTimer.stop(); popupHideTimer = null; }
    }

    private void dismissPopupNow() {
        if (popupShowTimer != null) { popupShowTimer.stop(); popupShowTimer = null; }
        if (activePopup != null) {
            FadeTransition ft = new FadeTransition(Duration.millis(110), activePopup);
            ft.setToValue(0);
            StackPane p = activePopup;
            ft.setOnFinished(e -> hoverOverlay.getChildren().remove(p));
            ft.play();
            activePopup = null;
            if (hoverOverlay.getChildren().isEmpty())
                hoverOverlay.setMouseTransparent(true);
        }
    }

    // ── Notifications ─────────────────────────────────────────────────────────
    private void refreshNotifBadge() {
        int count = notifDAO.countUnseen();
        notifBadge.setText(count > 0 ? String.valueOf(Math.min(count, 99)) : "");
        notifBadge.setVisible(count > 0);
        notifBadge.setManaged(count > 0);
    }

    @FXML
    public void onNotifications() {
        notifDAO.markAllSeen();
        refreshNotifBadge();

        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Notifications");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(400); dialog.setHeight(480);

        VBox list = new VBox(8);
        list.setPadding(new Insets(12));
        list.setStyle("-fx-background-color:#111;");

        try {
            List<NotificationDAO.Notification> notifs = notifDAO.getRecent();
            if (notifs.isEmpty()) {
                Label empty = new Label("Aucune notification récente.");
                empty.setStyle("-fx-text-fill:#888;-fx-font-size:13px;");
                list.getChildren().add(empty);
            }
            for (NotificationDAO.Notification n : notifs) {
                HBox row = new HBox(10);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(6, 8, 6, 8));
                row.setStyle("-fx-background-color:#1c1c1c;-fx-background-radius:6px;-fx-cursor:hand;");
                ImageView cover = new ImageView();
                cover.setFitWidth(40); cover.setFitHeight(56);
                if (n.coverUrl != null && !n.coverUrl.isBlank())
                    cover.setImage(new Image(n.coverUrl, 40, 56, false, true, true));
                Label text = new Label("Nouveau " + (n.isFilm ? "film" : "série") + " : " + n.title);
                text.setStyle("-fx-text-fill:#eee;-fx-font-size:12px;");
                text.setWrapText(true);
                row.getChildren().addAll(cover, text);
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

    // ── History ───────────────────────────────────────────────────────────────
    @FXML
    public void onHistory() {
        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Historique");
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setWidth(600); dialog.setHeight(520);

        VBox list = new VBox(10);
        list.setPadding(new Insets(14));
        list.setStyle("-fx-background-color:#111;");

        try {
            List<Content> history = watchHistoryDAO.getHistory(Session.getCurrentUser().getId());
            if (history.isEmpty()) {
                Label empty = new Label("Vous n'avez encore rien regardé.");
                empty.setStyle("-fx-text-fill:#888;-fx-font-size:13px;");
                list.getChildren().add(empty);
            }
            for (Content c : history) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8));
                row.setStyle("-fx-background-color:#1c1c1c;-fx-background-radius:6px;-fx-cursor:hand;");
                ImageView cover = new ImageView();
                cover.setFitWidth(50); cover.setFitHeight(70);
                if (c.getCoverUrl() != null)
                    cover.setImage(new Image(c.getCoverUrl(), 50, 70, false, true, true));
                VBox info = new VBox(4);
                Label tl = new Label(c.getTitle());
                tl.setStyle("-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:bold;");
                Label ml = new Label(c.getReleaseYear() +
                    (c.getDurationMin() > 0 ? "  \u2022  " + c.getDurationMin() + " min" : ""));
                ml.setStyle("-fx-text-fill:#888;-fx-font-size:11px;");
                info.getChildren().addAll(tl, ml);
                HBox.setHgrow(info, Priority.ALWAYS);
                row.getChildren().addAll(cover, info);
                row.setOnMouseClicked(e -> { dialog.close(); openDetail(c); });
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

    // ── Search ────────────────────────────────────────────────────────────────
    @FXML
    public void onSearch() {
        dismissPopupNow();
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { renderContent(allContent); return; }
        try {
            List<Content> results = contentService.search(kw);
            categoriesContainer.getChildren().clear();
            Label title = new Label("Résultats pour : \"" + kw + "\"");
            title.getStyleClass().add("category-title");
            FlowPane flow = new FlowPane();
            flow.setHgap(14); flow.setVgap(14);
            for (Content c : results) flow.getChildren().add(buildCard(c));
            VBox box = new VBox(10, title, flow);
            categoriesContainer.getChildren().add(box);
        } catch (SQLException e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    @FXML public void onClearSearch() { searchField.clear(); renderContent(allContent); }

    // ── Navigation ────────────────────────────────────────────────────────────
    @FXML public void onMyList() {
        try { Navigator.navigateTo("/ui/MyList.fxml"); }
        catch (Exception e) { showAlert(e.getMessage()); }
    }

    @FXML public void onLogout() {
        hideDropdown();
        Session.logout();
        try { Navigator.navigateTo("/ui/LoginPage.fxml"); }
        catch (Exception e) { showAlert(e.getMessage()); }
    }

    private void openDetail(Content c) {
        dismissPopupNow();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/Detail.fxml"));
            Parent root = loader.load();
            DetailController ctrl = loader.getController();
            ctrl.setContent(c);
            Stage stage = Navigator.getPrimaryStage();
            stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
        } catch (IOException e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}