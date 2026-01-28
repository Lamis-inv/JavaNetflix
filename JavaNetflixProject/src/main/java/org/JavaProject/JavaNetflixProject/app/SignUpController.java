package org.JavaProject.JavaNetflixProject.app;


import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

public class SignUpController {

	@FXML private Button signUpButton;
    @FXML private Hyperlink goToLogin;

    @FXML
    public void initialize() {
        goToLogin.setOnAction(e -> {
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("/ui/LoginPage.fxml"));
                Stage stage = (Stage) goToLogin.getScene().getWindow();
                stage.setScene(new Scene(loginRoot, 400, 500));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        signUpButton.setOnAction(e -> {
        	System.out.println("I am SignedUp in");
        });

    
    }
}
