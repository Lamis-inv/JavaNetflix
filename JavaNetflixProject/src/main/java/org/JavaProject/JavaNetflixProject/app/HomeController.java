package org.JavaProject.JavaNetflixProject.app;
import org.JavaProject.JavaNetflixProject.app.DetailController;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.JavaProject.JavaNetflixProject.Entities.Content;
import org.JavaProject.JavaNetflixProject.Entities.Category;
import org.JavaProject.JavaNetflixProject.Services.ContentService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import org.JavaProject.JavaNetflixProject.Utils.Session;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class HomeController {

    @FXML private VBox categoriesContainer;
    @FXML private Label usernameLabel;
    @FXML private TextField searchField;
    @FXML private ImageView featuredCover;
    @FXML private Label featuredTitle;
    @FXML private Label featuredSynopsis;

    private final ContentService contentService = new ContentService();
    private List<Content> featuredContent;

    @FXML
    public void initialize() {
    	usernameLabel.setText("Bonjour, " + Session.getCurrentUser().getNom());
        loadFeatured();
        loadCategories();
    }

    /** FEATURED */
    private int featuredIndex = 0;
    private List<Content> featuredList;

    private void loadFeatured() {
        try {
            featuredList = contentService.getFeaturedContent();
            if (featuredList == null || featuredList.isEmpty()) return;

            updateFeaturedBanner();

            // Slide every 5 seconds
            Timeline slider = new Timeline(new KeyFrame(Duration.seconds(5), e -> {
                featuredIndex = (featuredIndex + 1) % featuredList.size();
                updateFeaturedBanner();
            }));
            slider.setCycleCount(Timeline.INDEFINITE);
            slider.play();

        } catch (SQLException e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    private void updateFeaturedBanner() {
        Content c = featuredList.get(featuredIndex);

        featuredTitle.setText(c.getTitle());
        featuredSynopsis.setText(
            c.getSynopsis() != null && !c.getSynopsis().isBlank()
                ? c.getSynopsis().substring(0, Math.min(150, c.getSynopsis().length())) + "..."
                : ""
        );

       
            featuredCover.setImage(new Image(c.getCoverUrl(), true)); // fallback
        
    }

    /** CATEGORIES */
    private void loadCategories() {
        try {
            categoriesContainer.getChildren().clear();

            List<Category> categories = contentService.getAllCategories();

            for (Category cat : categories) {
                List<Content> items = contentService.getByCategory(cat.getId());
                if (items == null || items.isEmpty()) continue;

                VBox categoryBox = new VBox(10);

                Label catLabel = new Label(cat.getName());
                catLabel.getStyleClass().add("category-title");

                HBox row = new HBox(15);

                for (Content c : items) {
                    row.getChildren().add(buildCard(c));
                }

                ScrollPane scrollPane = new ScrollPane(row);
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setFitToHeight(true);

                categoryBox.getChildren().addAll(catLabel, scrollPane);
                categoriesContainer.getChildren().add(categoryBox);
            }

        } catch (SQLException e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    /** CARD */
    private VBox buildCard(Content c) {
        VBox card = new VBox(6);
        card.getStyleClass().add("content-card");
        card.setPrefWidth(160);

        ImageView img = new ImageView();
        img.setFitWidth(155);
        img.setFitHeight(220);

        if (c.getCoverUrl() != null && !c.getCoverUrl().isBlank()) {
            img.setImage(new Image(c.getCoverUrl(), 155, 220, false, true, true));
        }

        Label titleLbl = new Label(c.getTitle());
        titleLbl.getStyleClass().add("card-title");
        titleLbl.setWrapText(true);

        card.getChildren().addAll(img, titleLbl);

        // 👉 click opens detail (you can implement later)
        card.setOnMouseClicked(e -> openDetail(c));

        return card;
    }

    /** SEARCH (FIXED) */
    @FXML
    public void onSearch() {
        String kw = searchField.getText().trim();

        if (kw.isEmpty()) {
            loadCategories();
            return;
        }

        try {
            categoriesContainer.getChildren().clear();

            List<Content> results = contentService.search(kw);

            VBox resultBox = new VBox(10);
            Label title = new Label("Résultats: " + kw);

            HBox row = new HBox(15);
            for (Content c : results) {
                row.getChildren().add(buildCard(c));
            }

            ScrollPane sp = new ScrollPane(row);
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

            resultBox.getChildren().addAll(title, sp);
            categoriesContainer.getChildren().add(resultBox);

        } catch (SQLException e) {
            showAlert("Erreur: " + e.getMessage());
        }
    }

    @FXML
    public void onClearSearch() {
        searchField.clear();
        loadCategories();
    }

    @FXML
    public void onMyList() {
        try { Navigator.navigateTo("/ui/MyList.fxml", 1280, 800); }
        catch (Exception e) { showAlert(e.getMessage()); }
    }

    @FXML
    public void onLogout() {
        Session.logout();
        try { Navigator.navigateTo("/ui/LoginPage.fxml", 900, 600); }
        catch (Exception e) { showAlert(e.getMessage()); }
    }

    private void openDetail(Content c) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ui/Detail.fxml")
            );

            Parent root = loader.load();

            // Get controller
            DetailController controller = loader.getController();

            // Send content to detail page
            controller.setContent(c);

            // Change scene
            Scene scene = new Scene(root, 1280, 800);
            Navigator.getPrimaryStage().setScene(scene);

        } catch (IOException e) {
            showAlert("Erreur ouverture détail: " + e.getMessage());
        }
    }
    
    @FXML
    public void onBack() {
        try {
			Navigator.navigateTo("/ui/home.fxml", 1280, 800);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    private void showAlert(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
    
}