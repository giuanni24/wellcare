package maindir.view.cli;

import java.util.Scanner;

public class LoginCLIView {
    private final Scanner input;

    public LoginCLIView() {
        this.input = new Scanner(System.in);
    }

    public void displayLoginHeader() {
        System.out.println("\n========================================");
        System.out.println("              LOGIN");
        System.out.println("========================================");
    }

    public void displayRegistrationHeader() {
        System.out.println("\n========================================");
        System.out.println("           REGISTRAZIONE");
        System.out.println("========================================");
    }

    public int showAuthMenu() {
        System.out.println("\n========================================");
        System.out.println("     SISTEMA PRENOTAZIONI MEDICHE");
        System.out.println("========================================");
        System.out.println("1. Login");
        System.out.println("2. Registrazione");
        System.out.println("0. Esci");
        System.out.print("Scelta: ");
        try {
            return Integer.parseInt(input.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public String collectEmail() {
        System.out.print("Email: ");
        return input.nextLine();
    }

    public String collectPassword() {
        System.out.print("Password: ");
        return input.nextLine();
    }

    public String collectFiscalCode() {
        System.out.print("Codice Fiscale: ");
        return input.nextLine();
    }

    public String collectName() {
        System.out.print("Nome: ");
        return input.nextLine();
    }

    public String collectSurname() {
        System.out.print("Cognome: ");
        return input.nextLine();
    }

    public void displayLoginSuccess(String userName) {
        System.out.println("\nLogin effettuato con successo!");
        System.out.println("Benvenuto/a, " + userName + "!\n");
    }

    public void displayRegistrationSuccess() {
        System.out.println("\nRegistrazione completata con successo!");
        System.out.println("Ora puoi effettuare il login.\n");
    }

    public void displayLoginFailed() {
        System.out.println("\nLogin fallito! Credenziali non valide.\n");
    }

    public void displayError(String message) {
        System.out.println("ERRORE: " + message);
    }
}
