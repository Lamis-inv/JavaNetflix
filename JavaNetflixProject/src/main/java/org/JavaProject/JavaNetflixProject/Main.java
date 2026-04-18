package org.JavaProject.JavaNetflixProject;

import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    private double dragOffsetX, dragOffsetY;
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNDECORATED);

        Navigator.setPrimaryStage(primaryStage);
        primaryStage.setTitle("Notflix");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        // Build title bar and store it in Navigator
        HBox titleBar = buildTitleBar(primaryStage);
        Navigator.setTitleBar(titleBar); // ← key line

        // Let Navigator handle everything from here
        Navigator.navigateTo("/ui/Splash.fxml");
    }

    private HBox buildTitleBar(Stage stage) {
        Label title = new Label("NOTFLIX");
        title.setStyle(
            "-fx-text-fill: #c0c0c0;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-font-family: 'Arial Black', Arial, sans-serif;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button minimizeBtn = winBtn("─", false);
        Button maximizeBtn = winBtn("□", false);
        Button closeBtn    = winBtn("✕", true);

        minimizeBtn.setOnAction(e -> stage.setIconified(true));
        maximizeBtn.setOnAction(e -> {
            stage.setMaximized(!stage.isMaximized());
            maximizeBtn.setText(stage.isMaximized() ? "❐" : "□");
        });
        closeBtn.setOnAction(e -> stage.close());

        HBox bar = new HBox(4, title, spacer, minimizeBtn, maximizeBtn, closeBtn);
        bar.setPrefHeight(30);
        bar.setMaxHeight(30);
        bar.setMinHeight(30);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setPadding(new Insets(0, 4, 0, 14));
        bar.setStyle(
            "-fx-background-color: #0d0d0d;" +
            "-fx-border-color: transparent transparent rgba(255,255,255,0.06) transparent;" +
            "-fx-border-width: 0 0 1 0;"
        );

        bar.setOnMousePressed(e -> {
            dragOffsetX = e.getSceneX();
            dragOffsetY = e.getSceneY();
        });
        bar.setOnMouseDragged(e -> {
            if (!stage.isMaximized()) {
                stage.setX(e.getScreenX() - dragOffsetX);
                stage.setY(e.getScreenY() - dragOffsetY);
            }
        });
        bar.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
                maximizeBtn.setText(stage.isMaximized() ? "❐" : "□");
            }
        });
        

        return bar;
    }

    private Button winBtn(String symbol, boolean isClose) {
        String base =
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #888888;" +
            "-fx-font-size: 11px;" +
            "-fx-min-width: 38px;" +
            "-fx-min-height: 30px;" +
            "-fx-max-height: 30px;" +
            "-fx-border-color: transparent;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 0;";

        Button btn = new Button(symbol);
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(base +
            (isClose
                ? "-fx-background-color: #c42b1c; -fx-text-fill: white;"
                : "-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white;")));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    public static void main(String[] args) {
        launch(args);
    }
}