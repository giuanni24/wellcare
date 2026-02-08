package maindir.view.cli;

import maindir.bean.DoctorBean;

import java.time.LocalDate;
import java.util.Scanner;

public class ManageAvailabilityCLIView {

    private Scanner scanner;

    public ManageAvailabilityCLIView() {
        this.scanner = new Scanner(System.in);
    }

    public void displayWelcome(DoctorBean doctor) {
        System.out.println("\n=== GESTIONE INDISPONIBILITA DOTTORE ===");
        System.out.println("Dott. " + doctor.getName() + " " + doctor.getSurname());
    }

    public String collectStartDate() {
        System.out.println("\nInserisci la data di inizio (YYYY-MM-DD) o 'annulla':");
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    public String collectEndDate() {
        System.out.println("\nInserisci la data di fine (YYYY-MM-DD) o 'annulla':");
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    public void displayAffectedAppointments(LocalDate startDate, LocalDate endDate, int count) {
        System.out.println("\nPeriodo: dal " + startDate + " al " + endDate);
        System.out.println("Appuntamenti confermati nel periodo: " + count);
    }

    public boolean askConfirmation() {
        System.out.println("\nConfermi l'operazione? (s/n)");
        System.out.print("> ");
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("s") || response.equals("si");
    }

    public void displayMessage(String message) {
        System.out.println(message);
    }

    public void displaySuccess() {
        System.out.println("\nOperazione completata con successo.");
    }

    public void displayError() {
        System.out.println("\nErrore durante l'operazione. Riprova.");
    }
}
