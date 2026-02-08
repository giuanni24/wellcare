package maindir.view.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import maindir.bean.AppointmentBean;
import maindir.bean.DoctorBean;
import maindir.bean.ServiceBean;
import maindir.bean.UserBean;
import maindir.controller.BookingRequestController;
import maindir.exceptions.ControllerException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class PatientBookingGUIController {

    @FXML private ComboBox<ServiceBean> cmbService;
    @FXML private ComboBox<DoctorBean> cmbDoctor;
    @FXML private DatePicker datePicker;
    @FXML private Button btnConferma;
    @FXML private Label lblMessage;
    @FXML private Button btnHome;
    @FXML private Button btnBack;
    private BookingRequestController appController;
    private UserBean loggedUser;

    @FXML
    public void initialize() {
        // Recupera i dati passati dal Navigator
        appController = new BookingRequestController();
        UserBean user = (UserBean) Navigator.getInstance().getCurrentData();
        if (user != null) {
            setLoggedUser(user);
            Navigator.getInstance().clearCurrentData();
        }

        addHoverEffect(btnBack);
        addHoverEffect(btnConferma);
        addHoverEffect(btnHome);


        datePicker.setDisable(true);
        cmbDoctor.setDisable(true);
        datePicker.setEditable(false);

        setupConverters();
        loadServices();

        cmbService.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadDoctorsForService(newVal);
                cmbDoctor.setDisable(false);

                datePicker.setValue(null);
                datePicker.setDisable(true);
            }
        });

        cmbDoctor.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newDoctor) -> {
            if (newDoctor != null) {
                Platform.runLater(cmbDoctor::hide);
                datePicker.setDisable(false);
                datePicker.setValue(null);
                updateDatePickerCells();
            } else {
                datePicker.setDisable(true);
            }
        });
    }

    private void updateDatePickerCells() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setDisable(empty);
                    return;
                }

                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                    return;
                }

                DayOfWeek dow = date.getDayOfWeek();
                if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
                    setDisable(true);
                    setStyle("-fx-background-color: #EEEEEE; -fx-text-fill: #888888;");
                }
            }
        });
    }

    public void setLoggedUser(UserBean user) {
        this.loggedUser = user;
    }

    private void loadServices() {
        try {
            List<ServiceBean> services = appController.retrieveAllServices();
            cmbService.setItems(FXCollections.observableArrayList(services));
        }catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void loadDoctorsForService(ServiceBean service) {
        try {
            List<DoctorBean> doctors = appController.findDoctorByService(service);
            cmbDoctor.setItems(FXCollections.observableArrayList(doctors));
        }catch(ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    @FXML
    private void handleConferma(ActionEvent event) {
        lblMessage.setVisible(false);

        ServiceBean selectedService = cmbService.getValue();
        DoctorBean selectedDoctor = cmbDoctor.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedService == null || selectedDoctor == null || selectedDate == null) {
            showMessage("Compilare tutti i campi!", true);
            return;
        }

        AppointmentBean requestBean = new AppointmentBean();
        requestBean.setPatient(loggedUser);
        requestBean.setDoctor(selectedDoctor);
        requestBean.setService(selectedService);
        requestBean.setRequestedDate(selectedDate);

        boolean available = appController.isDoctorAvailableOnDate(requestBean);
        if (!available) {
            showMessage("Nessuna disponibilità per questa data.", true);
            return;
        }

        Navigator.getInstance().goToWithData(event, "PatientBookingSummary.fxml", requestBean);
    }

    @FXML
    private void handleHome(ActionEvent event) {
        Navigator.getInstance().goToWithData(event, "PatientHomepage.fxml", this.loggedUser);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        handleHome(event);
    }

    private void showMessage(String text, boolean isError) {
        lblMessage.setText(text);
        lblMessage.setStyle(isError
                ? "-fx-text-fill: red; -fx-font-weight: bold;"
                : "-fx-text-fill: green; -fx-font-weight: bold;");
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

    private void setupConverters() {
        cmbService.setConverter(new StringConverter<>() {
            @Override
            public String toString(ServiceBean s) {
                return (s == null) ? null : s.getName();
            }

            @Override
            public ServiceBean fromString(String string) {
                return null;
            }
        });

        cmbDoctor.setConverter(new StringConverter<>() {
            @Override
            public String toString(DoctorBean d) {
                return (d == null) ? null : "Dr. " + d.getName() + " " + d.getSurname();
            }

            @Override
            public DoctorBean fromString(String string) {
                return null;
            }
        });
    }
}
