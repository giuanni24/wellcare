package maindir.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import maindir.bean.DoctorBean;
import maindir.bean.UnavailabilityBean;
import maindir.bean.UserBean;
import maindir.controller.DoctorManageAvailabilityController;
import maindir.exceptions.ControllerException;

import java.time.LocalDate;

public class ManageAvailabilityGUIController {

    @FXML private DatePicker dpStart;
    @FXML private DatePicker dpEnd;
    @FXML private Button btnHome;
    @FXML private Button btnSubmit;
    @FXML private Button btnLogout;
    @FXML private Label lblStatus;
    @FXML private Label lblAffectedCount;
    @FXML private VBox popupConfirm;

    private DoctorManageAvailabilityController applicationController;
    private UserBean loggedUser;
    private UnavailabilityBean currentUnavailability;

    @FXML
    public void initialize() {
        initializeController();
        configureButtons();
        configurePopup();
        configureDatePickers();
    }

    private void initializeController() {
        this.applicationController = new DoctorManageAvailabilityController();
        UserBean user = (UserBean) Navigator.getInstance().getCurrentData();
        if (user != null) {
            setLoggedUser(user);
            Navigator.getInstance().clearCurrentData();
        }
    }

    private void configureButtons() {
        addHoverEffect(btnSubmit);
        addHoverEffect(btnHome);
        addHoverEffect(btnLogout);
    }

    private void configurePopup() {
        if (popupConfirm != null) {
            popupConfirm.setVisible(false);
            popupConfirm.setManaged(false);
        }
    }

    private void configureDatePickers() {
        dpEnd.setDisable(true);
        dpStart.setDayCellFactory(d -> createStartDateCell());
        dpStart.valueProperty().addListener((obs, old, newValue) -> handleStartDateChange(newValue));
    }

    private DateCell createStartDateCell() {
        return new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(shouldDisableStartDate(item, empty));
            }
        };
    }

    private boolean shouldDisableStartDate(LocalDate item, boolean empty) {
        return empty || item == null || item.isBefore(LocalDate.now().plusDays(1));
    }

    private void handleStartDateChange(LocalDate newValue) {
        if (newValue == null) {
            resetEndDatePicker();
            return;
        }

        enableEndDatePicker(newValue);
        resetEndDateIfInvalid(newValue);
    }

    private void resetEndDatePicker() {
        dpEnd.setDisable(true);
        dpEnd.setValue(null);
    }

    private void enableEndDatePicker(LocalDate startDate) {
        dpEnd.setDisable(false);
        dpEnd.setDayCellFactory(d -> createEndDateCell(startDate));
    }

    private void resetEndDateIfInvalid(LocalDate startDate) {
        if (dpEnd.getValue() != null && dpEnd.getValue().isBefore(startDate)) {
            dpEnd.setValue(null);
        }
    }

    private DateCell createEndDateCell(LocalDate startDate) {
        return new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setDisable(shouldDisableEndDate(item, empty, startDate));
            }
        };
    }

    private boolean shouldDisableEndDate(LocalDate item, boolean empty, LocalDate startDate) {
        return empty || item == null || item.isBefore(startDate);
    }


    public void setLoggedUser(UserBean user) {
        this.loggedUser = user;
    }

    @FXML
    private void handleCheckAvailability() {
        LocalDate start = dpStart.getValue();
        LocalDate end = dpEnd.getValue();

        if (start == null || end == null || end.isBefore(start)) {
            showStatus("Seleziona un range di date valido.", true);
            return;
        }

        try {
            DoctorBean doctorBean = new DoctorBean();
            doctorBean.setId(loggedUser.getId());

            currentUnavailability = new UnavailabilityBean();
            currentUnavailability.setDoctor(doctorBean);
            currentUnavailability.setStartDate(start);
            currentUnavailability.setEndDate(end);

            int count = applicationController.countAffectedAppointments(currentUnavailability);

            if (count > 0) {
                lblAffectedCount.setText("Ci sono " + count + " appuntamenti intaccati in questo periodo.");
                showPopup();
            } else {
                confirmIndisponibilita();
            }

        } catch (ControllerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void confirmIndisponibilita() {
        try {
            applicationController.blockUnavailabilityPeriod(currentUnavailability);
            hidePopup();
            showStatus("Indisponibilità registrata con successo!", false);
        } catch (ControllerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void closePopup() {
        hidePopup();
    }

    private void showPopup() {
        if (popupConfirm == null) return;
        popupConfirm.setManaged(true);
        popupConfirm.setVisible(true);
    }

    private void hidePopup() {
        if (popupConfirm == null) return;
        popupConfirm.setVisible(false);
        popupConfirm.setManaged(false);
    }

    private void addHoverEffect(Button button) {
        if (button == null) return;
        button.setOnMouseEntered(e -> { button.setScaleX(1.05); button.setScaleY(1.05); });
        button.setOnMouseExited(e -> { button.setScaleX(1.0); button.setScaleY(1.0); });
    }

    private void showStatus(String msg, boolean isError) {
        lblStatus.setText(msg);
        lblStatus.setStyle("-fx-text-fill: " + (isError ? "#C0392B" : "#27AE60") + ";");
        lblStatus.setVisible(true);
    }

    @FXML
    private void handleHome(ActionEvent event) {
        Navigator.getInstance().goToWithData(event, "DoctorHomepage.fxml", this.loggedUser);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Navigator.getInstance().goTo(event, "LandingPage.fxml");
    }
}
