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
import maindir.controller.NotificationController;
import maindir.exceptions.ControllerException;

import java.util.List;

public class HomepageSecretaryGUIController {

    @FXML private Button btnLogout;
    @FXML private Button btnRegisterInvoice;
    @FXML private Button btnManageRequests;
    @FXML private Button btnAcceptPatient;
    @FXML private Button btnOk;
    @FXML private VBox popupNotifiche;
    @FXML private VBox notificationContainer;
    @FXML private ScrollPane scrollNotifiche;

    private final NotificationController notificationController;

    public HomepageSecretaryGUIController() {
        this.notificationController = new NotificationController();
    }

    @FXML
    public void initialize() {
        addHoverEffect(btnRegisterInvoice);
        addHoverEffect(btnManageRequests);
        addHoverEffect(btnLogout);
        addHoverEffect(btnAcceptPatient);

        if (popupNotifiche != null) {
            popupNotifiche.setVisible(false);
            popupNotifiche.setManaged(false);
        }
        try {
            checkNotifications();
        }catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void checkNotifications() throws ControllerException {
        List<NotificationBean> unread = notificationController.getUnreadNotificationsForSecretary();

        if (unread != null && !unread.isEmpty()) {
            showNotificationPopup(unread);
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
    private void goToManageRequests(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SecretaryRequestList.fxml");
    }

    @FXML
    private void goToAcceptPatient(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SecretaryAcceptPatient.fxml");
    }

    @FXML
    private void goToRegisterInvoice(ActionEvent event) {
        Navigator.getInstance().goTo(event, "SecretaryRegisterInvoice.fxml");
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
