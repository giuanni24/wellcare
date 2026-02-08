package maindir.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import maindir.bean.NotificationBean;
import maindir.bean.UserBean;
import maindir.controller.NotificationController;
import maindir.exceptions.ControllerException;

import java.util.List;

public class HomepagePatientGUIController {

    @FXML private Button btnLogout;
    @FXML private Button btnPrenotaVisita;
    @FXML private Button btnVisualizzaPrenotazioni;
    @FXML private Button btnOk;
    @FXML private VBox popupNotifiche;
    @FXML private VBox notificationContainer;
    @FXML private ScrollPane scrollNotifiche;

    private UserBean loggedUser;
    private final NotificationController notificationController;

    public HomepagePatientGUIController() {
        this.notificationController = new NotificationController();
    }

    public void setLoggedUser(UserBean user) {
        this.loggedUser = user;
        checkAndShowNotifications();
    }

    @FXML
    public void initialize() {
        // Recupera i dati passati dal Navigator
        UserBean user = (UserBean) Navigator.getInstance().getCurrentData();
        if (user != null) {
            setLoggedUser(user);
            Navigator.getInstance().clearCurrentData();
        }

        addHoverEffect(btnPrenotaVisita);
        addHoverEffect(btnVisualizzaPrenotazioni);

        if (popupNotifiche != null) {
            popupNotifiche.setVisible(false);
            popupNotifiche.setManaged(false);
        }
    }

    private void checkAndShowNotifications() {
        try{
        List<NotificationBean> unreadNotifications =
                notificationController.getUnreadNotificationsForPatient(loggedUser);

        if (unreadNotifications != null && !unreadNotifications.isEmpty()) {
            showNotificationPopup(unreadNotifications);
        }}catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void showNotificationPopup(List<NotificationBean> notifications) {
        notificationContainer.getChildren().clear();

        for (int i = 0; i < notifications.size(); i++) {
            NotificationBean notification = notifications.get(i);

            VBox notificationBox = new VBox(5);
            notificationBox.setStyle("-fx-background-color: #C0C0C0; -fx-background-radius: 10; -fx-padding: 20;");
            notificationBox.setMaxWidth(Double.MAX_VALUE);
            VBox.setMargin(notificationBox, new Insets(0, 0, 10, 0));

            Label lblTitolo = new Label("NOTIFICA " + (i + 1));
            lblTitolo.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");
            lblTitolo.setFont(Font.font("System Bold", 24));

            Label lblMessaggio = new Label(notification.getMessage());
            lblMessaggio.setStyle("-fx-font-size: 16;");
            lblMessaggio.setWrapText(true);
            lblMessaggio.setMaxWidth(Double.MAX_VALUE);

            notificationBox.getChildren().addAll(lblTitolo, lblMessaggio);
            notificationContainer.getChildren().add(notificationBox);
        }

        popupNotifiche.setVisible(true);
        popupNotifiche.setManaged(true);

        if (btnOk != null) {
            btnOk.setDefaultButton(true);
            btnOk.requestFocus();
        }
    }

    @FXML
    private void handleOk() {
        popupNotifiche.setVisible(false);
        popupNotifiche.setManaged(false);

        if (btnOk != null) {
            btnOk.setDefaultButton(false);
        }
    }

    @FXML
    private void handlePrenotaVisita(ActionEvent event) {
        Navigator.getInstance().goToWithData(event, "PatientBooking.fxml", this.loggedUser);
    }

    @FXML
    private void handleVisualizzaPrenotazioni(ActionEvent event) {
        Navigator.getInstance().goToWithData(event, "PatientProfile.fxml", this.loggedUser);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Navigator.getInstance().goTo(event, "LandingPage.fxml");
    }

    private void addHoverEffect(Button button) {
        if (button == null) return;
        String originalStyle = button.getStyle();
        button.setOnMouseEntered(e -> button.setStyle(originalStyle + "-fx-scale-x: 1.05; -fx-scale-y: 1.05;"));
        button.setOnMouseExited(e -> button.setStyle(originalStyle));
    }
}
