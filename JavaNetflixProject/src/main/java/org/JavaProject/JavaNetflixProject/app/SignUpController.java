package org.JavaProject.JavaNetflixProject.app;


import java.io.IOException;

import org.JavaProject.JavaNetflixProject.Services.AuthService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignUpController {

	@FXML private TextField username;
    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmpassword;
    @FXML private Hyperlink goToLogin;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();
    
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        goToLogin.setOnAction(e -> {
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("/ui/LoginPage.fxml"));
                Stage stage = (Stage) goToLogin.getScene().getWindow();
                stage.setScene(new Scene(loginRoot, 400, 500));
            } catch (IOException ex) {
            	errorLabel.setText(ex.getMessage());
                errorLabel.setVisible(true);
            }
        });
    }
    @FXML
    public void onRegister() {
        errorLabel.setVisible(false);
        try {
            authService.register(username.getText(), email.getText(),password.getText(), confirmpassword.getText());
            // Auto-login after registration
            authService.login(email.getText(), password.getText());
            Navigator.navigateTo("/ui/home.fxml", 1280, 800);
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        }
        
        
    }

}
