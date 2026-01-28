package org.JavaProject.JavaNetflixProject.app;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

public class LoginController {
	
	@FXML private Button loginButton;
    @FXML private Hyperlink goToSignUp;

    @FXML
    public void initialize() {
        goToSignUp.setOnAction(e -> {
            try {
                Parent signUpRoot = FXMLLoader.load(getClass().getResource("/ui/SignUpPage.fxml"));
                Stage stage = (Stage) goToSignUp.getScene().getWindow();
                stage.setScene(new Scene(signUpRoot, 400, 500));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        loginButton.setOnAction(e -> {
        	System.out.println("I am logged in");
        });

       
    }

}
