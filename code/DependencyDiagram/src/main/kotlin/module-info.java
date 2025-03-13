module org.example.dependencydiagram {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;


    opens org.example.dependencydiagram to javafx.fxml;
    exports org.example.dependencydiagram;
}