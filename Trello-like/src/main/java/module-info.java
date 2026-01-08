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
    requires java.desktop;
    requires javafx.swing;

    opens org.example.trellolike to javafx.fxml;
    exports org.example.trellolike;
    exports org.example.trellolike.tache;
    opens org.example.trellolike.tache to javafx.fxml;
    exports org.example.trellolike.vue;
    opens org.example.trellolike.vue to javafx.fxml;
    exports org.example.trellolike.controlleur;
    opens org.example.trellolike.controlleur to javafx.fxml;
    exports org.example.trellolike.vue.Kanban;
    opens org.example.trellolike.vue.Kanban to javafx.fxml;
    exports org.example.trellolike.vue.Liste_Archives;
    opens org.example.trellolike.vue.Liste_Archives to javafx.fxml;
    exports org.example.trellolike.modele;
    opens org.example.trellolike.modele to javafx.fxml;
    exports org.example.trellolike.log;
    opens org.example.trellolike.log to javafx.fxml;
    exports org.example.trellolike.Etiquette;
    opens org.example.trellolike.Etiquette to javafx.fxml;
    exports org.example.trellolike.vue.Etiquette;
    opens org.example.trellolike.vue.Etiquette to javafx.fxml;
    exports org.example.trellolike.vue.VueGantt;
    opens org.example.trellolike.vue.VueGantt to javafx.fxml;
    exports org.example.trellolike.vue.Calendrier;
    opens org.example.trellolike.vue.Calendrier to javafx.fxml;
}