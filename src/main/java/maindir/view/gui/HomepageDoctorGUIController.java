package maindir.view.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import maindir.bean.UserBean;


public class HomepageDoctorGUIController {

    @FXML private Button btnLogout;
    @FXML private Button btnAgenda;
    @FXML private Button btnAvailability;

    private UserBean loggedUser;

    @FXML
    public void initialize() {
        // Recupera i dati passati dal Navigator
        UserBean user = (UserBean) Navigator.getInstance().getCurrentData();
        if (user != null) {
            setLoggedUser(user);
            Navigator.getInstance().clearCurrentData();
        }

        addHoverEffect(btnLogout);
        addHoverEffect(btnAgenda);
        addHoverEffect(btnAvailability);
    }

    private void addHoverEffect(Button button) {
        if (button == null) return;
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });
        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }

    public void setLoggedUser(UserBean user) {
        this.loggedUser = user;
    }

    @FXML
    private void goToAgenda(ActionEvent event) {
        //caso
    }

    @FXML
    private void goToAvailability(ActionEvent event) {
        Navigator.getInstance().goToWithData(event, "ManageAvailability.fxml", this.loggedUser);
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        Navigator.getInstance().goTo(event, "LandingPage.fxml");
    }
}
