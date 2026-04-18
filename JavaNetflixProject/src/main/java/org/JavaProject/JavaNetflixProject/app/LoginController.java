package org.JavaProject.JavaNetflixProject.app;

import org.JavaProject.JavaNetflixProject.Entities.User;
import org.JavaProject.JavaNetflixProject.Services.AuthService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {

    @FXML private TextField     loginEmail;
    @FXML private PasswordField loginPassword;
    @FXML private Button        loginButton;
    @FXML private Label         errorLabel;
    @FXML private Hyperlink     goToSignUp;
    @FXML private Hyperlink     forgotLink;
    @FXML private VBox          forgotBox;
    @FXML private Label         maskedEmailLabel;
    @FXML
    private javafx.scene.layout.Pane glowLayer;

    private final AuthService authService = new AuthService();
    private boolean forgotShown = false;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        forgotBox.setVisible(false);
        forgotBox.setManaged(false);
    }

    @FXML
    public void onLogin() {
        hideError();
        hideForgot();
        String email    = loginEmail.getText().trim();
        String password = loginPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            User user = authService.login(email, password);
            if (user.isAdmin()) {
                Navigator.navigateTo("/ui/AdminDashboard.fxml", 1280, 800);
            } else {
                Navigator.navigateTo("/ui/home.fxml", 1280, 800);
            }
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void onKeyPress(KeyEvent e) {
        if (e.getCode() == KeyCode.ENTER) onLogin();
    }

    /** Forgot password — shows masked email hint */
    @FXML
    public void onForgotPassword() {
        if (forgotShown) { hideForgot(); return; }

        String email = loginEmail.getText().trim();
        String masked = maskEmail(email.isEmpty() ? "example@gmail.com" : email);
        maskedEmailLabel.setText("We sent a verification link to:  " + masked);

        forgotBox.setVisible(true);
        forgotBox.setManaged(true);
        forgotShown = true;
        forgotLink.setText("Hide");
    }

    @FXML
    public void goToSignUp() {
        try { Navigator.navigateTo("/ui/SignUpPage.fxml", 1100, 700); }
        catch (Exception e) { showError(e.getMessage()); }
    }


    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 0) return email;
        String local  = email.substring(0, at);
        String domain = email.substring(at);
        if (local.length() <= 2) return local.charAt(0) + "****" + domain;
        return local.charAt(0)
                + "*".repeat(Math.max(2, local.length() - 2))
                + local.charAt(local.length() - 1)
                + domain;
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void hideForgot() {
        forgotBox.setVisible(false);
        forgotBox.setManaged(false);
        forgotShown = false;
        forgotLink.setText("Forgot password?");
    }
}