package org.JavaProject.JavaNetflixProject.app;
import java.awt.TextArea;
import java.io.IOException;

import org.JavaProject.JavaNetflixProject.Entities.FilmEntities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;


public class FilmCardController {

    @FXML
    private StackPane cardRoot;

    @FXML
    private ImageView posterImage;

    @FXML
    private VBox infoBox;

    @FXML
    private Label titleLabel;

    @FXML
    private Label genreLabel;

    @FXML
    private Button addToListButton;
    @FXML
    private ImageView posterImageView;

    private FilmEntities film;

    public void setFilm(FilmEntities film) {
        this.film = film;
        titleLabel.setText(film.getTitre());
        genreLabel.setText(film.getGenre());
        try {
            Image poster = new Image(getClass().getResourceAsStream(film.getCoverUrl()));
            posterImageView.setImage(poster);
        } catch (Exception e) {
            System.out.println("Could not load poster: " + film.getCoverUrl());
        }
        // Hover effects
        cardRoot.setOnMouseEntered(this::handleMouseEnter);
        cardRoot.setOnMouseExited(this::handleMouseExit);
        cardRoot.setOnMouseClicked(this::handleCardClick);
    }

    private void handleMouseEnter(MouseEvent e) {
        infoBox.setVisible(true);
        cardRoot.setScaleX(1.0);
        cardRoot.setScaleY(1.0);
    }

    private void handleMouseExit(MouseEvent e) {
        infoBox.setVisible(false);
        cardRoot.setScaleX(1.0);
        cardRoot.setScaleY(1.0);
    }
    private void handleCardClick(MouseEvent event) {
        if (event.getClickCount() == 1) { // Single click
            openFilmDetail();
        }
    }
    
    private void openFilmDetail() {
        try {
            // NOTE: Changed from /views/ to /ui/
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/FilmDetail.fxml")
            );
            Parent detailRoot = loader.load();
            
            FilmDetailController controller = loader.getController();
            controller.setFilm(this.film);
            
            Stage detailStage = new Stage();
            detailStage.setTitle(film.getTitre() + " - Netflix");
            detailStage.setScene(new Scene(detailRoot, 1000, 700));
            
            // Apply CSS - also check this path
            try {
                detailStage.getScene().getStylesheets().add(
                    getClass().getResource("/css/film-detail.css").toExternalForm()
                );
            } catch (Exception e) {
                System.err.println("CSS not loaded: " + e.getMessage());
            }
            
            detailStage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to open film detail: " + e.getMessage());
            
            // Show error to user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open film details");
            alert.setContentText("Could not load the detail page: " + e.getMessage());
            alert.showAndWait();
        }
    }


    



}
