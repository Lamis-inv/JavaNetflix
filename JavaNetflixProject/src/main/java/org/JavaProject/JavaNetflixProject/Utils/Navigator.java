package org.JavaProject.JavaNetflixProject.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Navigator {
    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) { primaryStage = stage; }
    public static Stage getPrimaryStage() { return primaryStage; }

    public static <T> T navigateTo(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Navigator.class.getResource("/css/netflix.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        return loader.getController();
    }

    public static <T> T navigateTo(String fxmlPath, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add(Navigator.class.getResource("/css/netflix.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.centerOnScreen();
        primaryStage.show();
        return loader.getController();
    }
}
