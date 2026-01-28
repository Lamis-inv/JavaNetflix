package org.JavaProject.JavaNetflixProject.app;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class HomeController {
	
	@FXML
    private HBox trendingRow;

    @FXML
    private HBox actionRow;
    @FXML
    private HBox comedyRow;
    @FXML
    private HBox horrorRow;

    @FXML
    public void initialize() {
        addMovie(trendingRow, "Shrek 4", "/images/shrek4.jpg");
        addMovie(actionRow, "Damsel", "/images/damsel.jpg");
        addMovie(actionRow, "Bumble bee", "/images/bumblebee.png");
        addMovie(horrorRow, "five nights at freddy's 2", "/images/fnaf2.png");
        addMovie(actionRow, "Free Guy", "/images/freeguy.jpg");
        addMovie(comedyRow, "Kung Fu Panda 4", "/images/kungfupanda4.jpg");
        //addMovie(actionRow, "Mortal Engines", "/images/mortalengines.jfif");
        addMovie(trendingRow, "Sing", "/images/sing.jpg");
        addMovie(trendingRow, "Zootopia 2", "/images/zootopia2.jpg");
        addMovie(trendingRow, "Tetris", "/images/tetris.jpg");
        addMovie(actionRow, "Uncharted", "/images/uncharted.jpg");
    }

    private void addMovie(HBox row, String title, String imagePath) {

        ImageView poster = new ImageView(
                new Image(getClass().getResourceAsStream(imagePath))
        );
        poster.setFitWidth(120);
        poster.setFitHeight(180);

        Label label = new Label(title);

        VBox card = new VBox(5, poster, label);
        card.setStyle("-fx-alignment: center;");

        row.getChildren().add(card);
    }

}
