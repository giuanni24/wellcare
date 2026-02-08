package maindir.view.cli;

import maindir.bean.NotificationBean;
import maindir.bean.UserBean;
import maindir.controller.NotificationController;
import maindir.exceptions.ControllerException;

import java.util.List;

public class HomepagePatientCLIController {
    private final HomepageCLIView view;
    private final NotificationController notificationController;
    public HomepagePatientCLIController() {
        this.view = new HomepageCLIView();
        this.notificationController = new NotificationController();
    }

    public void start(UserBean loggedUser) {
        view.displayWelcome(loggedUser.getName());

        showAndMarkNotifications(loggedUser);

        boolean logout = false;
        while (!logout) {
            int choice = view.showPatientMenu();

            switch (choice) {
                case 1:
                    bookVisit(loggedUser);
                    break;
                case 2:
                    viewMyBookings(loggedUser);
                    break;
                case 3:
                    logout = true;
                    break;
                default:
                    view.displayMessage("Scelta non valida!");
            }
        }
    }
    private void showAndMarkNotifications(UserBean loggedUser) {
        // Carica e marca automaticamente come lette
        try{
        List<NotificationBean> unreadNotifications = notificationController.getUnreadNotificationsForPatient(loggedUser);

        if (!unreadNotifications.isEmpty()) {
            view.displayNotifications(unreadNotifications);

            view.displayMessage("\nPremi INVIO per continuare...");
            view.waitForEnter();
        }}catch (ControllerException e){
            view.displayMessage(e.getMessage());
        }
    }


    private void bookVisit(UserBean loggedUser) {
        new PatientBookingCLIController().start(loggedUser);
    }

    private void viewMyBookings(UserBean loggedUser) {
        new PatientProfileCLIController().start(loggedUser);
    }
}
