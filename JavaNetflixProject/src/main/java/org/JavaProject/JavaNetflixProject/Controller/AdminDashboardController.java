package org.JavaProject.JavaNetflixProject.Controller;

import org.JavaProject.JavaNetflixProject.DAO.*;
import org.JavaProject.JavaNetflixProject.Entities.*;
import org.JavaProject.JavaNetflixProject.Services.ContentService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import org.JavaProject.JavaNetflixProject.Utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class AdminDashboardController {

    // Tabs
    @FXML private TabPane tabPane;

    // Catalogue tab
    @FXML private TableView<Content> contentTable;
    @FXML private TableColumn<Content, Integer> colId;
    @FXML private TableColumn<Content, String> colTitle;
    @FXML private TableColumn<Content, String> colType;
    @FXML private TableColumn<Content, String> colCategory;
    @FXML private TableColumn<Content, Integer> colViews;
    @FXML private TableColumn<Content, Double> colRating;

    // Form fields
    @FXML private TextField formTitle;
    @FXML private ComboBox<String> formType;
    @FXML private TextArea formSynopsis;
    @FXML private TextField formYear;
    @FXML private TextField formCoverUrl;
    @FXML private TextField formVideoUrl;
    @FXML private TextField formDuration;
    @FXML private TextField formCasting;
    @FXML private ComboBox<Category> formCategory;
    @FXML private CheckBox formFeatured;
    @FXML private Label formError;

    // Users tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String> colUserName;
    @FXML private TableColumn<User, String> colUserEmail;
    @FXML private TableColumn<User, String> colUserRole;

    // Comments tab
    @FXML private TableView<Comment> commentsTable;
    @FXML private TableColumn<Comment, Integer> colCommentId;
    @FXML private TableColumn<Comment, String> colCommentUser;
    @FXML private TableColumn<Comment, String> colCommentBody;
    @FXML private TableColumn<Comment, Boolean> colCommentFlagged;

    // Analytics
    @FXML private PieChart categoryPieChart;
    @FXML private BarChart<String, Number> top5BarChart;
    @FXML private BarChart<String, Number> registrationsBarChart;

    @FXML private Label adminNameLabel;

    private final ContentService contentService = new ContentService();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final UserDAO userDAO = new UserDAO();
    private final CommentDAO commentDAO = new CommentDAO();

    private Content selectedContent;

    @FXML
    public void initialize() {
        adminNameLabel.setText("Admin: " + SessionManager.getCurrentUser().getNom());
        formError.setVisible(false);

        setupContentTable();
        setupUsersTable();
        setupCommentsTable();

        formType.setItems(FXCollections.observableArrayList("FILM", "SERIE"));

        loadAllData();
    }

    private void setupContentTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colViews.setCellValueFactory(new PropertyValueFactory<>("viewCount"));
        colRating.setCellValueFactory(new PropertyValueFactory<>("avgRating"));
        colCategory.setCellValueFactory(data ->
            new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCategory() != null ? data.getValue().getCategory().getName() : ""));

        contentTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n != null) populateForm(n);
        });
    }

    private void setupUsersTable() {
        colUserId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUserName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUserEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colUserRole.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    private void setupCommentsTable() {
        colCommentId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCommentUser.setCellValueFactory(new PropertyValueFactory<>("userName"));
        colCommentBody.setCellValueFactory(new PropertyValueFactory<>("body"));
        colCommentFlagged.setCellValueFactory(new PropertyValueFactory<>("flagged"));
    }

    private void loadAllData() {
        try {
            // Content
            List<Content> contents = contentService.getAllContent();
            contentTable.setItems(FXCollections.observableArrayList(contents));

            // Categories for form
            List<Category> cats = categoryDAO.findAll();
            formCategory.setItems(FXCollections.observableArrayList(cats));

            // Users
            List<User> users = userDAO.findAll();
            usersTable.setItems(FXCollections.observableArrayList(users));

            // Comments
            List<Comment> comments = commentDAO.findAll();
            commentsTable.setItems(FXCollections.observableArrayList(comments));

            // Analytics
            loadAnalytics();

        } catch (SQLException e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    private void loadAnalytics() throws SQLException {
        // Pie chart - content by category
        categoryPieChart.getData().clear();
        List<Object[]> catData = contentService.getContentByCategory();
        for (Object[] row : catData) {
            categoryPieChart.getData().add(new PieChart.Data((String) row[0], (int) row[1]));
        }

        // Bar chart - top 5 viewed
        top5BarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Vues");
        List<Content> top5 = contentService.getTop5();
        for (Content c : top5) {
            series.getData().add(new XYChart.Data<>(c.getTitle().length() > 15 ? c.getTitle().substring(0,15) + "..." : c.getTitle(), c.getViewCount()));
        }
        top5BarChart.getData().add(series);

        // Registrations per day
        registrationsBarChart.getData().clear();
        XYChart.Series<String, Number> regSeries = new XYChart.Series<>();
        regSeries.setName("Inscriptions");
        List<Object[]> regData = userDAO.getRegistrationsPerDay();
        for (Object[] row : regData) {
            regSeries.getData().add(new XYChart.Data<>((String) row[0], (int) row[1]));
        }
        registrationsBarChart.getData().add(regSeries);
    }

    private void populateForm(Content c) {
        selectedContent = c;
        formTitle.setText(c.getTitle());
        formType.setValue(c.getType().name());
        formSynopsis.setText(c.getSynopsis());
        formYear.setText(String.valueOf(c.getReleaseYear()));
        formCoverUrl.setText(c.getCoverUrl() != null ? c.getCoverUrl() : "");
        formVideoUrl.setText(c.getVideoUrl() != null ? c.getVideoUrl() : "");
        formDuration.setText(String.valueOf(c.getDurationMin()));
        formCasting.setText(c.getCasting() != null ? c.getCasting() : "");
        formFeatured.setSelected(c.isFeatured());
        if (c.getCategory() != null) {
            formCategory.getItems().stream()
                .filter(cat -> cat.getId() == c.getCategory().getId())
                .findFirst().ifPresent(cat -> formCategory.setValue(cat));
        }
        formError.setVisible(false);
    }

    @FXML
    public void onNewContent() {
        selectedContent = null;
        clearForm();
    }

    @FXML
    public void onSaveContent() {
        formError.setVisible(false);
        try {
            Content c = selectedContent != null ? selectedContent : new Content();
            c.setTitle(formTitle.getText());
            c.setType(formType.getValue() != null ? Content.Type.valueOf(formType.getValue()) : null);
            c.setSynopsis(formSynopsis.getText());
            c.setReleaseYear(formYear.getText().isBlank() ? 0 : Integer.parseInt(formYear.getText()));
            c.setCoverUrl(formCoverUrl.getText());
            c.setVideoUrl(formVideoUrl.getText());
            c.setDurationMin(formDuration.getText().isBlank() ? 0 : Integer.parseInt(formDuration.getText()));
            c.setCasting(formCasting.getText());
            c.setCategory(formCategory.getValue());
            c.setFeatured(formFeatured.isSelected());

            if (selectedContent != null) {
                contentService.updateContent(c);
            } else {
                contentService.addContent(c);
            }
            loadAllData();
            clearForm();
            showInfo("Contenu sauvegardé avec succès!");
        } catch (Exception e) {
            formError.setText("Erreur: " + e.getMessage());
            formError.setVisible(true);
        }
    }

    @FXML
    public void onDeleteContent() {
        Content selected = contentTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Sélectionnez un contenu à supprimer."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer \"" + selected.getTitle() + "\" ?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    contentService.deleteContent(selected.getId());
                    loadAllData();
                } catch (Exception e) { showAlert("Erreur: " + e.getMessage()); }
            }
        });
    }

    @FXML
    public void onDeleteComment() {
        Comment selected = commentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Sélectionnez un commentaire."); return; }
        try {
            commentDAO.delete(selected.getId());
            loadAllData();
        } catch (Exception e) { showAlert("Erreur: " + e.getMessage()); }
    }

    @FXML
    public void onRefresh() { loadAllData(); }

    @FXML
    public void onLogout() {
        SessionManager.logout();
        try { Navigator.navigateTo("/com/jstream/view/Login.fxml", 900, 600); }
        catch (Exception e) { showAlert(e.getMessage()); }
    }

    private void clearForm() {
        selectedContent = null;
        formTitle.clear(); formSynopsis.clear(); formYear.clear();
        formCoverUrl.clear(); formVideoUrl.clear(); formDuration.clear();
        formCasting.clear(); formType.setValue(null); formCategory.setValue(null);
        formFeatured.setSelected(false); formError.setVisible(false);
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    private void showInfo(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }
}
