module org.JavaProject.JavaNetflixProject {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;

    opens org.JavaProject.JavaNetflixProject to javafx.fxml;
    exports org.JavaProject.JavaNetflixProject;
}
