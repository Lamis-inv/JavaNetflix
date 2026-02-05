package org.JavaProject.JavaNetflixProject.app;

import java.io.IOException;
import java.util.List;

import org.JavaProject.JavaNetflixProject.Entities.Movie;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private HBox advantureRow;
    @FXML private ScrollPane scrollPane;

    @FXML
    private void scrollRight() {
        scrollPane.setHvalue(scrollPane.getHvalue() + 0.2);
    }
    @FXML
    private void scrollLeft() {
        scrollPane.setHvalue(scrollPane.getHvalue() - 0.2);
    }
    
    @FXML
    public void initialize() {
        addMovie(trendingRow, "Shrek 4", "/images/shrek4.jpg");
        addMovie(actionRow, "Damsel", "/images/damsel.jpg");
        addMovie(actionRow, "Bumble bee", "/images/bumblebee.png");
        addMovie(horrorRow, "five nights at freddy's 2", "/images/fnaf2.png");
        addMovie(actionRow, "Free Guy", "/images/freeguy.jpg");
        addMovie(comedyRow, "Kung Fu Panda 4", "/images/kungfupanda4.jpg");
        addMovie(actionRow, "Mortal Engines", "/images/mortalengines.png");
        addMovie(trendingRow, "Sing", "/images/sing.jpg");
        addMovie(trendingRow, "Zootopia 2", "/images/zootopia2.jpg");
        addMovie(trendingRow, "Tetris", "/images/tetris.jpg");
        addMovie(actionRow, "Uncharted", "/images/uncharted.jpg");
        addMovie(actionRow, "Antman", "/images/antman.jpg");
        addMovie(actionRow, "Guardian of the galaxy", "/images/guardiansofthegalaxy.jpg");
        addMovie(actionRow, "Spiderman far from home", "/images/spidermanfarfromhome.jpg");
        addMovie(actionRow, "Spiderman home coming", "/images/spidermanhomecoming.jpg");
        addMovie(actionRow, "Spiderman no way home", "/images/spidermannowayhome.jpg");
        addMovie(advantureRow, "Ready Player One", "/images/readyplayerone.jpg");
        addMovie(trendingRow, "KPop Demon Hunters", "/images/kpopdemonhunters.jpg");
        addMovie(actionRow, "Ironman 3", "/images/ironman3.jpeg");
        addMovie(advantureRow, "Lego Movie Batman", "/images/legomoviebatman.jpg");
        addMovie(trendingRow, "Barbie as the island princess", "/images/barbieastheislandprincess.jpeg");
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
    
    
   /* private void loadMovies(String category, HBox row) throws IOException {
        List<Movie> movies = movieDAO.getByCategory(category);

        for (Movie movie : movies) {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/movie_card.fxml")
            );
            Node card = loader.load();

            MovieCardController controller = loader.getController();
            controller.setMovie(movie);

            row.getChildren().add(card);
        }
    }*/

}
