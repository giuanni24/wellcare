package maindir.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import maindir.bean.AppointmentBean;
import maindir.controller.BookingRequestController;
import maindir.exceptions.ControllerException;

import java.time.format.DateTimeFormatter;

public class PatientBookingSummaryGUIController {

    @FXML private Label lblServizio;
    @FXML private Label lblDottore;
    @FXML private Label lblCosto;
    @FXML private Label lblGiorno;

    @FXML private VBox popupConfirmation;
    @FXML private VBox popupCancellation;
    @FXML private Button btnReject;
    @FXML private Button btnHome;
    @FXML private Button btnConfirm;
    @FXML private Button btnBack;
    @FXML private Button btnOk;
    private AppointmentBean pendingRequest;
    private BookingRequestController appController;

    @FXML
    public void initialize() {
        // Recupera i dati passati dal Navigator
        appController = new BookingRequestController();
        AppointmentBean appointmentBean = (AppointmentBean) Navigator.getInstance().getCurrentData();
        if (appointmentBean != null) {
            setAppointmentData(appointmentBean);
            Navigator.getInstance().clearCurrentData();
        }

        addHoverEffect(btnReject);
        addHoverEffect(btnHome);
        addHoverEffect(btnBack);
        addHoverEffect(btnConfirm);
        addHoverEffect(btnOk);


        if (popupConfirmation != null) {
            popupConfirmation.setVisible(false);
            popupConfirmation.setManaged(false);
        }
        if (popupCancellation != null) {
            popupCancellation.setVisible(false);
            popupCancellation.setManaged(false);
        }
    }

    public void setAppointmentData(AppointmentBean bean) {
        this.pendingRequest = bean;

        if (bean != null) {
            if (bean.getService() != null) {
                lblServizio.setText(bean.getService().getName());
                lblCosto.setText("€ " + bean.getService().getBasePrice() + "0");
            }
            if (bean.getDoctor() != null) {
                lblDottore.setText(bean.getDoctor().getName() + " " + bean.getDoctor().getSurname());
            }
            if (bean.getRequestedDate() != null) {
                lblGiorno.setText(bean.getRequestedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
        }
    }

    @FXML
    private void handleConfermaDefinitiva() {
        try {
            appController.createBookingRequest(pendingRequest);

            popupConfirmation.setManaged(true);
            popupConfirmation.setVisible(true);

        } catch (ControllerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Errore durante il salvataggio.");
            alert.show();
        }
    }

    @FXML
    private void handleOkSuccess(ActionEvent event) {
        popupConfirmation.setVisible(false);
        popupConfirmation.setManaged(false);
        goToHome(event);
    }

    @FXML
    private void handleAnnulla() {
        popupCancellation.setManaged(true);
        popupCancellation.setVisible(true);
    }

    @FXML
    private void handleOkCancellation(ActionEvent event) {
        popupCancellation.setVisible(false);
        popupCancellation.setManaged(false);
        goToHome(event);
    }

    @FXML
    private void handleHome(ActionEvent event) {
        goToHome(event);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        if (pendingRequest != null && pendingRequest.getPatient() != null) {
            Navigator.getInstance().goToWithData(event, "PatientBooking.fxml", pendingRequest.getPatient());
        } else {
            System.out.println("ATTENZIONE: Tornando a PatientBooking senza utente!");
            Navigator.getInstance().goTo(event, "PatientBooking.fxml");
        }
    }

    private void goToHome(ActionEvent event) {
        if (pendingRequest != null && pendingRequest.getPatient() != null) {
            Navigator.getInstance().goToWithData(event, "PatientHomepage.fxml", pendingRequest.getPatient());
        } else {
            System.out.println("ATTENZIONE: Sto tornando alla home senza un utente loggato!");
            Navigator.getInstance().goTo(event, "PatientHomepage.fxml");
        }
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
