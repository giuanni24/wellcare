package maindir.view.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class JavaFXStarter extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Carica il file FXML della Landing Page
            // Assicurati che LandingPage.fxml sia dentro src/main/resources
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LandingPage.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, 1920, 1080);

            // Impostazioni della finestra principale
            primaryStage.setTitle("WellCare Center");
            primaryStage.setScene(scene);

            // Imposta l'icona (opzionale, se hai il file shield-icon.png nelle risorse)
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/shield-icon.png"))));
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("ERRORE CRITICO: Non riesco a trovare LandingPage.fxml");
            System.err.println("Assicurati che il file sia nella cartella src/main/resources");
        }catch (Exception e) {
        System.out.println("Icona non trovata, avvio senza icona.");
    }
    }
}
