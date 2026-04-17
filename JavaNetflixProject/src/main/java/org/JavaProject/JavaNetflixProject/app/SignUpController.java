package org.JavaProject.JavaNetflixProject.app;

import org.JavaProject.JavaNetflixProject.Services.AuthService;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class SignUpController {

    // ── The only valid admin authorization code ──
    private static final String ADMIN_CODE = "NOTFLIX-ADMIN-2024";

    @FXML private TextField     username;
    @FXML private TextField     email;
    @FXML private PasswordField password;
    @FXML private PasswordField confirmpassword;
    @FXML private PasswordField adminCode;
    @FXML private Hyperlink     goToLogin;
    @FXML private Label         errorLabel;
    @FXML private Label         strengthLabel;
    @FXML private Label         passwordHint;
    @FXML private Label         adminArrow;
    @FXML private Pane          strengthFill;
    @FXML private VBox          adminFields;
    @FXML private VBox          adminToggle;

    private final AuthService authService = new AuthService();
    private boolean adminExpanded = false;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        adminFields.setVisible(false);
        adminFields.setManaged(false);
    }

    /** Toggle admin section visibility */
    @FXML
    public void toggleAdminSection() {
        adminExpanded = !adminExpanded;
        adminFields.setVisible(adminExpanded);
        adminFields.setManaged(adminExpanded);
        adminArrow.setText(adminExpanded ? "▼" : "▶");
    }

    /** Password strength indicator */
    @FXML
    public void onPasswordTyped() {
        String pwd = password.getText();
        int score = measureStrength(pwd);
        double barWidth = 340.0; // approximate full width

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

    /** 0=empty 1=weak 2=fair 3=good 4=strong */
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

        // ── Basic validation ──
        if (nameVal.isEmpty()) { showError("Display name is required."); return; }
        if (emailVal.isEmpty() || !emailVal.contains("@")) { showError("Enter a valid email address."); return; }
        if (passVal.length() < 8) { showError("Password must be at least 8 characters."); return; }
        if (measureStrength(passVal) < 2) { showError("Password is too weak. Add numbers or symbols."); return; }
        if (!passVal.equals(confirmVal)) { showError("Passwords do not match."); return; }

        // ── Admin validation ──
        boolean wantsAdmin = adminExpanded && adminCode != null
                && !adminCode.getText().trim().isEmpty();
        if (adminExpanded && adminCode != null && !adminCode.getText().trim().isEmpty()) {
            if (!ADMIN_CODE.equals(adminCode.getText().trim())) {
                showError("Invalid admin authorization code.");
                return;
            }
        }

        try {
            authService.register(nameVal, emailVal, passVal, confirmVal);

            // If admin code was correct, upgrade the role
            if (wantsAdmin) {
                // Update role to ADMIN after registration
                org.JavaProject.JavaNetflixProject.DAO.UserDAO dao =
                        new org.JavaProject.JavaNetflixProject.DAO.UserDAO();
                org.JavaProject.JavaNetflixProject.Entities.User u = dao.findByEmail(emailVal);
                if (u != null) {
                    u.setRole("ADMIN");
                    dao.update(u);
                }
            }

            // Auto-login
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