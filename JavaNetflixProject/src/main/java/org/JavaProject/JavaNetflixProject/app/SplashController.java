package org.JavaProject.JavaNetflixProject.app;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.JavaProject.JavaNetflixProject.Utils.Navigator;

public class SplashController {

    @FXML private StackPane splashRoot;
    @FXML private Label logoLabel;

    @FXML
    public void initialize() {
        playIntro();
    }

    private void playIntro() {
        // Start state
        logoLabel.setOpacity(0);
        logoLabel.setScaleX(1.0);
        logoLabel.setScaleY(1.0);

        // 1) Fade in (0 → 0.8s)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), logoLabel);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setInterpolator(Interpolator.EASE_OUT);

        // 2) Hold (0.8s → 2.3s)
        PauseTransition hold = new PauseTransition(Duration.millis(1500));

        // 3) Zoom in + fade out together (2.3s → 3.3s)
        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(1000), logoLabel);
        zoomIn.setFromX(1.0); zoomIn.setToX(6.0);
        zoomIn.setFromY(1.0); zoomIn.setToY(6.0);
        zoomIn.setInterpolator(Interpolator.EASE_IN);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(1000), logoLabel);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setInterpolator(Interpolator.EASE_IN);

        // Also fade the whole background to black
        FadeTransition bgFade = new FadeTransition(Duration.millis(1000), splashRoot);
        bgFade.setFromValue(1);
        bgFade.setToValue(0);
        bgFade.setInterpolator(Interpolator.EASE_IN);

        ParallelTransition zoomFade = new ParallelTransition(zoomIn, fadeOut, bgFade);

        // Full sequence
        SequentialTransition sequence = new SequentialTransition(fadeIn, hold, zoomFade);

        sequence.setOnFinished(e -> {
            try {
                Navigator.navigateTo("/ui/LoginPage.fxml", 900, 600);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        sequence.play();
    }
}