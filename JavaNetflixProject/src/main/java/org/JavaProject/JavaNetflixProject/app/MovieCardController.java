package org.JavaProject.JavaNetflixProject.app;

import org.JavaProject.JavaNetflixProject.Entities.Movie;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MovieCardController {
	@FXML private ImageView poster;
    @FXML private VBox overlay;
    @FXML private Label title;
    @FXML private Label info;

    public void setMovie(Movie movie) {
        poster.setImage(new Image(movie.getPosterUrl()));
        title.setText(movie.getTitle());
        info.setText(movie.getDuration() + " • " + movie.getGenre());
    }

    @FXML
    private void initialize() {
        overlay.setVisible(false);

        poster.setOnMouseEntered(e -> overlay.setVisible(true));
        poster.setOnMouseExited(e -> overlay.setVisible(false));
    }
}
