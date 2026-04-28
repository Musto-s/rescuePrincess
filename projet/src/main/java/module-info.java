module org.example.projet {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens org.example.projet to javafx.fxml;
    exports org.example.projet;
}