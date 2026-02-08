package maindir.controller;

import maindir.bean.UserBean;
import maindir.view.cli.HomepageDoctorCLIController;
import maindir.view.cli.HomepagePatientCLIController;
import maindir.view.cli.HomepageSecretaryController;
import maindir.view.cli.LoginCLIController;

import java.util.Scanner;

public class ApplicationController {

    public void startCLI() {
        Scanner input = new Scanner(System.in);
        boolean continueApp = true;

        while (continueApp) {
            LoginCLIController loginController = new LoginCLIController();
            UserBean loggedUser = loginController.start();

            routeUserCLI(loggedUser);

            System.out.print("\nVuoi fare un nuovo login? (s/n): ");
            String answer = input.nextLine().trim();
            continueApp = answer.equalsIgnoreCase("s");
        }

        System.out.println("Arrivederci!");
    }

    private void routeUserCLI(UserBean loggedUser) {
        switch (loggedUser.getRole()) {
            case PATIENT:
                new HomepagePatientCLIController().start(loggedUser);
                break;
            case SECRETARY:
                new HomepageSecretaryController().start(loggedUser);
                break;
            case DOCTOR:
                new HomepageDoctorCLIController().start(loggedUser);
                break;
        }
    }
}
