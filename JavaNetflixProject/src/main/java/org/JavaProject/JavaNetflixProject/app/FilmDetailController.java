package org.JavaProject.JavaNetflixProject.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.JavaProject.JavaNetflixProject.Entities.FilmEntities;



public class FilmDetailController {
    
    @FXML private ImageView posterImage;
    @FXML private Label titleText;           // Using Label instead of Text
    @FXML private Label yearLabel;
    @FXML private Label genreLabel;
    @FXML private Label descriptionText;     // Using Label
    @FXML private Label releaseDateLabel;
    @FXML private Label genreFullLabel;
    @FXML private Label fullDescriptionLabel;
    @FXML private Button likeButton;
    
    private FilmEntities currentFilm;
    private boolean isLiked = false;
    
    public void setFilm(FilmEntities film) {
        this.currentFilm = film;
        updateUI();
    }
    
    private void updateUI() {
        // Set basic information
        titleText.setText(currentFilm.getTitre());
        descriptionText.setText(currentFilm.getDescription());
        fullDescriptionLabel.setText(currentFilm.getDescription());
        genreLabel.setText(currentFilm.getGenre());
        genreFullLabel.setText(currentFilm.getGenre());
        
        // Format and set release date
        if (currentFilm.getDateSortie() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            String formattedDate = currentFilm.getDateSortie().format(formatter);
            releaseDateLabel.setText(formattedDate);
            yearLabel.setText(String.valueOf(currentFilm.getDateSortie().getYear()));
        } else {
            releaseDateLabel.setText("Not available");
            yearLabel.setText("N/A");
        }
        
        // Load image
        try {
            Image poster = new Image(getClass().getResourceAsStream(currentFilm.getCoverUrl()));
            posterImage.setImage(poster);
        } catch (Exception e) {
            System.out.println("Could not load image: " + currentFilm.getCoverUrl());
        }
    }
    
    @FXML
    private void playFilm() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Play Film");
        alert.setHeaderText("Playing: " + currentFilm.getTitre());
        alert.setContentText("Video URL: " + currentFilm.getUrlVideo());
        alert.showAndWait();
    }
    
    @FXML
    private void addToList() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Added to List");
        alert.setHeaderText("Success!");
        alert.setContentText(currentFilm.getTitre() + " has been added to your list!");
        alert.showAndWait();
    }
    
    @FXML
    private void toggleLike() {
        isLiked = !isLiked;
        if (isLiked) {
            likeButton.setText("❤ Liked");
            likeButton.getStyleClass().remove("action-button");
            likeButton.getStyleClass().add("liked-button");
        } else {
            likeButton.setText("Like");
            likeButton.getStyleClass().remove("liked-button");
            likeButton.getStyleClass().add("action-button");
        }
    }
    
    @FXML
    private void goBack() {
        Stage stage = (Stage) titleText.getScene().getWindow();
        stage.close();
    }
}