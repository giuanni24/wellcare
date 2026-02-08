module medic.platform {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;
    requires org.checkerframework.checker.qual;
    opens maindir.view.gui to javafx.fxml;
    exports maindir;
    exports maindir.view.gui;
}