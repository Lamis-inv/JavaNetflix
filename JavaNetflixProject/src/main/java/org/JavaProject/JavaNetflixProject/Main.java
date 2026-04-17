package org.JavaProject.JavaNetflixProject;
import java.io.IOException;

import org.JavaProject.JavaNetflixProject.Utils.Navigator;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
	    Navigator.setPrimaryStage(primaryStage);
	    primaryStage.setTitle("NOTFLIX");
	    primaryStage.setMinWidth(900);
	    primaryStage.setMinHeight(600);
	    Navigator.navigateTo("/ui/Splash.fxml", 900, 600); // ← changed from LoginPage
	}
    public static void main(String[] args) {
        launch(args);
    }
}
