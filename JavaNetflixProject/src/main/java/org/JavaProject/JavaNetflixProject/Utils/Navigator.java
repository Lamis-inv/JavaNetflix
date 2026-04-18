package org.JavaProject.JavaNetflixProject.Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;

public class Navigator {

    private static Stage primaryStage;
    private static Node  titleBar;   // ← stored once, reused forever
    private static final int RESIZE_MARGIN = 5;
    

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
        stage.setResizable(true);
    }

    public static void setTitleBar(Node bar) {
        titleBar = bar;
    }

    public static Stage getPrimaryStage() { return primaryStage; }

    public static void navigateTo(String fxmlPath) throws IOException {
        if (primaryStage == null)
            throw new IllegalStateException("Navigator.setPrimaryStage() not called");

        var url = Navigator.class.getClassLoader().getResource(fxmlPath.substring(1));
        if (url == null) throw new RuntimeException("FXML NOT FOUND: " + fxmlPath);

        FXMLLoader loader = new FXMLLoader(url);
        Parent content;
        try {
            content = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        BorderPane root = new BorderPane();
        if (titleBar != null) root.setTop(titleBar); // ← always reattach
        root.setCenter(content);
        root.setStyle("-fx-background-color: #0a0a0a;");

        

        root.setOnMouseMoved(e -> {
            Scene scene = root.getScene();
            Stage stage = (Stage) scene.getWindow();

            if (e.getX() < RESIZE_MARGIN) {
                scene.setCursor(javafx.scene.Cursor.W_RESIZE);
            } else if (e.getX() > root.getWidth() - RESIZE_MARGIN) {
                scene.setCursor(javafx.scene.Cursor.E_RESIZE);
            } else if (e.getY() < RESIZE_MARGIN) {
                scene.setCursor(javafx.scene.Cursor.N_RESIZE);
            } else if (e.getY() > root.getHeight() - RESIZE_MARGIN) {
                scene.setCursor(javafx.scene.Cursor.S_RESIZE);
            } else {
                scene.setCursor(javafx.scene.Cursor.DEFAULT);
            }
        });
        
        root.setOnMouseDragged(e -> {
            Scene scene = root.getScene();
            if (scene == null) return;

            Stage stage = (Stage) scene.getWindow();

            if (scene.getCursor() == javafx.scene.Cursor.E_RESIZE) {
                stage.setWidth(e.getX());
            } else if (scene.getCursor() == javafx.scene.Cursor.S_RESIZE) {
                stage.setHeight(e.getY());
            } else if (scene.getCursor() == javafx.scene.Cursor.W_RESIZE) {
                double newWidth = stage.getWidth() - (e.getScreenX() - stage.getX());
                if (newWidth > 800) {
                    stage.setX(e.getScreenX());
                    stage.setWidth(newWidth);
                }
            } else if (scene.getCursor() == javafx.scene.Cursor.N_RESIZE) {
                double newHeight = stage.getHeight() - (e.getScreenY() - stage.getY());
                if (newHeight > 600) {
                    stage.setY(e.getScreenY());
                    stage.setHeight(newHeight);
                }
            }
        });
        
        Rectangle clip = new Rectangle();
        clip.setArcWidth(12); clip.setArcHeight(12);
        clip.widthProperty().bind(root.widthProperty());
        clip.heightProperty().bind(root.heightProperty());
        root.setClip(clip);

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
            double w = Math.max(primaryStage.getWidth(), 800);
            double h = Math.max(primaryStage.getHeight(), 600);
            Scene scene = new Scene(root, w, h);
            ThemeManager.applyTheme(scene);
            primaryStage.setScene(scene);
        }

        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void navigateTo(String fxmlPath, double ignoredW, double ignoredH)
            throws IOException {
        navigateTo(fxmlPath);
    }
}