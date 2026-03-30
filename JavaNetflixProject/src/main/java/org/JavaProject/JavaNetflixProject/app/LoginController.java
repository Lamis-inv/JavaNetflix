package org.JavaProject.JavaNetflixProject.app;

import java.io.IOException;

import org.JavaProject.JavaNetflixProject.Entities.User;
import org.JavaProject.JavaNetflixProject.Services.AuthService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class LoginController {
	
	@FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;
	@FXML private Button loginButton;
	@FXML private Label errorLabel;
    @FXML private Hyperlink goToSignUp;

    @FXML
    public void initialize() {
        goToSignUp.setOnAction(e -> {
            try {
                Parent signUpRoot = FXMLLoader.load(getClass().getResource("/ui/SignUpPage.fxml"));
                Stage stage = (Stage) goToSignUp.getScene().getWindow();
                stage.setScene(new Scene(signUpRoot, 400, 500));
            } catch (Exception ex) {
            	 showError(ex.getMessage());
            }
        });
        loginButton.setOnAction(e -> {
        	System.out.println("I am logged in");
        });
        errorLabel.setVisible(false);

       
    }
    @FXML
    public void onLogin() {
        errorLabel.setVisible(false);
        String email = loginEmail.getText();
        String password = loginPassword.getText();
        try {
            User user = AuthService.login(email, password);
            if (user.isAdmin()) {
                Navigator.navigateTo("/ui/AdminDashboard.fxml", 1280, 800);
            } else {
                Navigator.navigateTo("/ui/home.fxml", 1280, 800);
            }
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
            errorLabel.setVisible(true);
        }
    }
    @FXML
    public void onKeyPress(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) onLogin();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
    

}
