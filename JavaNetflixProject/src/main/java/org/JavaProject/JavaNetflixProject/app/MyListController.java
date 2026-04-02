package org.JavaProject.JavaNetflixProject.app;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.JavaProject.JavaNetflixProject.DAO.WatchlistDAO;
import org.JavaProject.JavaNetflixProject.Entities.Content;
import org.JavaProject.JavaNetflixProject.Services.ContentService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import org.JavaProject.JavaNetflixProject.Utils.Session;

public class MyListController {

    @FXML private FlowPane contentGrid;
    @FXML private Label usernameLabel;

    private final WatchlistDAO watchlistDAO = new WatchlistDAO();
    private final ContentService contentService = new ContentService();

    @FXML
    public void initialize() {
        usernameLabel.setText("Ma liste — " + Session.getCurrentUser().getNom());
        loadWatchlist();
    }

    private void loadWatchlist() {
        contentGrid.getChildren().clear();
        try {
            List<Integer> ids = watchlistDAO.getWatchlistContentIds(Session.getCurrentUser().getId());
            List<Content> myList = new ArrayList<>();
            for (int id : ids) {
                Content c = contentService.getById(id);
                if (c != null) myList.add(c);
            }

            if (myList.isEmpty()) {
                Label empty = new Label("Votre liste est vide. Ajoutez des films et séries !");
                empty.setStyle("-fx-text-fill: #888; -fx-font-size: 16px; -fx-padding: 40;");
                contentGrid.getChildren().add(empty);
                return;
            }

            for (Content c : myList) {
                contentGrid.getChildren().add(buildCard(c));
            }
        } catch (SQLException e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    private VBox buildCard(Content c) {
        VBox card = new VBox(6);
        card.getStyleClass().add("content-card");
        card.setPrefWidth(160);
        card.setCursor(javafx.scene.Cursor.HAND);

        ImageView img = new ImageView();
        img.setFitWidth(155);
        img.setFitHeight(220);
        img.setPreserveRatio(false);
        if (c.getCoverUrl() != null && !c.getCoverUrl().isBlank()) {
            try { img.setImage(new Image(c.getCoverUrl(), 155, 220, false, true, true)); }
            catch (Exception ignored) {}
        }

        Label titleLbl = new Label(c.getTitle());
        titleLbl.getStyleClass().add("card-title");
        titleLbl.setWrapText(true); titleLbl.setMaxWidth(155);

        Label badge = new Label(c.isFilm() ? "FILM" : "SÉRIE");
        badge.getStyleClass().add(c.isFilm() ? "badge-film" : "badge-serie");

        Label ratingLbl = new Label("⭐ " + String.format("%.1f", c.getAvgRating()));
        ratingLbl.getStyleClass().add("card-rating");

        card.getChildren().addAll(img, badge, titleLbl, ratingLbl);
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
        } catch (IOException e) {
            showAlert("Impossible d'ouvrir le détail: " + e.getMessage());
        }
    }

    @FXML
    public void onBack() {
        try { Navigator.navigateTo("/ui/home.fxml", 1280, 800); }
        catch (Exception e) { showAlert(e.getMessage()); }
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }
}
