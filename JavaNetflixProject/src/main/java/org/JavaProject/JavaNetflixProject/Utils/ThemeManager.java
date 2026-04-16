package org.JavaProject.JavaNetflixProject.Utils;

import javafx.scene.Scene;

import java.util.HashMap;
import java.util.Map;

public class ThemeManager {

    private static String currentAccent = "#ebeef2";
    private static String textColor = "black";

    private static final Map<String, String> genreColors = new HashMap<>();

    static {
        genreColors.put("Action",          "#ff3b3b");
        genreColors.put("Animation",       "#9b59b6");
        genreColors.put("Aventure",        "#e67e22");
        genreColors.put("Comédie",         "#f1c40f");
        genreColors.put("Documentaire",    "#16a085");
        genreColors.put("Drame",           "#3498db");
        genreColors.put("Horreur",         "#8b0000");
        genreColors.put("Romance",         "#ff69b4");
        genreColors.put("Science-Fiction", "#00c3ff");
        genreColors.put("Thriller",        "#2c3e50");
    }

    public static String getCurrentAccent() {
        return currentAccent;
    }

    public static void applyTheme(Scene scene) {
        if (scene == null) return;

        // Use a specific style class on the root + inject as looked-up color
        scene.getRoot().setStyle(
            "-fx-app-accent: " + currentAccent + ";" +    // custom looked-up color
            "-fx-base: #0a0a0a;" +
            "-fx-control-inner-background: #1a1a1a;" +
            "-fx-text-base-color: " + textColor + ";" +
            // Override the built-in accent explicitly too:
            "-fx-accent: " + currentAccent + ";" +
            "-fx-focus-color: " + currentAccent + ";" +
            "-fx-faint-focus-color: transparent;"
        );

        // Force a CSS re-pass
        scene.getRoot().applyCss();
    }

    public static void setThemeByGenre(String genre, Scene scene) {
        currentAccent = genreColors.getOrDefault(genre, "#ebeef2");
        textColor = isLightColor(currentAccent) ? "black" : "white";
        applyTheme(scene);
    }

    public static void setDefaultTheme(Scene scene) {
        currentAccent = "#ebeef2";
        textColor = "black";
        applyTheme(scene);
    }

    private static boolean isLightColor(String hex) {
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);
        double brightness = (0.299 * r + 0.587 * g + 0.114 * b);
        return brightness > 186;
    }
}