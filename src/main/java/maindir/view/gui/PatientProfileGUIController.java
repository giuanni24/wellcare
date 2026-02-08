package maindir.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import maindir.bean.AppointmentBean;
import maindir.bean.UserBean;
import maindir.controller.PatientProfileController;
import maindir.exceptions.ControllerException;
import maindir.model.enums.AppointmentStatus;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class PatientProfileGUIController {

    @FXML private VBox appointmentsContainer;
    @FXML private Label lblMessage;
    @FXML private Button btnHome;
    @FXML private Button btnBack;

    @FXML private VBox popupCancellation;

    private UserBean loggedUser;
    private PatientProfileController profileController;
    private AppointmentBean appointmentBean;

    @FXML
    public void initialize() {
        addHoverEffect(btnHome);
        addHoverEffect(btnBack);

        // INIZIALIZZA IL CONTROLLER PRIMA DI TUTTO!
        this.profileController = new PatientProfileController();

        if (popupCancellation != null) {
            popupCancellation.setVisible(false);
            popupCancellation.setManaged(false);
        }

        // Recupera i dati passati dal Navigator DOPO aver inizializzato il controller
        UserBean user = (UserBean) Navigator.getInstance().getCurrentData();
        if (user != null) {
            setLoggedUser(user);
            Navigator.getInstance().clearCurrentData();
        }
    }

    public void setLoggedUser(UserBean user) {
        this.loggedUser = user;
        loadAppointments();
    }

    private void loadAppointments() {
        appointmentsContainer.getChildren().clear();
        try{
        List<AppointmentBean> appointments = profileController.getActiveAppointments(loggedUser);

        if (appointments.isEmpty()) {
            Label emptyLabel = new Label("Nessuna prenotazione attiva.");
            emptyLabel.setFont(Font.font("System", 18));
            emptyLabel.setPadding(new Insets(20));
            appointmentsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (int i = 0; i < appointments.size(); i++) {
            AppointmentBean app = appointments.get(i);

            HBox row = createAppointmentRow(app);
            appointmentsContainer.getChildren().add(row);

            if (i < appointments.size() - 1) {
                Separator sep = new Separator();
                sep.setStyle("-fx-background-color: black;");
                appointmentsContainer.getChildren().add(sep);
            }
        }}catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private HBox createAppointmentRow(AppointmentBean app) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(15, 20, 15, 20));
        row.setSpacing(10);

        VBox infoBox = new VBox(5);

        String dateStr = (app.getRequestedDate() != null)
                ? app.getRequestedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "Data da definire";

        String serviceName = (app.getService() != null) ? app.getService().getName() : "Visita Generica";

        Label lblTitle = new Label(serviceName + " - " + dateStr);
        lblTitle.setStyle("-fx-text-fill: #000000;");
        lblTitle.setFont(Font.font("System", FontWeight.BOLD, 20));

        String doctorName = (app.getDoctor() != null)
                ? "Dott. " + app.getDoctor().getName() + " " + app.getDoctor().getSurname()
                : "Dott. Non assegnato";

        Label lblDoctor = new Label(doctorName);
        lblDoctor.setStyle("-fx-text-fill: #000000;");
        lblDoctor.setFont(Font.font("System", FontWeight.NORMAL, 16));

        Label lblStatus = new Label("Stato: " + app.getStatus());
        lblStatus.setStyle("-fx-text-fill: #666666; -fx-font-size: 12; -fx-font-style: italic;");

        infoBox.getChildren().addAll(lblTitle, lblDoctor, lblStatus);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        AppointmentStatus status = app.getStatus();

        // RESCHEDULING: mostra RIPROGRAMMA + ANNULLA
        if (status == AppointmentStatus.RESCHEDULING) {
            Button btnReprogram = new Button("RIPROGRAMMA");
            addHoverEffect(btnReprogram);
            styleButton(btnReprogram, "#FFCCBC");
            btnReprogram.setOnAction(e -> handleReprogram(e, app));
            buttonBox.getChildren().add(btnReprogram);

            Button btnCancel = new Button("ANNULLA");
            addHoverEffect(btnCancel);
            styleButton(btnCancel, "#B0BEC5");
            btnCancel.setOnAction(e -> handleCancelRequest(app));
            buttonBox.getChildren().add(btnCancel);
        }
        // PENDING o CONFIRMED: mostra solo ANNULLA
        else if (status == AppointmentStatus.PENDING || status == AppointmentStatus.CONFIRMED) {
            Button btnCancel = new Button("ANNULLA");
            addHoverEffect(btnCancel);
            styleButton(btnCancel, "#B0BEC5");
            btnCancel.setOnAction(e -> handleCancelRequest(app));
            buttonBox.getChildren().add(btnCancel);
        }

        row.getChildren().addAll(infoBox, spacer, buttonBox);
        return row;
    }

    private void handleCancelRequest(AppointmentBean app) {
        this.appointmentBean = app;
        popupCancellation.setManaged(true);
        popupCancellation.setVisible(true);
    }

    @FXML
    private void confirmCancellation() {
        try{
        if (appointmentBean != null) {
            profileController.cancelAppointment(appointmentBean);
            showMessage("Prenotazione annullata con successo.", false);
            loadAppointments();
            }
        }catch (ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
        closePopup();
    }

    @FXML
    private void closePopup() {
        this.appointmentBean = null;
        popupCancellation.setVisible(false);
        popupCancellation.setManaged(false);
    }

    private void handleReprogram(ActionEvent event, AppointmentBean app) {
        Navigator.getInstance().goToWithData(event, "PatientReprogram.fxml", app);
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: black; -fx-font-weight: bold; -fx-background-radius: 10; -fx-cursor: hand;");
        btn.setPrefWidth(140);
        btn.setPrefHeight(35);
    }

    private void showMessage(String msg, boolean isError) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + ";");
        lblMessage.setVisible(true);
    }

    @FXML
    private void handleHome(ActionEvent event) {
        goToHome(event);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        goToHome(event);
    }

    private void goToHome(ActionEvent event) {
        Navigator.getInstance().goToWithData(event, "PatientHomepage.fxml", this.loggedUser);
    }

    private void addHoverEffect(Button button) {
        if (button == null) return;

        javafx.scene.transform.Scale scale = new javafx.scene.transform.Scale(1, 1);
        scale.setPivotX(button.getWidth() / 2);
        scale.setPivotY(button.getHeight() / 2);
        button.getTransforms().add(scale);

        button.setOnMouseEntered(e -> {
            scale.setX(1.05);
            scale.setY(1.05);
        });

        button.setOnMouseExited(e -> {
            scale.setX(1.0);
            scale.setY(1.0);
        });
    }
}
