package org.JavaProject.JavaNetflixProject.Utils;

import javafx.fxml.FXMLLoader;
import org.JavaProject.JavaNetflixProject.Utils.ThemeManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Central navigation helper.
 *  - First call  → maximise to screen, set resizable=true
 *  - Later calls → keep current stage size / maximised state intact
 *  - Legacy navigateTo(path, w, h) overload kept for compilation; sizes ignored
 */
public class Navigator {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        stage.setResizable(true);
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void navigateTo(String fxmlPath) throws IOException {
        if (primaryStage == null)
            throw new IllegalStateException("Navigator.setPrimaryStage() not called");

        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxmlPath));
        Parent root = loader.load();

        boolean firstLoad = primaryStage.getScene() == null;

        if (firstLoad) {
            javafx.geometry.Rectangle2D screen = Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screen.getWidth(), screen.getHeight());
            ThemeManager.applyTheme(scene);
            primaryStage.setScene(scene);
            primaryStage.setX(screen.getMinX());
            primaryStage.setY(screen.getMinY());
            primaryStage.setMaximized(true);
        } else {
            double w = Math.max(primaryStage.getWidth(),  800);
            double h = Math.max(primaryStage.getHeight(), 600);
            Scene scene = new Scene(root, w, h);
            ThemeManager.applyTheme(scene);
            primaryStage.setScene(scene);
        }

        primaryStage.setResizable(true);
        primaryStage.show();
    }

    /** Legacy overload — sizes ignored, current stage size preserved */
    public static void navigateTo(String fxmlPath, double ignoredW, double ignoredH)
            throws IOException {
        navigateTo(fxmlPath);
    }
}