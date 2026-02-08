package maindir.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import maindir.bean.UserBean;
import maindir.controller.LoginController;
import maindir.exceptions.ControllerException;
import maindir.exceptions.DuplicateUserException;

public class LoginGUIController {

    @FXML private Button btnLogin;
    @FXML private Button btnRegistrazione;
    @FXML private Button btnConferma;
    @FXML private TextField txtNome;
    @FXML private TextField txtCognome;
    @FXML private TextField txtCodiceFiscale;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblErrore;

    private final LoginController loginController;
    private boolean isRegistrazioneMode = false;

    public LoginGUIController() {
        this.loginController = new LoginController();
    }

    @FXML
    public void initialize() {
        addHoverEffect(btnLogin);
        addHoverEffect(btnRegistrazione);
        addHoverEffect(btnConferma);
        showLogin();
    }

    @FXML
    private void showLogin() {
        isRegistrazioneMode = false;

        txtNome.setVisible(false);
        txtNome.setManaged(false);
        txtCognome.setVisible(false);
        txtCognome.setManaged(false);
        txtCodiceFiscale.setVisible(false);
        txtCodiceFiscale.setManaged(false);

        btnLogin.setStyle("-fx-background-color: #5A8CAE; -fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 15 50;");
        btnRegistrazione.setStyle("-fx-background-color: #5A8CAE; -fx-text-fill: black; -fx-font-size: 24; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 15 50;");

        lblErrore.setVisible(false);
        lblErrore.setManaged(false);

        clearFields();
    }

    @FXML
    private void showRegistrazione() {
        isRegistrazioneMode = true;

        txtNome.setVisible(true);
        txtNome.setManaged(true);
        txtCognome.setVisible(true);
        txtCognome.setManaged(true);
        txtCodiceFiscale.setVisible(true);
        txtCodiceFiscale.setManaged(true);

        btnLogin.setStyle("-fx-background-color: #5A8CAE; -fx-text-fill: black; -fx-font-size: 24; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 15 50;");
        btnRegistrazione.setStyle("-fx-background-color: #5A8CAE; -fx-text-fill: white; -fx-font-size: 24; -fx-font-weight: bold; -fx-background-radius: 30; -fx-padding: 15 50;");

        lblErrore.setVisible(false);
        lblErrore.setManaged(false);

        clearFields();
    }

    @FXML
    private void handleConferma(ActionEvent event) {
        if (isRegistrazioneMode) {
            handleRegistrazione(event);
        } else {
            handleLogin(event);
        }
    }

    private void handleLogin(ActionEvent event) {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showError("ATTENZIONE: email e/o password non corrette");
            return;
        }

        UserBean userBean = new UserBean();
        userBean.setEmail(email);
        userBean.setPassword(password);
        try{
        UserBean authenticatedUser = loginController.authenticate(userBean);

        if (authenticatedUser == null) {
            showError("ATTENZIONE: email e/o password non corrette");
            return;
        }

        loadHome(event, authenticatedUser);}
        catch (ControllerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void loadHome(ActionEvent event, UserBean user) {
        String fxmlFile;

        switch (user.getRole()) {
            case PATIENT -> {
                fxmlFile = "PatientHomepage.fxml";
                Navigator.getInstance().goToWithData(event, fxmlFile, user);
            }
            case DOCTOR -> {
                fxmlFile = "DoctorHomepage.fxml";
                Navigator.getInstance().goToWithData(event, fxmlFile, user);
            }
            case SECRETARY -> {
                fxmlFile = "SecretaryHomepage.fxml";
                Navigator.getInstance().goTo(event, fxmlFile);
            }
            default -> showError("Ruolo utente non riconosciuto");
        }
    }

    private void handleRegistrazione(ActionEvent event) {
        String nome = txtNome.getText().trim();
        String cognome = txtCognome.getText().trim();
        String codiceFiscale = txtCodiceFiscale.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText();

        if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() ||
                email.isEmpty() || password.isEmpty()) {
            showError("ATTENZIONE: compilare tutti i campi");
            return;
        }

        UserBean userBean = new UserBean();
        userBean.setName(nome);
        userBean.setSurname(cognome);
        userBean.setFiscalCode(codiceFiscale);
        userBean.setEmail(email);
        userBean.setPassword(password);
        try{
        UserBean registeredUser = loginController.register(userBean);

        if (registeredUser != null) {
            showSuccess("Registrazione completata con successo!");
            loadHome(event, registeredUser);
        } else {
            showError("ATTENZIONE: errore durante la registrazione");
        }}catch (ControllerException | DuplicateUserException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }

    private void showError(String message) {
        lblErrore.setText(message);
        lblErrore.setStyle("-fx-background-color: #FFD4A3; -fx-padding: 15; -fx-font-size: 16;");
        lblErrore.setVisible(true);
        lblErrore.setManaged(true);
    }

    private void showSuccess(String message) {
        lblErrore.setText(message);
        lblErrore.setStyle("-fx-background-color: #C8E6C9; -fx-padding: 15; -fx-font-size: 16;");
        lblErrore.setVisible(true);
        lblErrore.setManaged(true);
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

    private void clearFields() {
        txtNome.clear();
        txtCognome.clear();
        txtCodiceFiscale.clear();
        txtEmail.clear();
        txtPassword.clear();
    }
}
