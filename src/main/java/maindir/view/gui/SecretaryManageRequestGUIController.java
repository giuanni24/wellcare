package maindir.view.gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import maindir.bean.AppointmentBean;
import maindir.bean.InvoiceBean;
import maindir.controller.BookingRequestController;
import maindir.exceptions.ControllerException;

import java.time.LocalTime;
import java.util.List;

public class SecretaryManageRequestGUIController {

    @FXML private ListView<String> listInvoices;
    @FXML private ComboBox<LocalTime> cmbSlots;
    @FXML private Label lblRequestInfo;
    @FXML private Label lblMessage;
    @FXML private Button btnHome;
    @FXML private Button btnLogout;
    @FXML private Button btnConfirm;
    @FXML private Button btnReject;

    // Popup motivo rifiuto
    @FXML private VBox popupReject;
    @FXML private TextArea txtRejectReason;

    // Popup "modello" (come PatientBookingSummary)
    @FXML private VBox popupConfirmation; // successo conferma
    @FXML private VBox popupCancellation; // successo rifiuto (colore #598bad nel tuo FXML)

    private BookingRequestController bookingController;
    private AppointmentBean currentRequest;

    @FXML
    public void initialize() {
        // Recupera i dati passati dal Navigator
        this.bookingController = new BookingRequestController();
        AppointmentBean request = (AppointmentBean) Navigator.getInstance().getCurrentData();
        if (request != null) {
            initData(request);
            Navigator.getInstance().clearCurrentData();
        }

        addHoverEffect(btnHome);
        addHoverEffect(btnLogout);
        addHoverEffect(btnConfirm);
        addHoverEffect(btnReject);



        hidePopup(popupReject);
        hidePopup(popupConfirmation);
        hidePopup(popupCancellation);

        if (lblMessage != null) {
            lblMessage.setVisible(false);
            lblMessage.setManaged(false);
        }
    }

    public void initData(AppointmentBean request) {
        this.currentRequest = request;

        String info = String.format("Visita: %s%nDottore: %s %s%nPaziente: %s %s",
                request.getService().getName(),
                request.getDoctor().getName(), request.getDoctor().getSurname(),
                request.getPatient().getName(), request.getPatient().getSurname());
        lblRequestInfo.setText(info);

        loadUnpaidInvoices();
        loadAvailableSlots();
    }

    private void loadUnpaidInvoices() {
        try {
            List<InvoiceBean> unpaid = bookingController.getUnpaidInvoices(currentRequest.getPatient());

            listInvoices.getItems().clear();
            if (unpaid.isEmpty()) {
                listInvoices.getItems().add("Nessuna fattura in sospeso.");
            } else {
                for (InvoiceBean inv : unpaid) {
                    listInvoices.getItems().add(
                            "Fattura #" + inv.getId() + " - €" + inv.getAmount() +
                                    "\nServizio: " + inv.getAppointmentBean().getService().getName() +
                                    "\nData: " + inv.getAppointmentBean().getRequestedDate()
                    );
                }
            }
        }catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void loadAvailableSlots() {
        try{
        List<LocalTime> slots = bookingController.getAvailableSlots(currentRequest);

        if (slots.isEmpty()) {
            cmbSlots.setDisable(true);
            cmbSlots.setPromptText("NESSUNO SLOT DISPONIBILE");
            showMessage("Nessuna disponibilità per questa data.", true);
        } else {
            cmbSlots.setItems(FXCollections.observableArrayList(slots));
        }}catch (ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void handleConfirm() {
        LocalTime selectedTime = cmbSlots.getValue();

        if (selectedTime == null) {
            showMessage("Selezionare un orario prima di confermare!", true);
            return;
        }

        currentRequest.setConfirmedTime(selectedTime);
        try{
        boolean success = bookingController.acceptRequest(currentRequest);

        if (success) {
            disableUI();
            showPopup(popupConfirmation);
        } else {
            showMessage("Errore durante l'approvazione.", true);
        }}catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void handleRejectClick() {
        txtRejectReason.clear();
        showPopup(popupReject);
    }

    @FXML
    private void confirmRejection() {
        String reason = txtRejectReason.getText();
        if (reason == null || reason.trim().isEmpty()) {
            reason = "Nessun motivo specificato.";
        }

        currentRequest.setRejectionReason(reason);
        try{
        boolean success = bookingController.rejectRequest(currentRequest);

        if (success) {
            hidePopup(popupReject);
            disableUI();
            showPopup(popupCancellation);
        } else {
            showMessage("Errore nel rifiuto della richiesta.", true);
        }}catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void closePopup() {
        hidePopup(popupReject);
    }

    @FXML
    private void handleOkSuccess(ActionEvent event) {
        hidePopup(popupConfirmation);
        goHome(event);
    }

    @FXML
    private void handleOkCancellation(ActionEvent event) {
        hidePopup(popupCancellation);
        goHome(event);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SecretaryRequestList.fxml");
    }

    @FXML
    private void handleHome(ActionEvent event) {
        goHome(event);
    }

    private void goHome(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SecretaryHomepage.fxml");
    }

    private void showMessage(String msg, boolean isError) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + "; -fx-font-weight: bold;");
        lblMessage.setManaged(true);
        lblMessage.setVisible(true);
    }

    private void disableUI() {
        cmbSlots.setDisable(true);
        listInvoices.setDisable(true);
        btnConfirm.setDisable(true);
        btnReject.setDisable(true);
    }

    private void showPopup(VBox popup) {
        if (popup == null) return;
        popup.setManaged(true);
        popup.setVisible(true);
    }

    private void hidePopup(VBox popup) {
        if (popup == null) return;
        popup.setVisible(false);
        popup.setManaged(false);
    }

    private void addHoverEffect(Button button) {
        if (button == null) return;
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> button.setStyle(originalStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        button.setOnMouseExited(e -> button.setStyle(originalStyle));
    }
}
