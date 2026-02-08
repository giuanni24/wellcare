package maindir.view.cli;

import maindir.bean.NotificationBean;
import maindir.bean.UserBean;
import maindir.controller.NotificationController;
import maindir.exceptions.ControllerException;

import java.util.List;

public class HomepageSecretaryController {
    private final HomepageCLIView view;
    private final NotificationController notificationController;
    public HomepageSecretaryController() {
        this.view = new HomepageCLIView();
        this.notificationController = new NotificationController();
    }

    public void start(UserBean loggedUser) {
        view.displayWelcome(loggedUser.getName());

        showAndMarkNotifications();

        boolean logout = false;
        while (!logout) {
            int choice = view.showSecretaryMenu();

            switch (choice) {
                case 1:
                    manageBookingRequests();
                    break;
                case 2:
                    acceptUser();
                    break;
                case 3:
                    logout = true;
                    break;
                default:
                    view.displayMessage("Scelta non valida!");
            }
        }
    }
    private void showAndMarkNotifications() {
        try{
        List<NotificationBean> unreadNotifications = notificationController.getUnreadNotificationsForSecretary();

        if (!unreadNotifications.isEmpty()) {
            view.displayNotifications(unreadNotifications);

            view.displayMessage("\nPremi INVIO per continuare...");
            view.waitForEnter();
        }}catch (ControllerException e){
            view.displayMessage(e.getMessage());
        }
    }

    private void manageBookingRequests() {
        new SecretaryBookingCLIController().start();
    }

    private void acceptUser() {
        new SecretaryAcceptCLIController().start();
    }

}
