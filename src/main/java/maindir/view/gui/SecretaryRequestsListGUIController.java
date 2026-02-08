package maindir.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import maindir.bean.AppointmentBean;
import maindir.controller.BookingRequestController;
import maindir.exceptions.ControllerException;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class SecretaryRequestsListGUIController {

    @FXML private VBox requestsContainer;
    @FXML private Label lblMessage;
    @FXML private Button btnHome;
    @FXML private Button btnBack;

    private BookingRequestController bookingController;

    @FXML
    public void initialize() {
        addHoverEffect(btnHome);
        addHoverEffect(btnBack);
        this.bookingController = new BookingRequestController();
        loadRequests();
    }

    private void loadRequests() {
        requestsContainer.getChildren().clear();
        try{
        List<AppointmentBean> pendingRequests = bookingController.getPendingRequests();

        if (pendingRequests == null || pendingRequests.isEmpty()) {
            Label empty = new Label("Nessuna richiesta in attesa.");
            empty.setFont(Font.font("System", 18));
            requestsContainer.getChildren().add(empty);
            return;
        }

        for (AppointmentBean req : pendingRequests) {
            Button btn = createRequestButton(req);
            addHoverEffect(btn);
            requestsContainer.getChildren().add(btn);
        }}catch (ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private Button createRequestButton(AppointmentBean req) {
        String dateStr = req.getRequestedDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String doctorStr = "Dott. " + req.getDoctor().getSurname();
        String patientStr = "Paziente: " + req.getPatient().getName() + " " + req.getPatient().getSurname();

        String buttonText = String.format("%s - %s%n%s\t\t\t\t%s",
                req.getService().getName(), dateStr, doctorStr, patientStr);

        Button btn = new Button(buttonText);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 0 0 2 0; " +
                "-fx-font-size: 18px; -fx-cursor: hand; -fx-background-radius: 0;");

        btn.setOnAction(e -> goToManageRequest(e, req));
        return btn;
    }

    private void goToManageRequest(ActionEvent event, AppointmentBean request) {
        Navigator.getInstance().goToWithData(event, "SecretaryManageRequest.fxml", request);
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
        if (button == null) return;
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> button.setStyle(originalStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        button.setOnMouseExited(e -> button.setStyle(originalStyle));
    }
}
