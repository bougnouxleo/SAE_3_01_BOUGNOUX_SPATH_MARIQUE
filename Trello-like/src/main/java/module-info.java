module org.example.trellolike {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens org.example.trellolike to javafx.fxml;
    exports org.example.trellolike;
    exports org.example.trellolike.tache;
    opens org.example.trellolike.tache to javafx.fxml;
    exports org.example.trellolike.vue;
    opens org.example.trellolike.vue to javafx.fxml;
    exports org.example.trellolike.controlleur;
    opens org.example.trellolike.controlleur to javafx.fxml;
}