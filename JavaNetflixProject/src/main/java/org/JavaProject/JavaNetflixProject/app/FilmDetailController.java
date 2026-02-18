package org.JavaProject.JavaNetflixProject.app;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.stage.Modality;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.JavaProject.JavaNetflixProject.Entities.FilmEntities;

public class FilmDetailController {
    
    @FXML private ImageView posterImage;
    @FXML private Label titleText;
    @FXML private Label yearLabel;
    @FXML private Label genreLabel;
    @FXML private Label descriptionText;
    @FXML private Label releaseDateLabel;
    @FXML private Label genreFullLabel;
    @FXML private Label fullDescriptionLabel;
    @FXML private Button likeButton;
    
    private FilmEntities currentFilm;
    private boolean isLiked = false;
    private MediaPlayer mediaPlayer;
    
    public void setFilm(FilmEntities film) {
        this.currentFilm = film;
        updateUI();
    }
    
    private void updateUI() {
        titleText.setText(currentFilm.getTitre());
        descriptionText.setText(currentFilm.getDescription());
        fullDescriptionLabel.setText(currentFilm.getDescription());
        genreLabel.setText(currentFilm.getGenre());
        genreFullLabel.setText(currentFilm.getGenre());

        if (currentFilm.getDateSortie() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            releaseDateLabel.setText(currentFilm.getDateSortie().format(formatter));
            yearLabel.setText(String.valueOf(currentFilm.getDateSortie().getYear()));
        } else {
            releaseDateLabel.setText("N/A");
            yearLabel.setText("N/A");
        }

        try {
            Image poster = new Image(getClass().getResourceAsStream(currentFilm.getCoverUrl()));
            posterImage.setImage(poster);
        } catch (Exception ignored) {}
    }

    @FXML
    private void playFilm() {
        if (currentFilm.getUrlVideo() == null || currentFilm.getUrlVideo().isEmpty()) {
            showAlert("Error", "No video URL available");
            return;
        }

        try {
            String mediaUrl = currentFilm.getUrlVideo().startsWith("http")
                    ? currentFilm.getUrlVideo()
                    : getClass().getResource(currentFilm.getUrlVideo()).toExternalForm();

            Media media = new Media(mediaUrl);
            mediaPlayer = new MediaPlayer(media);

            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setPreserveRatio(true);
            mediaView.setMouseTransparent(true);

            StackPane videoPane = new StackPane(mediaView);
            videoPane.setStyle("-fx-background-color: black;");

            mediaView.fitWidthProperty().bind(videoPane.widthProperty());
            mediaView.fitHeightProperty().bind(videoPane.heightProperty());

            VBox controls = createControls();

            BorderPane root = new BorderPane();
            root.setCenter(videoPane);
            root.setBottom(controls);

            Scene scene = new Scene(root, 1280, 720);
            
            // Create video stage
            Stage videoStage = new Stage();
            videoStage.setTitle(currentFilm.getTitre());
            videoStage.initOwner(titleText.getScene().getWindow());
            videoStage.initModality(Modality.NONE);
            videoStage.setScene(scene);
            
            scene.setOnKeyPressed(e -> {
                switch (e.getCode()) {
                    case SPACE:
                        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                            mediaPlayer.pause();
                        } else {
                            mediaPlayer.play();
                        }
                        break;
                    case LEFT:
                        mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));
                        break;
                    case RIGHT:
                        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
                        break;
                    case F:
                        videoStage.setFullScreen(!videoStage.isFullScreen());
                        break;
                }
            });

            mediaPlayer.setOnReady(mediaPlayer::play);

            videoStage.setOnCloseRequest(e -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            });

            videoStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Playback Error", e.getMessage());
        }
    }

    private VBox createControls() {
        VBox root = new VBox(6);
        root.setStyle(
            "-fx-padding: 12;" +
            "-fx-background-color: linear-gradient(to top, rgba(0,0,0,0.9), transparent);"
        );

        Label currentTime = new Label("00:00");
        Label totalTime = new Label("00:00");
        currentTime.setStyle("-fx-text-fill: white;");
        totalTime.setStyle("-fx-text-fill: white;");

        Slider timeSlider = new Slider(0, 100, 0);
        HBox.setHgrow(timeSlider, Priority.ALWAYS);

        mediaPlayer.currentTimeProperty().addListener((obs, old, now) -> {
            Duration total = mediaPlayer.getTotalDuration();
            if (total != null && !total.isUnknown() && !timeSlider.isValueChanging()) {
                timeSlider.setValue(now.toMillis() / total.toMillis() * 100);
            }
            currentTime.setText(formatTime(now));
        });

        mediaPlayer.totalDurationProperty().addListener((obs, old, total) -> {
            if (total != null) {
                totalTime.setText(formatTime(total));
            }
        });

        timeSlider.setOnMouseReleased(e -> {
            Duration total = mediaPlayer.getTotalDuration();
            if (total != null && !total.isUnknown()) {
                mediaPlayer.seek(total.multiply(timeSlider.getValue() / 100));
            }
        });

        HBox timeBox = new HBox(6, currentTime, timeSlider, totalTime);
        timeBox.setAlignment(Pos.CENTER);

        Button playPause = new Button("⏸");
        playPause.setOnAction(e -> {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playPause.setText("▶");
            } else {
                mediaPlayer.play();
                playPause.setText("⏸");
            }
        });

        Button back10 = new Button("⏪ 10s");
        back10.setOnAction(e -> {
            mediaPlayer.seek(
                mediaPlayer.getCurrentTime().subtract(Duration.seconds(10))
            );
        });

        Button forward10 = new Button("10s ⏩");
        forward10.setOnAction(e -> {
            mediaPlayer.seek(
                mediaPlayer.getCurrentTime().add(Duration.seconds(10))
            );
        });
        
        Button fullscreenBtn = new Button("⛶");
        fullscreenBtn.setOnAction(e -> {
            Stage stage = (Stage) fullscreenBtn.getScene().getWindow();
            stage.setFullScreen(!stage.isFullScreen());
            stage.setFullScreenExitHint("");
        });

        // Fix 3: volumeSlider was incorrectly named
        Slider volumeSlider = new Slider(0, 100, 80);
        mediaPlayer.setVolume(0.5);
        volumeSlider.valueProperty().addListener((o, a, b) ->
                mediaPlayer.setVolume(b.doubleValue() / 100)
        );

        Label volumeLabel = new Label("🔊");
        volumeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        HBox buttons = new HBox(18,
                back10,
                playPause,
                forward10,
                volumeLabel,
                volumeSlider, 
                fullscreenBtn
        );
        buttons.setAlignment(Pos.CENTER);

        buttons.getChildren().forEach(n ->
            n.setStyle("-fx-text-fill: white; -fx-background-color: transparent;")
        );
        
        styleBigButton(playPause);
        styleBigButton(back10);
        styleBigButton(forward10);
        styleBigButton(fullscreenBtn);

        root.getChildren().addAll(timeBox, buttons);
        return root;
    }

    private String formatTime(Duration d) {
        if (d == null || d.isUnknown()) return "00:00";
        int s = (int) d.toSeconds();
        return String.format("%02d:%02d", s / 60, s % 60);
    }
    
    private void styleBigButton(Button btn) {
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;"
        );
        btn.setMinWidth(90);
        btn.setMinHeight(40);
    }

    @FXML private void toggleLike() { isLiked = !isLiked;
    if (isLiked) { 
    	likeButton.setText("❤ Liked"); 
    	likeButton.getStyleClass().remove("action-button"); 
    	likeButton.getStyleClass().add("liked-button"); 
    	} 
    else { 
    	likeButton.setText("Like"); 
    	likeButton.getStyleClass().remove("liked-button");
    likeButton.getStyleClass().add("action-button");
    } 
    }

    @FXML
    private void addToList() {
        showAlert("Added", currentFilm.getTitre() + " added to your list!");
    }

    @FXML
    private void goBack() {
        ((Stage) titleText.getScene().getWindow()).close();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}