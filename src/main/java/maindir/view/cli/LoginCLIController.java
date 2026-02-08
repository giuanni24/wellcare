package maindir.view.cli;

import maindir.bean.UserBean;
import maindir.controller.LoginController;
import maindir.exceptions.ControllerException;
import maindir.exceptions.DuplicateUserException;

public class LoginCLIController {
    private final LoginController loginController;
    private final LoginCLIView view;

    public LoginCLIController() {
        this.loginController = new LoginController();
        this.view = new LoginCLIView();
    }

    private UserBean getCredentials() {
        String email = "";
        String password = "";
        boolean validInput = false;

        while (!validInput) {
            email = view.collectEmail();
            if (email != null && !email.trim().isEmpty()) {
                password = view.collectPassword();
                if (password == null || password.trim().isEmpty()) {
                    view.displayError("La password non può essere vuota!");
                } else {
                    validInput = true;
                }
            } else {
                view.displayError("L'email non può essere vuota!");
            }
        }

        UserBean credentials = new UserBean();
        credentials.setEmail(email.trim());
        credentials.setPassword(password.trim());

        return credentials;
    }

    private UserBean getRegistrationData() {
        String email = collectValidInput("email", view::collectEmail);
        String password = collectValidInput("password", view::collectPassword);
        String fiscalCode = collectValidInput("codice fiscale", view::collectFiscalCode);
        String name = collectValidInput("nome", view::collectName);
        String surname = collectValidInput("cognome", view::collectSurname);

        UserBean newUser = new UserBean();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setFiscalCode(fiscalCode);
        newUser.setName(name);
        newUser.setSurname(surname);

        return newUser;
    }

    private String collectValidInput(String fieldName, java.util.function.Supplier<String> collector) {
        while (true) {
            String input = collector.get();

            if (isValidInput(input)) {
                return input.trim();
            }

            view.displayError("Il " + fieldName + " non può essere vuoto!");
        }
    }

    private boolean isValidInput(String input) {
        return input != null && !input.trim().isEmpty();
    }


    private UserBean handleLogin() {
        view.displayLoginHeader();

        UserBean loggedUser = null;
        boolean authenticated = false;
    try{
        while (!authenticated) {
            UserBean credentials = getCredentials();
            loggedUser = loginController.authenticate(credentials);

            if (loggedUser != null) {
                authenticated = true;
                view.displayLoginSuccess(loggedUser.getName() + " " + loggedUser.getSurname());
            } else {
                view.displayLoginFailed();
            }
        }}catch (ControllerException e){
        view.displayError(e.getMessage());
    }

        return loggedUser;
    }

    private UserBean handleRegistration() {
        try {
            view.displayRegistrationHeader();

            UserBean newUser = getRegistrationData();
            UserBean registeredUser = loginController.register(newUser);
            view.displayRegistrationSuccess();

            return registeredUser;  // ← Restituisci l'utente registrato
        }catch(ControllerException | DuplicateUserException e){
            view.displayError(e.getMessage());
        }
        return null;
    }

    public UserBean start() {
        int choice = view.showAuthMenu();

        switch (choice) {
            case 1:
                return handleLogin();
            case 2:
                return handleRegistration();  // ← Login automatico!
            case 0:
                System.out.println("Arrivederci!");
                System.exit(0);
                break;
            default:
                view.displayError("Scelta non valida!");
                return start();  // Riprova
        }

        return null;
    }

}
