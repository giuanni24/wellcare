package maindir.view.cli;

import maindir.bean.UserBean;

public class HomepageDoctorCLIController {
    private final HomepageCLIView view;

    public HomepageDoctorCLIController() {
        this.view = new HomepageCLIView();
    }

    public void start(UserBean loggedUser) {
        view.displayWelcome(loggedUser.getName());

        boolean logout = false;
        while (!logout) {
            int choice = view.showDoctorMenu();

            switch (choice) {
                case 1:
                    viewAgenda();
                    break;
                case 2:
                    manageAvailability(loggedUser);
                    break;
                case 3:
                    logout = true;
                    break;
                default:
                    view.displayMessage("Scelta non valida!");
            }
        }
    }

    private void viewAgenda() {
        new ManageDoctorAppointmentsCLIController().start();
    }

    private void manageAvailability(UserBean loggedUser) {
        new ManageAvailabilityCLIController().start(loggedUser);
    }
}
