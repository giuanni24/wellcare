package maindir.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import maindir.bean.AppointmentBean;
import maindir.bean.UserBean;
import maindir.controller.AcceptController;
import maindir.exceptions.ControllerException;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SecretaryAcceptPatientGUIController {

    @FXML private TextField txtFiscalCode;
    @FXML private VBox appointmentsContainer;
    @FXML private Label lblStatus;
    @FXML private Button btnHome;
    @FXML private Button btnBack;
    @FXML private Button btnSearch;

    private AcceptController acceptController;

    @FXML
    public void initialize() {
        addHoverEffect(btnHome);
        addHoverEffect(btnBack);
        addHoverEffect(btnSearch);
        this.acceptController = new AcceptController();
    }

    @FXML
    private void handleSearch() {
        String cf = txtFiscalCode.getText();
        appointmentsContainer.getChildren().clear();
        lblStatus.setVisible(false);

        if (cf == null || cf.length() != 16) {
            showStatus("Inserire un Codice Fiscale valido (16 caratteri)", true);
            return;
        }

        try {
            UserBean patient = acceptController.findByCF(cf);
            if (patient == null) {
                showStatus("Paziente non trovato.", true);
                return;
            }

            List<AppointmentBean> todayApps = acceptController.findTodayAppointmentsByPatient(patient);

            if (todayApps.isEmpty()) {
                showStatus("Nessun appuntamento per oggi.", false);
            } else {
                for (AppointmentBean app : todayApps) {
                    appointmentsContainer.getChildren().add(createAppointmentButton(app));
                }
            }
        } catch (ControllerException e) {
            showStatus("Errore: " + e.getMessage(), true);
        }
    }

    private Button createAppointmentButton(AppointmentBean app) {
        String timeStr = app.getConfirmedTime().format(DateTimeFormatter.ofPattern("HH:mm"));
        String text = String.format("Ore %s - %s%nDott. %s %s",
                timeStr, app.getService().getName(),
                app.getDoctor().getName(), app.getDoctor().getSurname());

        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: white; -fx-border-color: #DDD; -fx-border-width: 0 0 1 0; " +
                "-fx-font-size: 16; -fx-padding: 15; -fx-cursor: hand;");

        // Hover Effect
        btn.setOnMouseEntered(e -> {
            btn.setScaleX(1.02);
            btn.setStyle(btn.getStyle() + "-fx-background-color: #F0F0F0;");
        });
        btn.setOnMouseExited(e -> {
            btn.setScaleX(1.0);
            btn.setStyle(btn.getStyle().replace("-fx-background-color: #F0F0F0;", "-fx-background-color: white;"));
        });

        btn.setOnAction(e -> handleAcceptance(app));
        return btn;
    }

    private void handleAcceptance(AppointmentBean app) {
        try {
            acceptController.acceptPatient(app);
            showStatus("Paziente accettato con successo per l'appuntamento delle " + app.getConfirmedTime(), false);
            handleSearch();
        }catch (ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void showStatus(String msg, boolean isError) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + ";");
        lblStatus.setVisible(true);
    }

    @FXML
    private void handleHome(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SecretaryHomepage.fxml");
    }

    @FXML
    private void handleBack(ActionEvent event) {
        handleHome(event);
    }

    private void addHoverEffect(Button button) {
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> button.setStyle(originalStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        button.setOnMouseExited(e -> button.setStyle(originalStyle));
    }
}
