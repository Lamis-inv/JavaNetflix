package org.JavaProject.JavaNetflixProject.app;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.JavaProject.JavaNetflixProject.DAO.*;
import org.JavaProject.JavaNetflixProject.Entities.*;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import org.JavaProject.JavaNetflixProject.Utils.Session;

public class AdminDashboardController implements Initializable {

    // ── Sidebar & Topbar ──
    @FXML private VBox sidebar;
    @FXML private Label adminNameLabel, avatarInitials, flaggedBadge, clockLabel, clockLabel2;
    @FXML private Label pageTitle, pageBreadcrumb;
    @FXML private TextField globalSearch;
    @FXML private Label notifBadge;

    // ── Stack ──
    @FXML private StackPane contentStack;
    @FXML private ScrollPane dashboardPane, analyticsPane;
    @FXML private VBox filmsPane, seriesPane, categoriesPane, usersPane, commentsPane, flaggedPane;

    // ── KPIs ──
    @FXML private Label kpiFilms, kpiSeries, kpiUsers, kpiFlagged, kpiViews;

    // ── Dashboard Charts ──
    @FXML private StackPane top5ChartContainer, categoryChartContainer, registrationsChartContainer;
    @FXML private VBox activityFeed;

    // ── Recent Content Table ──
    @FXML private TableView<Content> recentContentTable;
    @FXML private TableColumn<Content, String> colRCTitle, colRCType, colRCGenre, colRCViews, colRCRating;

    // ── Films ──
    @FXML private TableView<Content> filmsTable;
    @FXML private TableColumn<Content, String> colFilmTitle, colFilmCategory, colFilmYear,
            colFilmViews, colFilmRating, colFilmFeatured;
    @FXML private TableColumn<Content, Void> colFilmActions;
    @FXML private TextField filmSearchField;
    @FXML private ComboBox<String> filmGenreFilter;
    @FXML private Label filmCountLabel;

    // ── Series ──
    @FXML private ListView<Content> seriesList;
    @FXML private TreeTableView<EpisodeItem> episodesTree;
    @FXML private TreeTableColumn<EpisodeItem, String> colEpTitle, colEpNum, colEpDuration;
    @FXML private Label selectedSeriesLabel, seriesCountLabel;
    @FXML private TreeTableColumn<EpisodeItem, Void> colEpActions;

    // ── Categories ──
    @FXML private TableView<Category> categoriesTable;
    @FXML private TableColumn<Category, String> colCatId, colCatName, colCatFilms;
    @FXML private TableColumn<Category, Void> colCatActions;
    @FXML private StackPane catMiniChartContainer;

    // ── Users ──
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> colUserName, colUserEmail, colUserRole, colUserJoined;
    @FXML private TableColumn<User, Void> colUserActions;
    @FXML private TextField userSearchField;
    @FXML private ComboBox<String> userRoleFilter;
    @FXML private Label userCountLabel;

    // ── Comments ──
    @FXML private TableView<Comment> commentsTable;
    @FXML private TableColumn<Comment, String> colComUser, colComBody, colComDate, colComFlagged;
    @FXML private TableColumn<Comment, Void> colComActions;
    @FXML private TextField commentSearchField;
    @FXML private ComboBox<String> commentFilterCombo;
    @FXML private Label commentCountLabel;

    // ── Flagged ──
    @FXML private TableView<Comment> flaggedTable;
    @FXML private TableColumn<Comment, String> colFlagUser, colFlagBody, colFlagDate;
    @FXML private TableColumn<Comment, Void> colFlagActions;
    @FXML private Label flaggedCountLabel;

    // ── Analytics ──
    @FXML private StackPane analyticsTop5, analyticsPie, analyticsReg;
    @FXML private Label analyticsFilms, analyticsSeries, analyticsUsers, analyticsComments, analyticsViews;

    // ── Nav Buttons ──
    @FXML private Button navDashboard, navAnalytics, navFilms, navSeries,
            navCategories, navUsers, navComments, navFlagged;

    // ── DAOs ──
    private final ContentDAO contentDAO = new ContentDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final UserDAO userDAO = new UserDAO();
    private final CommentDAO commentDAO = new CommentDAO();
    private final SeasonDAO seasonDAO = new SeasonDAO();
    private final EpisodeDAO episodeDAO = new EpisodeDAO();

    // ── Observable Data ──
    private final ObservableList<Content> allContent = FXCollections.observableArrayList();
    private final ObservableList<Content> allFilms = FXCollections.observableArrayList();
    private final ObservableList<Content> allSeries = FXCollections.observableArrayList();
    private final ObservableList<Category> allCategories = FXCollections.observableArrayList();
    private final ObservableList<User> allUsers = FXCollections.observableArrayList();
    private final ObservableList<Comment> allComments = FXCollections.observableArrayList();

    private Timeline clock;
    private List<Button> navButtons;

    // ═══════════════════════════════════════════════════════
    // INNER CLASS: Tree node for Season/Episode hierarchy
    // ═══════════════════════════════════════════════════════
    public static class EpisodeItem {
        private final String label;
        private final String num;
        private final String duration;
        private final boolean isSeason;
        private final int id;

        public EpisodeItem(String label, String num, String duration, boolean isSeason, int id) {
            this.label = label; this.num = num; this.duration = duration;
            this.isSeason = isSeason; this.id = id;
        }

        public String getLabel() { return label; }
        public String getNum() { return num; }
        public String getDuration() { return duration; }
        public boolean isSeason() { return isSeason; }
        public int getId() { return id; }
    }

    // ═══════════════════════════════════════════════════════
    // INITIALIZE
    // ═══════════════════════════════════════════════════════
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        navButtons = Arrays.asList(navDashboard, navAnalytics, navFilms,
                navSeries, navCategories, navUsers, navComments, navFlagged);

        startClock();
        setupCombos();
        setupAllColumns();
        setupSeriesListCell();

        // Set user info from session
        User u = Session.getCurrentUser();
        if (u != null) {
            adminNameLabel.setText(u.getNom());
            String initials = initials(u.getNom());
            avatarInitials.setText(initials);
        }

        Platform.runLater(() -> {
            loadAllData();
            setActive(navDashboard, dashboardPane, "Dashboard");
        });
    }

    // ═══════════════════════════════════════════════════════
    // CLOCK
    // ═══════════════════════════════════════════════════════
    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm  dd/MM");
        clock = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String t = LocalDateTime.now().format(fmt);
            if (clockLabel != null) clockLabel.setText(t);
            if (clockLabel2 != null) clockLabel2.setText(t);
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    // ═══════════════════════════════════════════════════════
    // COMBOS
    // ═══════════════════════════════════════════════════════
    private void setupCombos() {
        userRoleFilter.setItems(FXCollections.observableArrayList("All Roles", "ADMIN", "USER"));
        commentFilterCombo.setItems(FXCollections.observableArrayList(
                "All Comments", "Flagged Only", "Normal"));
    }

    // ═══════════════════════════════════════════════════════
    // TABLE COLUMNS
    // ═══════════════════════════════════════════════════════
    private void setupAllColumns() {
        // ── Recent Content ──
        colRCTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        colRCType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getType().name()));
        colRCGenre.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCategory() != null ? d.getValue().getCategory().getName() : "—"));
        colRCViews.setCellValueFactory(d -> new SimpleStringProperty(
                formatNumber(d.getValue().getViewCount())));
        colRCRating.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getAvgRating() > 0 ? String.format("%.1f ★", d.getValue().getAvgRating()) : "—"));

        // ── Films Table ──
        colFilmTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTitle()));
        colFilmCategory.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCategory() != null ? d.getValue().getCategory().getName() : "—"));
        colFilmYear.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getReleaseYear() > 0 ? String.valueOf(d.getValue().getReleaseYear()) : "—"));
        colFilmViews.setCellValueFactory(d -> new SimpleStringProperty(formatNumber(d.getValue().getViewCount())));
        colFilmRating.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getAvgRating() > 0 ? String.format("%.1f ★", d.getValue().getAvgRating()) : "—"));
        colFilmFeatured.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().isFeatured() ? "★ Featured" : "—"));
        colFilmFeatured.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setGraphic(null); return; }
                Label lbl = new Label(item);
                if ("★ Featured".equals(item)) lbl.setStyle("-fx-text-fill:#f5c842;-fx-font-size:11px;-fx-font-weight:bold;");
                else lbl.setStyle("-fx-text-fill:#3a3a4e;-fx-font-size:11px;");
                setGraphic(lbl); setText(null);
            }
        });
        colFilmActions.setCellFactory(col -> new TableCell<>() {
            private final Button edit = new Button("✏");
            private final Button del = new Button("🗑");
            private final HBox box = new HBox(6, edit, del);
            {
                edit.getStyleClass().addAll("btn-secondary", "btn-sm");
                del.getStyleClass().addAll("btn-danger", "btn-sm");
                edit.setOnAction(e -> editContent(getTableView().getItems().get(getIndex())));
                del.setOnAction(e -> deleteContent(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        // ── Categories ──
        colCatId.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getId())));
        colCatName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCatFilms.setCellValueFactory(d -> {
            long c = allContent.stream()
                    .filter(cnt -> cnt.getCategory() != null && cnt.getCategory().getId() == d.getValue().getId())
                    .count();
            return new SimpleStringProperty(String.valueOf(c));
        });
        colCatActions.setCellFactory(col -> new TableCell<>() {
            private final Button edit = new Button("✏ Rename");
            private final Button del = new Button("🗑");
            private final HBox box = new HBox(6, edit, del);
            {
                edit.getStyleClass().addAll("btn-secondary", "btn-sm");
                del.getStyleClass().addAll("btn-danger", "btn-sm");
                edit.setOnAction(e -> editCategory(getTableView().getItems().get(getIndex())));
                del.setOnAction(e -> deleteCategory(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        // ── Users ──
        colUserName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNom()));
        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUserRole.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRole()));
        colUserRole.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label lbl = new Label(item);
                lbl.getStyleClass().add("ADMIN".equals(item) ? "badge-admin" : "badge-user-role");
                setGraphic(lbl); setText(null);
            }
        });
        colUserJoined.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCreatedAt() != null
                        ? d.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "—"));
        colUserActions.setCellFactory(col -> new TableCell<>() {
            private final Button del = new Button("🗑 Delete");
            private final Button prom = new Button("↑ Promote");
            private final HBox box = new HBox(6, prom, del);
            {
                del.getStyleClass().addAll("btn-danger", "btn-sm");
                prom.getStyleClass().addAll("btn-secondary", "btn-sm");
                del.setOnAction(e -> deleteUser(getTableView().getItems().get(getIndex())));
                prom.setOnAction(e -> promoteUser(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                User u = getTableView().getItems().get(getIndex());
                prom.setVisible(!"ADMIN".equals(u.getRole()));
                setGraphic(box);
            }
        });

        // ── Comments ──
        colComUser.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUserName()));
        colComBody.setCellValueFactory(d -> {
            String b = d.getValue().getBody();
            return new SimpleStringProperty(b != null && b.length() > 80 ? b.substring(0, 80) + "…" : b);
        });
        colComDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCreatedAt() != null
                        ? d.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "—"));
        colComFlagged.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().isFlagged() ? "Flagged" : "OK"));
        colComFlagged.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label lbl = new Label(item);
                lbl.getStyleClass().add("Flagged".equals(item) ? "badge-flagged" : "badge-ok");
                setGraphic(lbl); setText(null);
            }
        });
        colComActions.setCellFactory(col -> new TableCell<>() {
            private final Button del = new Button("🗑");
            private final Button flag = new Button("⚑ Flag");
            private final HBox box = new HBox(6, flag, del);
            {
                del.getStyleClass().addAll("btn-danger", "btn-sm");
                flag.getStyleClass().addAll("btn-secondary", "btn-sm");
                del.setOnAction(e -> deleteComment(getTableView().getItems().get(getIndex())));
                flag.setOnAction(e -> flagComment(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        // ── Flagged ──
        colFlagUser.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUserName()));
        colFlagBody.setCellValueFactory(d -> {
            String b = d.getValue().getBody();
            return new SimpleStringProperty(b != null && b.length() > 90 ? b.substring(0, 90) + "…" : b);
        });
        colFlagDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getCreatedAt() != null
                        ? d.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "—"));
        colFlagActions.setCellFactory(col -> new TableCell<>() {
            private final Button del = new Button("🗑 Delete");
            private final Button approve = new Button("✓ Approve");
            private final HBox box = new HBox(6, approve, del);
            {
                del.getStyleClass().addAll("btn-danger", "btn-sm");
                approve.getStyleClass().addAll("btn-success", "btn-sm");
                del.setOnAction(e -> deleteComment(getTableView().getItems().get(getIndex())));
                approve.setOnAction(e -> approveComment(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : box);
            }
        });

        // ── Episode Tree ──
        colEpTitle.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getValue().getLabel()));
        colEpNum.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getValue().getNum()));
        colEpDuration.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getValue().getDuration()));

        colEpTitle.setCellFactory(col -> new TreeTableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                EpisodeItem node = getTreeTableRow().getItem();
                setText(item);
                if (node != null && node.isSeason()) {
                    setStyle("-fx-font-weight:bold;-fx-text-fill:#e8e8e8;");
                } else {
                    setStyle("-fx-text-fill:#9999aa;-fx-font-size:12px;");
                }
            }
        });
        colEpActions.setCellFactory(col -> new TreeTableCell<>() {
            private final Button editBtn = new Button("✏ Edit");
            private final Button delBtn  = new Button("🗑");
            private final HBox   box     = new HBox(6, editBtn, delBtn);
            {
                editBtn.getStyleClass().addAll("btn-secondary", "btn-sm");
                delBtn.getStyleClass().addAll("btn-danger", "btn-sm");
                editBtn.setOnAction(e -> {
                    EpisodeItem item = getTreeTableRow().getItem();
                    if (item != null && !item.isSeason()) editEpisode(item);
                });
                delBtn.setOnAction(e -> {
                    EpisodeItem item = getTreeTableRow().getItem();
                    if (item != null && !item.isSeason()) deleteEpisode(item);
                });
            }
            @Override protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                if (empty) { setGraphic(null); return; }
                EpisodeItem item = getTreeTableRow().getItem();
                setGraphic(item != null && !item.isSeason() ? box : null);
            }
        });	
    }
        @FXML
        private void handleWatchMode() {
            if (!confirm("Mode Spectateur",
                    "Passer en mode spectateur ?",
                    "Vous serez redirigé vers la page d'accueil. Un bouton vous permettra de revenir.")) return;
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/home.fxml"));
                Parent homeRoot = loader.load();
                injectAdminReturnButton(homeRoot);
                Stage stage = Navigator.getPrimaryStage();
                stage.setScene(new Scene(homeRoot, stage.getWidth(), stage.getHeight()));
                stage.setTitle("Notflix — Mode Spectateur");
            } catch (Exception e) {
                showError("Navigation Error", e.getMessage());
            }
        }

        private void injectAdminReturnButton(Parent homeRoot) {
            if (homeRoot instanceof StackPane) {
                StackPane sp = (StackPane) homeRoot;

                Button returnBtn = new Button("🖥  Retour Dashboard");
                returnBtn.setStyle(
                    "-fx-background-color:rgba(20,20,28,0.92);" +
                    "-fx-text-fill:#e5e5e5;" +
                    "-fx-font-size:12px;" +
                    "-fx-font-weight:bold;" +
                    "-fx-background-radius:8px;" +
                    "-fx-border-color:rgba(255,255,255,0.12);" +
                    "-fx-border-width:1px;" +
                    "-fx-border-radius:8px;" +
                    "-fx-padding:8 16 8 16;" +
                    "-fx-cursor:hand;" +
                    "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.7),16,0,0,4);"
                );

                StackPane.setAlignment(returnBtn, javafx.geometry.Pos.BOTTOM_CENTER);
                StackPane.setMargin(returnBtn, new javafx.geometry.Insets(0, 0, 24, 0));

                returnBtn.setOnAction(e -> {
                    try {
                        FXMLLoader l = new FXMLLoader(getClass().getResource("/ui/AdminDashboard.fxml"));
                        Parent dashRoot = l.load();
                        Stage stage = Navigator.getPrimaryStage();
                        stage.setScene(new Scene(dashRoot, stage.getWidth(), stage.getHeight()));
                        stage.setTitle("Notflix Admin");
                        stage.setMaximized(true);
                    } catch (Exception ex) { ex.printStackTrace(); }
                });

                sp.getChildren().add(returnBtn);
            }
        }

        private void editEpisode(EpisodeItem item) {
            Dialog<Episode> dlg = new Dialog<>();
            dlg.setTitle("Edit Episode");
            dlg.getDialogPane().getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
            dlg.getDialogPane().getStyleClass().add("dialog-pane");
            dlg.getDialogPane().setPrefWidth(500);

            GridPane grid = new GridPane();
            grid.setHgap(12); grid.setVgap(10);
            grid.setPadding(new Insets(4, 0, 4, 0));

            TextField tfTitle = styledField("Title");
            TextField tfNum   = styledField("Episode Number");
            TextField tfDur   = styledField("Duration (min)");
            TextField tfVideo = styledField("Video URL");
            TextArea  taSyn   = new TextArea(); 
            taSyn.setPromptText("Synopsis…");
            taSyn.setPrefRowCount(2); 
            taSyn.getStyleClass().add("text-area-dark");

            tfNum.setText(item.getNum());
            tfDur.setText(item.getDuration());

            addRow(grid, 0, "Title *", tfTitle);
            addRow(grid, 1, "Episode #", tfNum);
            addRow(grid, 2, "Duration (min)", tfDur);
            addRow(grid, 3, "Video URL", tfVideo);
            addRowFull(grid, 4, "Synopsis", taSyn);

            dlg.getDialogPane().setContent(grid);
            dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            ((Button) dlg.getDialogPane().lookupButton(ButtonType.OK)).getStyleClass().add("btn-primary");

            dlg.setResultConverter(bt -> {
                if (bt != ButtonType.OK) return null;
                Episode ep = new Episode();
                ep.setId(item.getId());
                ep.setTitle(tfTitle.getText().trim());
                try { ep.setEpisodeNum(Integer.parseInt(tfNum.getText().trim())); } catch (Exception ignored) {}
                try { ep.setDurationMin(Integer.parseInt(tfDur.getText().trim())); } catch (Exception ignored) {}
                ep.setVideoUrl(tfVideo.getText().trim());
                ep.setSynopsis(taSyn.getText().trim());
                return ep;
            });

            dlg.showAndWait().ifPresent(ep -> {
                if (ep == null) return;
                new Thread(() -> {
                    try {
                        episodeDAO.update(ep);
                        Platform.runLater(() -> showToast("Episode updated!", true));
                    } catch (Exception ex) {
                        Platform.runLater(() -> showError("Error", ex.getMessage()));
                    }
                }).start();
            });
        }

        private void deleteEpisode(EpisodeItem item) {
            if (!confirm("Delete Episode", "Delete episode " + item.getLabel() + "?", "")) return;
            new Thread(() -> {
                try {
                    episodeDAO.delete(item.getId());
                    Platform.runLater(() -> {
                        Content selected = seriesList.getSelectionModel().getSelectedItem();
                        if (selected != null) loadSeriesTree(selected);
                        showToast("Episode deleted.", false);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showError("Error", ex.getMessage()));
                }
            }).start();
        }

    // ═══════════════════════════════════════════════════════
    // SERIES LIST CELL
    // ═══════════════════════════════════════════════════════
    private void setupSeriesListCell() {
        seriesList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Content c, boolean empty) {
                super.updateItem(c, empty);
                if (empty || c == null) { setText(null); setGraphic(null); return; }
                VBox box = new VBox(2);
                Label title = new Label(c.getTitle());
                title.setStyle("-fx-text-fill:#e8e8e8;-fx-font-size:13px;-fx-font-weight:bold;");
                Label info = new Label((c.getCategory() != null ? c.getCategory().getName() : "—")
                        + "  ·  " + c.getReleaseYear());
                info.setStyle("-fx-text-fill:#5a5a6e;-fx-font-size:11px;");
                box.getChildren().addAll(title, info);
                setGraphic(box); setText(null);
            }
        });

        seriesList.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) loadSeriesTree(n);
        });
    }

    // ═══════════════════════════════════════════════════════
    // DATA LOADING
    // ═══════════════════════════════════════════════════════
    private void loadAllData() {
        new Thread(() -> {
            try {
                List<Content> contents = contentDAO.findAll();
                List<Category> cats = categoryDAO.findAll();
                List<User> users = userDAO.findAll();
                List<Comment> comments = commentDAO.findAll();

                Platform.runLater(() -> {
                    allContent.setAll(contents);
                    allCategories.setAll(cats);
                    allUsers.setAll(users);
                    allComments.setAll(comments);

                    // Split by type
                    List<Content> films = contents.stream()
                            .filter(c -> c.getType() == Content.Type.FILM)
                            .collect(Collectors.toList());
                    List<Content> series = contents.stream()
                            .filter(c -> c.getType() == Content.Type.SERIE)
                            .collect(Collectors.toList());

                    allFilms.setAll(films);
                    allSeries.setAll(series);

                    // Tables
                    filmsTable.setItems(allFilms);
                    seriesList.setItems(allSeries);
                    categoriesTable.setItems(allCategories);
                    usersTable.setItems(allUsers);
                    commentsTable.setItems(allComments.filtered(c -> !c.isFlagged()));
                    flaggedTable.setItems(allComments.filtered(Comment::isFlagged));
                    recentContentTable.setItems(
                            FXCollections.observableArrayList(contents.stream().limit(10).collect(Collectors.toList())));

                    // KPIs
                    long totalViews = contents.stream().mapToLong(Content::getViewCount).sum();
                    long flaggedCount = comments.stream().filter(Comment::isFlagged).count();

                    animateLabel(kpiFilms, films.size());
                    animateLabel(kpiSeries, series.size());
                    animateLabel(kpiUsers, users.size());
                    animateLabel(kpiFlagged, (int) flaggedCount);
                    kpiViews.setText(formatNumber((int) totalViews));

                    flaggedBadge.setText(String.valueOf(flaggedCount));
                    filmCountLabel.setText(films.size() + " films total");
                    seriesCountLabel.setText(series.size() + " series total");
                    userCountLabel.setText(users.size() + " registered users");
                    commentCountLabel.setText(comments.size() + " total comments");
                    flaggedCountLabel.setText(flaggedCount + " items flagged");

                    // Genre filter for films
                    List<String> genres = new ArrayList<>();
                    genres.add("All Genres");
                    cats.stream().map(Category::getName).forEach(genres::add);
                    filmGenreFilter.setItems(FXCollections.observableArrayList(genres));

                    // Activity feed
                    buildActivityFeed(comments, users);

                    // Dashboard charts
                    buildDashboardCharts();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Data Load Error", e.getMessage()));
            }
        }).start();
    }

    // ═══════════════════════════════════════════════════════
    // CHARTS
    // ═══════════════════════════════════════════════════════
    private void buildDashboardCharts() {
        buildTop5(top5ChartContainer);
        buildPie(categoryChartContainer);
        buildReg(registrationsChartContainer);
    }

    private void buildAnalyticsCharts() {
        buildTop5(analyticsTop5);
        buildPie(analyticsPie);
        buildReg(analyticsReg);

        // Mini analytics labels
        long films = allFilms.size();
        long series = allSeries.size();
        long views = allContent.stream().mapToLong(Content::getViewCount).sum();
        if (analyticsFilms != null) analyticsFilms.setText(String.valueOf(films));
        if (analyticsSeries != null) analyticsSeries.setText(String.valueOf(series));
        if (analyticsUsers != null) analyticsUsers.setText(String.valueOf(allUsers.size()));
        if (analyticsComments != null) analyticsComments.setText(String.valueOf(allComments.size()));
        if (analyticsViews != null) analyticsViews.setText(formatNumber((int) views));

        // Category mini chart
        if (catMiniChartContainer != null) buildPie(catMiniChartContainer);
    }

    private void buildTop5(StackPane container) {
        container.getChildren().clear();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#5a5a6e"));
        yAxis.setTickLabelFill(Color.web("#5a5a6e"));

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setBarGap(4);
        chart.setCategoryGap(10);
        chart.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Sort allContent by views, top 5
        allContent.stream()
                .sorted((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()))
                .limit(5)
                .forEach(c -> {
                    String t = c.getTitle().length() > 14 ? c.getTitle().substring(0, 14) + "…" : c.getTitle();
                    series.getData().add(new XYChart.Data<>(t, c.getViewCount()));
                });

        chart.getData().add(series);
        chart.setPrefHeight(Double.MAX_VALUE);
        VBox.setVgrow(chart, Priority.ALWAYS);
        container.getChildren().add(chart);
    }

    private void buildPie(StackPane container) {
        container.getChildren().clear();
        PieChart pie = new PieChart();
        pie.setLegendVisible(true);
        pie.setLabelsVisible(false);
        pie.setAnimated(false);
        pie.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

        Map<String, Long> dist = new LinkedHashMap<>();
        allContent.forEach(c -> {
            String cat = c.getCategory() != null ? c.getCategory().getName() : "Unknown";
            dist.merge(cat, 1L, Long::sum);
        });
        dist.forEach((name, count) -> pie.getData().add(new PieChart.Data(name, count)));

        pie.setPrefHeight(Double.MAX_VALUE);
        container.getChildren().add(pie);
    }

    private void buildReg(StackPane container) {
        container.getChildren().clear();
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setTickLabelFill(Color.web("#5a5a6e"));
        yAxis.setTickLabelFill(Color.web("#5a5a6e"));

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setLegendVisible(false);
        chart.setAnimated(false);
        chart.setCreateSymbols(true);
        chart.getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());

        XYChart.Series<String, Number> series = new XYChart.Series<>();

        try {
            List<Object[]> regData = userDAO.getRegistrationsPerDay();
            if (regData.isEmpty()) {
                // Show last 7 days with 0
                for (int i = 6; i >= 0; i--) {
                    String day = LocalDateTime.now().minusDays(i)
                            .format(DateTimeFormatter.ofPattern("dd/MM"));
                    series.getData().add(new XYChart.Data<>(day, 0));
                }
            } else {
                regData.forEach(row -> series.getData().add(
                        new XYChart.Data<>((String) row[0], (int) row[1])));
            }
        } catch (Exception e) {
            // fallback: empty
        }

        chart.getData().add(series);
        chart.setPrefHeight(Double.MAX_VALUE);
        container.getChildren().add(chart);
    }

    // ═══════════════════════════════════════════════════════
    // ACTIVITY FEED
    // ═══════════════════════════════════════════════════════
    private void buildActivityFeed(List<Comment> comments, List<User> users) {
        activityFeed.getChildren().clear();

        // Last 5 flagged comments
        comments.stream().filter(Comment::isFlagged).limit(3).forEach(c -> {
            HBox row = actRow("⚑ Comment flagged by " + c.getUserName(), "#ff3b3b");
            activityFeed.getChildren().add(row);
        });

        // Last 4 users
        users.stream().limit(4).forEach(u -> {
            HBox row = actRow("👤 " + u.getNom() + " registered", "#22c55e");
            activityFeed.getChildren().add(row);
        });

        // Top content
        allContent.stream()
                .sorted((a, b) -> Integer.compare(b.getViewCount(), a.getViewCount()))
                .limit(3)
                .forEach(c -> {
                    HBox row = actRow("👁 " + c.getTitle() + " — " + formatNumber(c.getViewCount()) + " views", "#4fa3e0");
                    activityFeed.getChildren().add(row);
                });
    }

    private HBox actRow(String text, String color) {
        HBox row = new HBox(10);
        row.setStyle("-fx-padding:8 0 8 0;-fx-border-color:transparent transparent rgba(255,255,255,0.05) transparent;-fx-border-width:0 0 1 0;");
        Circle dot = new Circle(4, Color.web(color));
        dot.setEffect(new javafx.scene.effect.DropShadow(6, Color.web(color)));
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill:#9999aa;-fx-font-size:12px;");
        lbl.setWrapText(true);
        HBox.setHgrow(lbl, Priority.ALWAYS);
        row.getChildren().addAll(dot, lbl);
        return row;
    }

    // ═══════════════════════════════════════════════════════
    // SERIES TREE
    // ═══════════════════════════════════════════════════════
    private void loadSeriesTree(Content serie) {
        selectedSeriesLabel.setText(serie.getTitle());
        TreeItem<EpisodeItem> root = new TreeItem<>(new EpisodeItem(serie.getTitle(), "", "", false, serie.getId()));
        root.setExpanded(true);

        new Thread(() -> {
            try {
                List<Season> seasons = seasonDAO.findBySerieId(serie.getId());
                Platform.runLater(() -> {
                    for (Season season : seasons) {
                        TreeItem<EpisodeItem> seasonNode = new TreeItem<>(
                                new EpisodeItem("📂 " + season.toString(), "", "", true, season.getId()));
                        seasonNode.setExpanded(true);

                        new Thread(() -> {
                            try {
                                List<Episode> episodes = episodeDAO.findBySeasonId(season.getId());
                                Platform.runLater(() -> {
                                    episodes.forEach(ep -> {
                                        TreeItem<EpisodeItem> epNode = new TreeItem<>(
                                                new EpisodeItem(
                                                        "   E" + ep.getEpisodeNum() + " — " + ep.getTitle(),
                                                        String.valueOf(ep.getEpisodeNum()),
                                                        ep.getDurationMin() > 0 ? ep.getDurationMin() + " min" : "—",
                                                        false,
                                                        ep.getId()
                                                ));
                                        seasonNode.getChildren().add(epNode);
                                    });
                                });
                            } catch (Exception ignored) {}
                        }).start();

                        root.getChildren().add(seasonNode);
                    }
                    episodesTree.setRoot(root);
                    episodesTree.setShowRoot(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Load Error", e.getMessage()));
            }
        }).start();
    }

    // ═══════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════
    private void setActive(Button btn, Node pane, String title) {
        // Hide all panels
        contentStack.getChildren().forEach(n -> n.setVisible(false));
        pane.setVisible(true);

        // Update nav styles
        navButtons.forEach(b -> {
            b.getStyleClass().remove("nav-btn-active");
        });
        if (btn != null) {
            if (!btn.getStyleClass().contains("nav-btn-active"))
                btn.getStyleClass().add("nav-btn-active");
        }

        pageTitle.setText(title);
        pageBreadcrumb.setText("Home / " + title);

        // Fade
        FadeTransition ft = new FadeTransition(Duration.millis(180), pane);
        ft.setFromValue(0.5); ft.setToValue(1.0); ft.play();
    }

    @FXML private void showDashboard() {
        setActive(navDashboard, dashboardPane, "Dashboard");
    }

    @FXML private void showAnalytics() {
        setActive(navAnalytics, analyticsPane, "Analytics");
        Platform.runLater(this::buildAnalyticsCharts);
    }

    @FXML private void showFilms() {
        setActive(navFilms, filmsPane, "Films");
    }

    @FXML private void showSeries() {
        setActive(navSeries, seriesPane, "Series & Episodes");
    }

    @FXML private void showCategories() {
        setActive(navCategories, categoriesPane, "Categories");
        Platform.runLater(() -> buildPie(catMiniChartContainer));
    }

    @FXML private void showUsers() {
        setActive(navUsers, usersPane, "Users");
    }

    @FXML private void showComments() {
        setActive(navComments, commentsPane, "Comments");
    }

    @FXML private void showFlagged() {
        setActive(navFlagged, flaggedPane, "Flagged Content");
    }

    // ═══════════════════════════════════════════════════════
    // CONTENT CRUD (Films & Series)
    // ═══════════════════════════════════════════════════════
    @FXML private void showAddContentDialog() { openContentDialog(null, false); }
    @FXML private void showAddSeriesDialog()   { openContentDialog(null, true); }

    private void editContent(Content c) { openContentDialog(c, c.isSerie()); }

    private void openContentDialog(Content existing, boolean forSerie) {
        boolean isEdit = (existing != null);
        String typeLabel = forSerie ? "Series" : "Film";

        Dialog<Content> dlg = new Dialog<>();
        dlg.setTitle(isEdit ? "Edit " + typeLabel : "Add " + typeLabel);
        dlg.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/dark-theme.css").toExternalForm());
        dlg.getDialogPane().getStyleClass().add("dialog-pane");
        dlg.getDialogPane().setPrefWidth(560);

        // ── Form ──
        GridPane grid = new GridPane();
        grid.setHgap(14); grid.setVgap(10);
        grid.setPadding(new Insets(4, 0, 4, 0));

        TextField tfTitle = styledField("Title *");
        TextField tfYear = styledField("Release Year");
        TextField tfCover = styledField("Cover URL");
        TextField tfTrailer = styledField("Trailer URL");
        TextField tfCasting = styledField("Casting");
        TextArea taSynopsis = new TextArea();
        taSynopsis.setPromptText("Synopsis…");
        taSynopsis.setPrefRowCount(3);
        taSynopsis.getStyleClass().add("text-area-dark");
        taSynopsis.getStyleClass().add("text-field-dark");

        TextField tfVideo = styledField("Video URL (film only)");
        TextField tfDuration = styledField("Duration (min)");

        ComboBox<Category> cbCat = new ComboBox<>(allCategories);
        cbCat.setPromptText("Category");
        cbCat.getStyleClass().add("combo-dark");
        cbCat.setMaxWidth(Double.MAX_VALUE);

        CheckBox cbFeatured = new CheckBox("Featured");
        cbFeatured.getStyleClass().add("check-box-dark");

        // Pre-fill if editing
        if (existing != null) {
            tfTitle.setText(existing.getTitle());
            tfYear.setText(existing.getReleaseYear() > 0 ? String.valueOf(existing.getReleaseYear()) : "");
            tfCover.setText(existing.getCoverUrl() != null ? existing.getCoverUrl() : "");
            tfTrailer.setText(existing.getTrailerUrl() != null ? existing.getTrailerUrl() : "");
            tfCasting.setText(existing.getCasting() != null ? existing.getCasting() : "");
            taSynopsis.setText(existing.getSynopsis() != null ? existing.getSynopsis() : "");
            tfVideo.setText(existing.getVideoUrl() != null ? existing.getVideoUrl() : "");
            tfDuration.setText(existing.getDurationMin() > 0 ? String.valueOf(existing.getDurationMin()) : "");
            cbFeatured.setSelected(existing.isFeatured());
            if (existing.getCategory() != null) {
                allCategories.stream()
                        .filter(c -> c.getId() == existing.getCategory().getId())
                        .findFirst().ifPresent(cbCat::setValue);
            }
        }

        addRow(grid, 0, "Title *", tfTitle);
        addRow(grid, 1, "Year", tfYear);
        addRow(grid, 2, "Category", cbCat);
        addRowFull(grid, 3, "Synopsis", taSynopsis);
        addRow(grid, 4, "Cover URL", tfCover);
        addRow(grid, 5, "Trailer URL", tfTrailer);
        addRow(grid, 6, "Casting", tfCasting);
        if (!forSerie) {
            addRow(grid, 7, "Video URL", tfVideo);
            addRow(grid, 8, "Duration (min)", tfDuration);
        }
        grid.add(cbFeatured, 0, forSerie ? 7 : 9, 2, 1);

        Label errorLbl = new Label();
        errorLbl.setStyle("-fx-text-fill:#ff3b3b;-fx-font-size:12px;");
        grid.add(errorLbl, 0, forSerie ? 8 : 10, 2, 1);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.setText(isEdit ? "Save Changes" : "Add " + typeLabel);
        okBtn.getStyleClass().add("btn-primary");

        dlg.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;
            String title = tfTitle.getText().trim();
            if (title.isEmpty()) { errorLbl.setText("Title is required!"); return null; }

            Content c = isEdit ? existing : new Content();
            c.setTitle(title);
            c.setType(forSerie ? Content.Type.SERIE : Content.Type.FILM);
            c.setSynopsis(taSync(tasynopsis -> tasynopsis.getText(), taSync2 -> taSync2.getText(), taSynopsis));
            try { c.setReleaseYear(Integer.parseInt(tfYear.getText().trim())); } catch (Exception ignored) {}
            c.setCoverUrl(tfCover.getText().trim());
            c.setTrailerUrl(tfTrailer.getText().trim());
            c.setCasting(tfCasting.getText().trim());
            if (!forSerie) {
                c.setVideoUrl(tfVideo.getText().trim());
                try { c.setDurationMin(Integer.parseInt(tfDuration.getText().trim())); } catch (Exception ignored) {}
            }
            c.setCategory(cbCat.getValue());
            c.setFeatured(cbFeatured.isSelected());
            return c;
        });

        dlg.showAndWait().ifPresent(c -> {
            if (c == null) return;
            new Thread(() -> {
                try {
                    if (isEdit) contentDAO.update(c);
                    else contentDAO.save(c);
                    Platform.runLater(() -> {
                        loadAllData();
                        showToast(isEdit ? "Updated: " + c.getTitle() : "Added: " + c.getTitle(), true);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showError("Save Error", ex.getMessage()));
                }
            }).start();
        });
    }

    private String taSync(java.util.function.Function<TextArea, String> fn1,
                          java.util.function.Function<TextArea, String> fn2, TextArea ta) {
        return ta.getText().trim();
    }

    private void deleteContent(Content c) {
        if (!confirm("Delete Content", "Delete \"" + c.getTitle() + "\"?",
                "This will permanently remove it.")) return;
        new Thread(() -> {
            try {
                contentDAO.delete(c.getId());
                Platform.runLater(() -> {
                    loadAllData();
                    showToast("Deleted: " + c.getTitle(), false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Delete Error", ex.getMessage()));
            }
        }).start();
    }

    // ═══════════════════════════════════════════════════════
    // SEASON / EPISODE CRUD
    // ═══════════════════════════════════════════════════════
    @FXML private void showAddSeasonDialog() {
        Content selected = seriesList.getSelectionModel().getSelectedItem();
        if (selected == null) { showToast("Please select a series first.", false); return; }

        Dialog<Season> dlg = new Dialog<>();
        dlg.setTitle("Add Season — " + selected.getTitle());
        dlg.getDialogPane().getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
        dlg.getDialogPane().getStyleClass().add("dialog-pane");

        GridPane grid = new GridPane();
        grid.setHgap(12); grid.setVgap(10);
        grid.setPadding(new Insets(4, 0, 4, 0));

        TextField tfNum = styledField("Season Number");
        TextField tfTitle = styledField("Title (e.g. Saison 1)");

        addRow(grid, 0, "Number *", tfNum);
        addRow(grid, 1, "Title", tfTitle);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        ((Button) dlg.getDialogPane().lookupButton(ButtonType.OK)).getStyleClass().add("btn-primary");

        dlg.setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;
            try {
                int num = Integer.parseInt(tfNum.getText().trim());
                String title = tfTitle.getText().trim();
                if (title.isEmpty()) title = "Saison " + num;
                Season s = new Season();
                s.setSerieId(selected.getId());
                s.setNumber(num);
                s.setTitle(title);
                return s;
            } catch (Exception e) { return null; }
        });

        dlg.showAndWait().ifPresent(s -> {
            if (s == null) return;
            new Thread(() -> {
                try {
                    seasonDAO.save(s);
                    Platform.runLater(() -> {
                        loadSeriesTree(selected);
                        showToast("Season added!", true);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showError("Error", ex.getMessage()));
                }
            }).start();
        });
    }

    @FXML private void showAddEpisodeDialog() {
        Content selected = seriesList.getSelectionModel().getSelectedItem();
        if (selected == null) { showToast("Please select a series first.", false); return; }

        new Thread(() -> {
            try {
                List<Season> seasons = seasonDAO.findBySerieId(selected.getId());
                Platform.runLater(() -> {
                    if (seasons.isEmpty()) {
                        showToast("Add a season first!", false);
                        return;
                    }
                    Dialog<Episode> dlg = new Dialog<>();
                    dlg.setTitle("Add Episode — " + selected.getTitle());
                    dlg.getDialogPane().getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
                    dlg.getDialogPane().getStyleClass().add("dialog-pane");
                    dlg.getDialogPane().setPrefWidth(520);

                    GridPane grid = new GridPane();
                    grid.setHgap(12); grid.setVgap(10);
                    grid.setPadding(new Insets(4, 0, 4, 0));

                    ComboBox<Season> cbSeason = new ComboBox<>(FXCollections.observableArrayList(seasons));
                    cbSeason.getStyleClass().add("combo-dark");
                    cbSeason.setMaxWidth(Double.MAX_VALUE);
                    cbSeason.getSelectionModel().selectFirst();

                    TextField tfNum = styledField("Episode Number");
                    TextField tfTitle = styledField("Title *");
                    TextField tfDuration = styledField("Duration (min)");
                    TextField tfVideo = styledField("Video URL");
                    TextField tfThumb = styledField("Thumbnail URL");
                    TextArea taSynopsis = new TextArea();
                    taSynopsis.setPromptText("Synopsis…"); taSynopsis.setPrefRowCount(2);
                    taSynopsis.getStyleClass().add("text-area-dark");

                    addRow(grid, 0, "Season *", cbSeason);
                    addRow(grid, 1, "Episode #", tfNum);
                    addRow(grid, 2, "Title *", tfTitle);
                    addRow(grid, 3, "Duration (min)", tfDuration);
                    addRow(grid, 4, "Video URL", tfVideo);
                    addRow(grid, 5, "Thumbnail URL", tfThumb);
                    addRowFull(grid, 6, "Synopsis", taSynopsis);

                    dlg.getDialogPane().setContent(grid);
                    dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                    ((Button) dlg.getDialogPane().lookupButton(ButtonType.OK)).getStyleClass().add("btn-primary");

                    dlg.setResultConverter(bt -> {
                        if (bt != ButtonType.OK) return null;
                        Season season = cbSeason.getValue();
                        if (season == null) return null;
                        String title = tfTitle.getText().trim();
                        if (title.isEmpty()) return null;
                        Episode ep = new Episode();
                        ep.setSeasonId(season.getId());
                        try { ep.setEpisodeNum(Integer.parseInt(tfNum.getText().trim())); } catch (Exception ignored) { ep.setEpisodeNum(1); }
                        ep.setTitle(title);
                        try { ep.setDurationMin(Integer.parseInt(tfDuration.getText().trim())); } catch (Exception ignored) {}
                        ep.setVideoUrl(tfVideo.getText().trim());
                        ep.setThumbnailUrl(tfThumb.getText().trim());
                        ep.setSynopsis(taSynopsis.getText().trim());
                        return ep;
                    });

                    dlg.showAndWait().ifPresent(ep -> {
                        if (ep == null) return;
                        new Thread(() -> {
                            try {
                                episodeDAO.save(ep);
                                Platform.runLater(() -> {
                                    loadSeriesTree(selected);
                                    showToast("Episode added!", true);
                                });
                            } catch (Exception ex) {
                                Platform.runLater(() -> showError("Error", ex.getMessage()));
                            }
                        }).start();
                    });
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error", ex.getMessage()));
            }
        }).start();
    }

    // ═══════════════════════════════════════════════════════
    // CATEGORY CRUD
    // ═══════════════════════════════════════════════════════
    @FXML private void showAddCategoryDialog() { openCategoryDialog(null); }

    private void editCategory(Category cat) { openCategoryDialog(cat); }

    private void openCategoryDialog(Category existing) {
        TextInputDialog dlg = new TextInputDialog(existing != null ? existing.getName() : "");
        dlg.setTitle(existing != null ? "Rename Category" : "New Category");
        dlg.setHeaderText(existing != null ? "Rename \"" + existing.getName() + "\"" : "Enter category name");
        dlg.setContentText("Name:");
        dlg.getDialogPane().getStylesheets().add(getClass().getResource("/css/dark-theme.css").toExternalForm());
        dlg.getDialogPane().getStyleClass().add("dialog-pane");

        dlg.showAndWait().ifPresent(name -> {
            if (name.isBlank()) return;
            new Thread(() -> {
                try {
                    if (existing != null) {
                        existing.setName(name);
                        categoryDAO.update(existing);
                    } else {
                        Category c = new Category();
                        c.setName(name);
                        categoryDAO.save(c);
                    }
                    Platform.runLater(() -> {
                        loadAllData();
                        showToast("Category saved: " + name, true);
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> showError("Error", ex.getMessage()));
                }
            }).start();
        });
    }

    private void deleteCategory(Category cat) {
        if (!confirm("Delete Category", "Delete \"" + cat.getName() + "\"?",
                "Content in this category will become uncategorized.")) return;
        new Thread(() -> {
            try {
                categoryDAO.delete(cat.getId());
                Platform.runLater(() -> {
                    loadAllData();
                    showToast("Category deleted: " + cat.getName(), false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error", ex.getMessage()));
            }
        }).start();
    }

    // ═══════════════════════════════════════════════════════
    // USER ACTIONS
    // ═══════════════════════════════════════════════════════
    private void deleteUser(User u) {
        if (!confirm("Delete User", "Delete \"" + u.getNom() + "\"?",
                "All their data will be permanently removed.")) return;
        new Thread(() -> {
            try {
                userDAO.delete(u.getId());
                Platform.runLater(() -> {
                    loadAllData();
                    showToast("User deleted: " + u.getNom(), false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error", ex.getMessage()));
            }
        }).start();
    }

    private void promoteUser(User u) {
        if (!confirm("Promote to Admin", "Promote \"" + u.getNom() + "\" to Admin?",
                "They will have full access to this dashboard.")) return;
        u.setRole("ADMIN");
        new Thread(() -> {
            try {
                userDAO.update(u);
                Platform.runLater(() -> {
                    usersTable.refresh();
                    showToast(u.getNom() + " promoted to Admin!", true);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error", ex.getMessage()));
            }
        }).start();
    }

    // ═══════════════════════════════════════════════════════
    // COMMENT ACTIONS
    // ═══════════════════════════════════════════════════════
    private void deleteComment(Comment c) {
        if (!confirm("Delete Comment", "Delete this comment?", "\"" + c.getBody() + "\"")) return;
        new Thread(() -> {
            try {
                commentDAO.delete(c.getId());
                Platform.runLater(() -> {
                    allComments.remove(c);
                    refreshFlaggedBadge();
                    showToast("Comment deleted.", false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error", ex.getMessage()));
            }
        }).start();
    }

    private void flagComment(Comment c) {
        new Thread(() -> {
            try {
                commentDAO.flag(c.getId());
                Platform.runLater(() -> {
                    c.setFlagged(true);
                    commentsTable.refresh();
                    flaggedTable.setItems(allComments.filtered(Comment::isFlagged));
                    refreshFlaggedBadge();
                    showToast("Comment flagged.", false);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error", ex.getMessage()));
            }
        }).start();
    }

    private void approveComment(Comment c) {
        // Unflag — direct update via CommentDAO using existing connection
        new Thread(() -> {
            try {
                java.sql.Connection conn = org.JavaProject.JavaNetflixProject.Utils.ConxDB.getConnection();
                try (java.sql.PreparedStatement ps = conn.prepareStatement(
                        "UPDATE comments SET flagged=0 WHERE id=?")) {
                    ps.setInt(1, c.getId());
                    ps.executeUpdate();
                }
                Platform.runLater(() -> {
                    c.setFlagged(false);
                    flaggedTable.setItems(allComments.filtered(Comment::isFlagged));
                    commentsTable.setItems(allComments.filtered(cm -> !cm.isFlagged()));
                    refreshFlaggedBadge();
                    showToast("Comment approved.", true);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Error", ex.getMessage()));
            }
        }).start();
    }

    @FXML private void dismissAllFlagged() {
        List<Comment> flagged = new ArrayList<>(allComments.filtered(Comment::isFlagged));
        flagged.forEach(c -> {
            new Thread(() -> {
                try {
                    java.sql.Connection conn = org.JavaProject.JavaNetflixProject.Utils.ConxDB.getConnection();
                    try (java.sql.PreparedStatement ps = conn.prepareStatement(
                            "UPDATE comments SET flagged=0 WHERE id=?")) {
                        ps.setInt(1, c.getId());
                        ps.executeUpdate();
                    }
                } catch (Exception ignored) {}
            }).start();
            c.setFlagged(false);
        });
        flaggedTable.setItems(allComments.filtered(Comment::isFlagged));
        commentsTable.setItems(allComments.filtered(cm -> !cm.isFlagged()));
        refreshFlaggedBadge();
        showToast("All flags dismissed.", true);
    }

    @FXML private void deleteAllFlagged() {
        if (!confirm("Delete All Flagged", "Delete all flagged comments?",
                "This action cannot be undone.")) return;
        List<Comment> flagged = new ArrayList<>(allComments.filtered(Comment::isFlagged));
        flagged.forEach(c -> {
            new Thread(() -> {
                try { commentDAO.delete(c.getId()); } catch (Exception ignored) {}
            }).start();
            allComments.remove(c);
        });
        refreshFlaggedBadge();
        showToast(flagged.size() + " comments deleted.", false);
    }

    private void refreshFlaggedBadge() {
        long count = allComments.stream().filter(Comment::isFlagged).count();
        flaggedBadge.setText(String.valueOf(count));
        animateLabel(kpiFlagged, (int) count);
        flaggedCountLabel.setText(count + " items flagged");
    }

    // ═══════════════════════════════════════════════════════
    // FILTERS
    // ═══════════════════════════════════════════════════════
    @FXML private void filterFilms() {
        String q = filmSearchField.getText().toLowerCase();
        String genre = filmGenreFilter.getValue();
        ObservableList<Content> filtered = allFilms.filtered(f -> {
            boolean mQ = q.isBlank() || f.getTitle().toLowerCase().contains(q);
            boolean mG = genre == null || genre.equals("All Genres")
                    || (f.getCategory() != null && f.getCategory().getName().equals(genre));
            return mQ && mG;
        });
        filmsTable.setItems(filtered);
        filmCountLabel.setText(filtered.size() + " films shown");
    }

    @FXML private void filterUsers() {
        String q = userSearchField.getText().toLowerCase();
        String role = userRoleFilter.getValue();
        usersTable.setItems(allUsers.filtered(u -> {
            boolean mQ = q.isBlank() || u.getNom().toLowerCase().contains(q)
                    || u.getEmail().toLowerCase().contains(q);
            boolean mR = role == null || role.equals("All Roles") || u.getRole().equals(role);
            return mQ && mR;
        }));
    }

    @FXML private void filterComments() {
        String q = commentSearchField.getText().toLowerCase();
        String filter = commentFilterCombo.getValue();
        commentsTable.setItems(allComments.filtered(c -> {
            boolean mQ = q.isBlank() || (c.getBody() != null && c.getBody().toLowerCase().contains(q));
            boolean mF = filter == null || filter.equals("All Comments")
                    || (filter.equals("Flagged Only") && c.isFlagged())
                    || (filter.equals("Normal") && !c.isFlagged());
            return mQ && mF;
        }));
    }

    // ═══════════════════════════════════════════════════════
    // MISC ACTIONS
    // ═══════════════════════════════════════════════════════
    @FXML private void refreshAll() {
        loadAllData();
        showToast("Data refreshed.", true);
    }

    @FXML private void quickAddContent() { showAddContentDialog(); }

    @FXML private void handleGlobalSearch() {
        String q = globalSearch.getText().trim().toLowerCase();
        if (q.isBlank()) return;
        if (allFilms.stream().anyMatch(f -> f.getTitle().toLowerCase().contains(q))) {
            showFilms();
            filmSearchField.setText(q);
            filterFilms();
        } else if (allUsers.stream().anyMatch(u -> u.getNom().toLowerCase().contains(q) || u.getEmail().toLowerCase().contains(q))) {
            showUsers();
            userSearchField.setText(q);
            filterUsers();
        }
    }

    @FXML private void showNotifications() {
        showToast("No new notifications.", true);
    }

    @FXML private void handleLogout() {
        if (!confirm("Sign Out", "Sign out of Notflix Admin?", "")) return;
        if (clock != null) clock.stop();
        Session.logout();
        try { Navigator.navigateTo("/ui/LoginPage.fxml", 900, 600); }
        catch (Exception e) { showError("Navigation Error", e.getMessage()); }
    }

    // ═══════════════════════════════════════════════════════
    // HELPERS
    // ═══════════════════════════════════════════════════════

    /** Animated KPI counter */
    private void animateLabel(Label label, int target) {
        if (label == null) return;
        Timeline t = new Timeline();
        int steps = 24;
        for (int i = 0; i <= steps; i++) {
            final int v = (int) (target * (i / (double) steps));
            t.getKeyFrames().add(new KeyFrame(Duration.millis(i * 22L),
                    e -> label.setText(String.valueOf(v))));
        }
        t.play();
    }

    /** Format large numbers: 1234 → 1.2K */
    private String formatNumber(int n) {
        if (n >= 1_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n >= 1_000) return String.format("%.1fK", n / 1_000.0);
        return String.valueOf(n);
    }

    /** User initials from name */
    private String initials(String name) {
        if (name == null || name.isBlank()) return "AD";
        String[] p = name.trim().split("\\s+");
        if (p.length >= 2) return ("" + p[0].charAt(0) + p[1].charAt(0)).toUpperCase();
        return name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    /** Styled TextField */
    private TextField styledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.getStyleClass().add("text-field-dark");
        return tf;
    }

    /** Add label + control row to GridPane */
    private void addRow(GridPane grid, int row, String label, Control ctrl) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("form-label-dark");
        GridPane.setConstraints(lbl, 0, row);
        GridPane.setConstraints(ctrl, 1, row);
        GridPane.setHgrow(ctrl, Priority.ALWAYS);
        ctrl.setMaxWidth(Double.MAX_VALUE);
        grid.getChildren().addAll(lbl, ctrl);
    }

    /** Add full-width control to GridPane */
    private void addRowFull(GridPane grid, int row, String label, Control ctrl) {
        Label lbl = new Label(label);
        lbl.getStyleClass().add("form-label-dark");
        GridPane.setConstraints(lbl, 0, row, 2, 1);
        GridPane.setConstraints(ctrl, 0, row + 1, 2, 1);
        GridPane.setHgrow(ctrl, Priority.ALWAYS);
        ctrl.setMaxWidth(Double.MAX_VALUE);
        grid.getChildren().addAll(lbl, ctrl);
    }

    /** Confirmation dialog */
    private boolean confirm(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content.isBlank() ? null : content);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/dark-theme.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    /** Error dialog */
    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(msg);
        alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/dark-theme.css").toExternalForm());
        alert.getDialogPane().getStyleClass().add("dialog-pane");
        alert.showAndWait();
    }

    /** Toast notification overlay */
    private void showToast(String msg, boolean success) {
        Label toast = new Label((success ? "✓  " : "✕  ") + msg);
        toast.setStyle(
                "-fx-background-color:#18181d;" +
                "-fx-text-fill:" + (success ? "#22c55e" : "#ff3b3b") + ";" +
                "-fx-font-size:12px;" +
                "-fx-background-radius:8px;" +
                "-fx-border-color:" + (success ? "rgba(34,197,94,0.3)" : "rgba(255,59,59,0.3)") + ";" +
                "-fx-border-width:1px;" +
                "-fx-border-radius:8px;" +
                "-fx-padding:10 18 10 18;" +
                "-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.7),16,0,0,4);");
        toast.setOpacity(0);
        toast.setTranslateX(20);
        toast.setTranslateY(-20);
        StackPane.setAlignment(toast, javafx.geometry.Pos.BOTTOM_RIGHT);
        contentStack.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), toast);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);
        FadeTransition fadeOut = new FadeTransition(Duration.millis(350), toast);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2.5));
        fadeOut.setOnFinished(e -> contentStack.getChildren().remove(toast));
        new SequentialTransition(fadeIn, fadeOut).play();
    }
}