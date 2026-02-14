package org.JavaProject.JavaNetflixProject;
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

public class Main extends Application {

	private static Scene scene;
	
    @Override
    public void start(Stage stage) throws IOException {

    	/*scene = new Scene(loadFXML("/ui/home"),1200, 720);
        stage.setScene(scene);


        */
    	
    	
    	Parent root = FXMLLoader.load(getClass().getResource("/ui/home.fxml"));
        Scene scene = new Scene(root, 1200, 680);
        scene.getStylesheets().add(
        	    Main.class.getResource("/css/netflix.css").toExternalForm());

        
        stage.setTitle("J-Stream");
        stage.setScene(scene);
        stage.show();
    }

  
    public static void main(String[] args) {
        launch(args);
    }
}
