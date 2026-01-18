module org.JavaProject.JavaNetflixProject {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.JavaProject.JavaNetflixProject to javafx.fxml;
    exports org.JavaProject.JavaNetflixProject;
}
