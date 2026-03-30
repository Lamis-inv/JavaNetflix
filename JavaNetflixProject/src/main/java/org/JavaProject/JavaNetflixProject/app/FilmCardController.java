package org.JavaProject.JavaNetflixProject.app;

import java.io.IOException;

import org.JavaProject.JavaNetflixProject.Entities.Film;

import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;

public class FilmCardController {

    @FXML
    private StackPane cardRoot;
    @FXML
    private ImageView posterImageView;
    @FXML
    private VBox infoBox;
    
    @FXML
    private Label titleLabel;
    @FXML
    private Label genreLabel;
    @FXML 
    private Region darkOverlay;
    
    private MediaView floatingMediaView;

    private Film film;
    private MediaPlayer previewPlayer;
    private MediaView mediaView;
    private PauseTransition hoverDelay;
    private static javafx.scene.layout.Pane hoverOverlay;
    private StackPane floatingCard;
    
    public void setFilm(Film film) {
        this.film = film;
        titleLabel.setText(film.getTitre());
        genreLabel.setText(film.getGenre());
        
        // Load poster image
        try {
            Image poster = new Image(getClass().getResourceAsStream(film.getCoverUrl()));
            posterImageView.setImage(poster);
        } catch (Exception e) {
            System.out.println("Could not load poster: " + film.getCoverUrl());
        }
        
        // Setup hover delay - 3 seconds
        hoverDelay = new PauseTransition(Duration.seconds(3));
        hoverDelay.setOnFinished(e -> playPreview());
        
        // Initialize media view (hidden)
        mediaView = new MediaView();
        mediaView.setPreserveRatio(true);
        mediaView.setVisible(false);
        mediaView.setMouseTransparent(true);
        
        // Add media view to card
        cardRoot.getChildren().add(0, mediaView);
        StackPane.setAlignment(mediaView, Pos.CENTER);
        
        // Bind media view to card size
        mediaView.setPreserveRatio(false);
        mediaView.fitWidthProperty().bind(cardRoot.widthProperty());
        mediaView.fitHeightProperty().bind(cardRoot.heightProperty());
        
        // Set hover handlers
        cardRoot.setOnMouseEntered(this::handleMouseEnter);
        cardRoot.setOnMouseExited(this::handleMouseExited);
        cardRoot.setOnMouseClicked(this::handleCardClick);
    }
    
    private void handleMouseEnter(MouseEvent e) {
        infoBox.setVisible(true);
        darkOverlay.setVisible(true);

        if (film.getUrlVideo() != null && !film.getUrlVideo().isEmpty()) {
            hoverDelay.playFromStart();
        }
    }
    
    private void handleMouseExited(MouseEvent e) {
        hoverDelay.stop();
        stopPreview();

        infoBox.setVisible(false);
        darkOverlay.setVisible(false);
    }
    
    private void playPreview() {
        if (hoverOverlay == null || film.getUrlVideo() == null) return;

        Bounds bounds = cardRoot.localToScene(cardRoot.getBoundsInLocal());

        floatingCard = new StackPane();
        floatingCard.setPrefSize(bounds.getWidth(), bounds.getHeight());
        floatingCard.setTranslateX(bounds.getMinX());
        floatingCard.setTranslateY(bounds.getMinY());
        floatingCard.getStyleClass().add("floating-card");

        // === VIDEO ONLY ===
        Media media = new Media(
            getClass().getResource(film.getUrlVideo()).toExternalForm()
        );

        previewPlayer = new MediaPlayer(media);
        previewPlayer.setVolume(0);
        previewPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        floatingMediaView = new MediaView(previewPlayer);
        floatingMediaView.setPreserveRatio(false);
        floatingMediaView.setFitWidth(bounds.getWidth());
        floatingMediaView.setFitHeight(bounds.getHeight());

        // Dark overlay ABOVE video
        Region overlay = new Region();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.35);");

        // Info box ABOVE overlay
        VBox infoClone = new VBox(
            new Label(film.getTitre()),
            new Label(film.getGenre())
        );
        infoClone.getStyleClass().add("floating-info");
        StackPane.setAlignment(infoClone, Pos.BOTTOM_LEFT);

        floatingCard.getChildren().addAll(
            floatingMediaView,
            overlay,
            infoClone
        );

        hoverOverlay.getChildren().add(floatingCard);

        // POP animation (video only)
        ScaleTransition pop = new ScaleTransition(Duration.millis(220), floatingCard);
        pop.setToX(1.4);
        pop.setToY(1.4);
        pop.play();

        previewPlayer.play();
    }
    
    private void stopPreview() {
        if (previewPlayer != null) {
            previewPlayer.stop();
            previewPlayer.dispose();
            previewPlayer = null;
        }

        if (floatingCard != null) {
            hoverOverlay.getChildren().remove(floatingCard);
            floatingCard = null;
        }
    }
    
    private void handleCardClick(MouseEvent event) {
        if (event.getClickCount() == 1) {
            stopPreview();
            openFilmDetail();
        }
    }
    
    private void openFilmDetail() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/FilmDetail.fxml")
            );
            Parent detailRoot = loader.load();
            
            FilmDetailController controller = loader.getController();
            controller.setFilm(this.film);
            
            Stage detailStage = new Stage();
            detailStage.setTitle(film.getTitre() + " - Netflix");
            detailStage.setScene(new Scene(detailRoot, 1000, 700));
            
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Cannot open film details");
            alert.setContentText("Could not load the detail page: " + e.getMessage());
            alert.showAndWait();
        }
    }
    public static void setHoverOverlay(javafx.scene.layout.Pane overlay) {
        hoverOverlay = overlay;
    }
}