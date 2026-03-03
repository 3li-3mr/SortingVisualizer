module com.example.sortingvisualizer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sortingvisualizer to javafx.fxml;
    opens com.example.sortingvisualizer.models to javafx.base;
    exports com.example.sortingvisualizer;
}