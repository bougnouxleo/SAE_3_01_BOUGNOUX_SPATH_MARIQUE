module org.trellolike.trellolike {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.trellolike.trellolike to javafx.fxml;
    exports org.trellolike.trellolike;
}