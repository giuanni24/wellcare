package maindir.view.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import maindir.bean.AppointmentBean;
import maindir.bean.DateBean;
import maindir.bean.UserBean;
import maindir.controller.PatientProfileController;
import maindir.exceptions.ControllerException;
import maindir.exceptions.ExpiredSlotException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class PatientReprogramGUIController {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<LocalTime> cmbHour;
    @FXML private Label lblMessage;
    @FXML private Button btnHome;
    @FXML private Button btnConferma;
    @FXML private Button btnBack;

    private UserBean loggedUser;
    private AppointmentBean currentAppointment;

    private PatientProfileController profileController;
    private List<DateBean> availableSlots;

    @FXML
    public void initialize() {
        // Recupera i dati passati dal Navigator
        this.profileController = new PatientProfileController();
        AppointmentBean app = (AppointmentBean) Navigator.getInstance().getCurrentData();
        if (app != null) {
            initData(app.getPatient(), app);
            Navigator.getInstance().clearCurrentData();
        }

        addHoverEffect(btnConferma);
        addHoverEffect(btnHome);
        addHoverEffect(btnBack);


        datePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                populateHoursForDate(newDate);
            } else {
                cmbHour.getItems().clear();
                cmbHour.setDisable(true);
            }
        });
    }

    public void initData(UserBean user, AppointmentBean app) {
        this.loggedUser = user;
        this.currentAppointment = app;
        loadAvailableSlots();
    }

    private void loadAvailableSlots() {
        try {
            this.availableSlots = profileController.getAvailableSlots(currentAppointment);
            setupDatePickerFactory();
            datePicker.setDisable(false);

        } catch (ControllerException e) {
            showMessage(e.getMessage(), true);
            datePicker.setDisable(true);
            cmbHour.setDisable(true);
            btnConferma.setDisable(true);
        }
    }

    private void setupDatePickerFactory() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date == null || empty) return;

                boolean isAvailable = availableSlots.stream()
                        .anyMatch(slot -> slot.getDateTime().toLocalDate().equals(date));

                if (!isAvailable) {
                    setDisable(true);
                    setStyle("-fx-background-color: #eeeeee;");
                } else {
                    setStyle("-fx-font-weight: bold; -fx-background-color: #ffffff;");
                }
            }
        });
    }

    private void populateHoursForDate(LocalDate date) {
        List<LocalTime> hours = availableSlots.stream()
                .filter(slot -> slot.getDateTime().toLocalDate().equals(date))
                .map(slot -> slot.getDateTime().toLocalTime())
                .sorted()
                .toList();

        cmbHour.setItems(FXCollections.observableArrayList(hours));
        cmbHour.setDisable(false);
    }

    @FXML
    private void handleConferma() {
        LocalDate selectedDate = datePicker.getValue();
        LocalTime selectedTime = cmbHour.getValue();

        if (selectedDate == null || selectedTime == null) {
            showMessage("Seleziona data e orario.", true);
            return;
        }

        try {
            currentAppointment.setRequestedDate(selectedDate);
            currentAppointment.setConfirmedTime(selectedTime);

            boolean success = profileController.confirmRescheduling(currentAppointment);

            if (success) {
                showMessage("Riprogrammazione avvenuta con successo!", false);
                delayAndBack();
            }

        } catch (ControllerException e) {
            showMessage("Errore: " + e.getMessage(), true);
        }catch (ExpiredSlotException e) {
            showMessage(e.getMessage(), true);
            delayAndBack();
        }
    }

    @FXML
    private void handleHome(ActionEvent event) {
        Navigator.getInstance().goToWithData(event, "PatientHomepage.fxml", loggedUser);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        Navigator.getInstance().goToWithData(event, "PatientProfile.fxml", loggedUser);
    }

    private void delayAndBack() {
        btnConferma.setDisable(true);
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    // Crea un ActionEvent fittizio per la navigazione automatica
                    ActionEvent event = new ActionEvent(btnBack, null);
                    handleBack(event);
                });
            }
        }, 1500);
    }

    private void showMessage(String msg, boolean isError) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: " + (isError ? "red" : "green") + "; -fx-font-weight: bold;");
        lblMessage.setVisible(true);
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
