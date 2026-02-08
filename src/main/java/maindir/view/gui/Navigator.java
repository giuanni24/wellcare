package maindir.view.gui;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Navigator class for handling page navigation in the JavaFX application.
 * Implements the Singleton pattern.
 */
public class Navigator {

    private static final Logger LOGGER = Logger.getLogger(Navigator.class.getName());
    private static Navigator instance = null;
    private static final String FXML_PATH = "/";

    private Object currentData;

    private Navigator() {
    }

    public static synchronized Navigator getInstance() {
        if (instance == null) {
            instance = new Navigator();
        }
        return instance;
    }

    public void goTo(ActionEvent event, String fxmlFile) {
        try {
            Stage stage = getStageFromEvent(event);

            // Salva dimensioni e stato prima di cambiare scena
            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean isMaximized = stage.isMaximized();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Ripristina dimensioni e stato
            if (isMaximized) {
                stage.setMaximized(true);
            } else if (width > 0 && height > 0) {
                stage.setWidth(width);
                stage.setHeight(height);
            }

            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Error navigating to " + fxmlFile);
        }
    }

    public void goToWithData(ActionEvent event, String fxmlFile, Object data) {
        this.currentData = data;
        goTo(event, fxmlFile);
    }

    public Object getCurrentData() {
        return currentData;
    }

    public void setCurrentData(Object data) {
        this.currentData = data;
    }

    public void clearCurrentData() {
        this.currentData = null;
    }

    private Stage getStageFromEvent(ActionEvent event) {
        Node source = (Node) event.getSource();
        return (Stage) source.getScene().getWindow();
    }

    public void goTo(Stage stage, String fxmlFile) {
        try {
            // Salva dimensioni e stato prima di cambiare scena
            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean isMaximized = stage.isMaximized();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH + fxmlFile));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Ripristina dimensioni e stato
            if (isMaximized) {
                stage.setMaximized(true);
            } else if (width > 0 && height > 0) {
                stage.setWidth(width);
                stage.setHeight(height);
            }

            stage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e, () -> "Error navigating to " + fxmlFile);
        }
    }
}
