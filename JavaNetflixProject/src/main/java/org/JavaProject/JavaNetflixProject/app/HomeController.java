package org.JavaProject.JavaNetflixProject.app;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import org.JavaProject.JavaNetflixProject.DAO.FilmDAO;
import org.JavaProject.JavaNetflixProject.Entities.FilmEntities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class HomeController {
	
	private FilmDAO filmDAO = new FilmDAO();
	
	@FXML
    private HBox trendingRow;

    @FXML
    private HBox actionRow;
    @FXML
    private HBox comedyRow;
    @FXML
    private HBox horrorRow;
    @FXML
    private HBox adventureRow;
  
    
    @FXML
    public void initialize() {
        try {
            loadFilms("trending", trendingRow);
            loadFilms("action", actionRow);
            loadFilms("comedy", comedyRow);
            loadFilms("horror", horrorRow);
            loadFilms("adventure", adventureRow);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    private void addMovie(HBox row, String title, String imagePath) {

        ImageView poster = new ImageView();
        poster.setFitWidth(120);
        poster.setFitHeight(180);
        poster.setPreserveRatio(true);

        try {
            URL url = getClass().getResource(imagePath);
            if (url == null) {
                System.out.println("❌ Resource not found: " + imagePath);
                return;
            }

            Image image = new Image(url.toExternalForm(), true);
            poster.setImage(image);

        } catch (Exception e) {
            System.out.println("❌ Failed to load image: " + imagePath);
            e.printStackTrace();
        }

        Label label = new Label(title);
        label.getStyleClass().add("movie-title");

        VBox card = new VBox(5, poster, label);
        card.getStyleClass().add("movie-card");
        card.setAlignment(Pos.CENTER);

        row.getChildren().add(card);
    }


    
    
    private void loadFilms(String genre, HBox row) throws IOException {
    	

        List<FilmEntities> films = filmDAO.getByGenre(genre);

        for (FilmEntities film : films) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/movie_card.fxml"));
            Node card = loader.load();

            FilmCardController controller = loader.getController();
            controller.setFilm(film);

            row.getChildren().add(card);
        }
    }

}
