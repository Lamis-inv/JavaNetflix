package org.JavaProject.JavaNetflixProject.app;

import org.JavaProject.JavaNetflixProject.Services.AuthService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SignUpController {

    /**
     * Admin detection rules (no toggle button):
     *  1.  Email ends with @admin.notflix.com   OR   @notflix.admin
     *  2.  AND the password contains the secret admin prefix "ADM!N_"
     *
     *  Example valid admin:
     *    email:    superadmin@admin.notflix.com
     *    password: ADM!N_mySecurePass123
     */
    private static final String ADMIN_EMAIL_SUFFIX_1 = "@admin.notflix.com";
    private static final String ADMIN_EMAIL_SUFFIX_2 = "@notflix.admin";
    private static final String ADMIN_PASS_PREFIX    = "ADM!N_";

    @FXML private TextField     username;
    @FXML private TextField     email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmpassword;
    @FXML private Label         goToLogin;
    @FXML private Label         errorLabel;
    @FXML private Label         strengthLabel;
    @FXML private Label         passwordHint;
    @FXML private Pane          strengthFill;
    @FXML private Label         adminHintLabel;   // small hint shown when @admin email typed

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        if (adminHintLabel != null) {
            adminHintLabel.setVisible(false);
            adminHintLabel.setManaged(false);
        }

        // Show admin hint when email looks admin
        if (email != null) {
            email.textProperty().addListener((obs, o, n) -> updateAdminHint(n));
        }
    }

    private void updateAdminHint(String emailText) {
        if (adminHintLabel == null) return;
        boolean isAdminEmail = emailText != null &&
            (emailText.endsWith(ADMIN_EMAIL_SUFFIX_1) || emailText.endsWith(ADMIN_EMAIL_SUFFIX_2));
        adminHintLabel.setVisible(isAdminEmail);
        adminHintLabel.setManaged(isAdminEmail);
    }

    /** Password strength indicator */
    @FXML
    public void onPasswordTyped() {
        String pwd = password.getText();
        int score = measureStrength(pwd);
        double barWidth = 340.0;

        switch (score) {
            case 0:
                strengthFill.setPrefWidth(0);
                strengthLabel.setText("");
                passwordHint.setText("Use 8+ characters with numbers and symbols.");
                break;
            case 1:
                strengthFill.setPrefWidth(barWidth * 0.2);
                strengthFill.getStyleClass().setAll("strength-fill-weak");
                strengthLabel.setText("Too weak");
                strengthLabel.setStyle("-fx-text-fill:#ff3b3b;-fx-font-size:10px;-fx-font-weight:700;");
                passwordHint.setText("Way too short. Keep going.");
                break;
            case 2:
                strengthFill.setPrefWidth(barWidth * 0.45);
                strengthFill.getStyleClass().setAll("strength-fill-fair");
                strengthLabel.setText("Fair");
                strengthLabel.setStyle("-fx-text-fill:#f5c842;-fx-font-size:10px;-fx-font-weight:700;");
                passwordHint.setText("Add numbers or symbols to strengthen it.");
                break;
            case 3:
                strengthFill.setPrefWidth(barWidth * 0.72);
                strengthFill.getStyleClass().setAll("strength-fill-good");
                strengthLabel.setText("Good");
                strengthLabel.setStyle("-fx-text-fill:#4fa3e0;-fx-font-size:10px;-fx-font-weight:700;");
                passwordHint.setText("Almost there — add a special character.");
                break;
            case 4:
                strengthFill.setPrefWidth(barWidth);
                strengthFill.getStyleClass().setAll("strength-fill-strong");
                strengthLabel.setText("Strong ✓");
                strengthLabel.setStyle("-fx-text-fill:#22c55e;-fx-font-size:10px;-fx-font-weight:700;");
                passwordHint.setText("Great password!");
                break;
        }
    }

    private int measureStrength(String pwd) {
        if (pwd == null || pwd.isEmpty()) return 0;
        int score = 0;
        if (pwd.length() >= 8)  score++;
        if (pwd.length() >= 12) score++;
        if (pwd.matches(".*[0-9].*")) score++;
        if (pwd.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;
        return Math.min(score, 4);
    }

    @FXML
    public void onRegister() {
        hideError();

        String nameVal    = username.getText().trim();
        String emailVal   = email.getText().trim();
        String passVal    = password.getText();
        String confirmVal = confirmpassword.getText();

        if (nameVal.isEmpty())  { showError("Display name is required."); return; }
        if (emailVal.isEmpty() || !emailVal.contains("@")) { showError("Enter a valid email address."); return; }
        if (passVal.length() < 8) { showError("Password must be at least 8 characters."); return; }
        if (measureStrength(passVal) < 2) { showError("Password is too weak. Add numbers or symbols."); return; }
        if (!passVal.equals(confirmVal)) { showError("Passwords do not match."); return; }

        // Determine if this should be an admin account
        boolean wantsAdmin = (emailVal.endsWith(ADMIN_EMAIL_SUFFIX_1)
                           || emailVal.endsWith(ADMIN_EMAIL_SUFFIX_2))
                          && passVal.startsWith(ADMIN_PASS_PREFIX);

        // If email looks admin but password doesn't have prefix — reject
        boolean isAdminEmail = emailVal.endsWith(ADMIN_EMAIL_SUFFIX_1)
                            || emailVal.endsWith(ADMIN_EMAIL_SUFFIX_2);
        if (isAdminEmail && !passVal.startsWith(ADMIN_PASS_PREFIX)) {
            showError("Admin accounts require a password starting with ADM!N_");
            return;
        }

        try {
            authService.register(nameVal, emailVal, passVal, confirmVal);

            if (wantsAdmin) {
                org.JavaProject.JavaNetflixProject.DAO.UserDAO dao =
                    new org.JavaProject.JavaNetflixProject.DAO.UserDAO();
                org.JavaProject.JavaNetflixProject.Entities.User u = dao.findByEmail(emailVal);
                if (u != null) {
                    u.setRole("ADMIN");
                    dao.update(u);
                }
            }

            authService.login(emailVal, passVal);
            Navigator.navigateTo("/ui/home.fxml", 1280, 800);

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    public void goToLogin() {
        try { Navigator.navigateTo("/ui/LoginPage.fxml", 1100, 700); }
        catch (Exception e) { showError(e.getMessage()); }
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
}