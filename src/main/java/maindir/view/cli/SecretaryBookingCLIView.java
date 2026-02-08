package maindir.view.cli;

import maindir.bean.AppointmentBean;
import maindir.bean.InvoiceBean;

import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class SecretaryBookingCLIView {
    private final Scanner scanner;

    public SecretaryBookingCLIView() {
        this.scanner = new Scanner(System.in);
    }

    public void displayPendingRequests(List<AppointmentBean> requests) {
        System.out.println("\n========================================");
        System.out.println("   RICHIESTE DI PRENOTAZIONE IN ATTESA");
        System.out.println("========================================");

        if (requests.isEmpty()) {
            return;
        }

        for (int i = 0; i < requests.size(); i++) {
            AppointmentBean appointment = requests.get(i);
            System.out.println("\n[" + (i + 1) + "]");
            System.out.println("    Paziente: " + appointment.getPatient().getName() + " " + appointment.getPatient().getSurname());
            System.out.println("    Medico: Dr. " + appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname());
            System.out.println("    Servizio: " + appointment.getService().getName());
            System.out.println("    Data Richiesta: " + appointment.getRequestedDate());
        }
        System.out.println("\n========================================");
    }

    public void displayNoRequests() {
        System.out.println("\nNessuna richiesta in attesa.");
    }

    public int showManageMenu() {
        System.out.println("\n--- MENU GESTIONE ---");
        System.out.println("1. Seleziona una richiesta da gestire");
        System.out.println("2. Torna alla homepage");
        System.out.print("Scelta: ");
        return scanner.nextInt();
    }

    public int selectRequest(int maxIndex) {
        System.out.print("\nInserisci il numero della richiesta da gestire (1-" + maxIndex + "): ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice-1;
    }

    public void displayRequestDetails(AppointmentBean appointment) {
        System.out.println("\n========================================");
        System.out.println("   DETTAGLI RICHIESTA");
        System.out.println("========================================");
        System.out.println("ID Richiesta: " + appointment.getId());
        System.out.println("Paziente: " + appointment.getPatient().getName() + " " + appointment.getPatient().getSurname());
        System.out.println("Email: " + appointment.getPatient().getEmail());
        System.out.println("Codice Fiscale: " + appointment.getPatient().getFiscalCode());
        System.out.println("Medico: Dr. " + appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname());
        System.out.println("Servizio: " + appointment.getService().getName());
        System.out.println("Prezzo: €" + appointment.getService().getBasePrice());
        System.out.println("Data Richiesta: " + appointment.getRequestedDate());
        System.out.println("========================================");
    }

    public int showRequestActionMenu() {
        System.out.println("\n--- AZIONI ---");
        System.out.println("1. Approva richiesta");
        System.out.println("2. Rifiuta richiesta");
        System.out.println("3. Torna indietro");
        System.out.print("Scelta: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    public boolean confirmApproval() {
        System.out.print("\nConfermi l'approvazione? (s/n): ");
        String answer = scanner.next();
        return answer.equalsIgnoreCase("s");
    }

    public boolean confirmRejection() {
        System.out.print("\nConfermi il rifiuto? (s/n): ");
        String answer = scanner.next();
        return answer.equalsIgnoreCase("s");
    }

    public void displayAvailableSlots(List<LocalTime> slots) {
        System.out.println("\n========================================");
        System.out.println("   SLOT DISPONIBILI");
        System.out.println("========================================");

        for (int i = 0; i < slots.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + slots.get(i));
        }
        System.out.println("========================================");
    }

    public int selectSlot(int maxIndex) {
        System.out.print("\nSeleziona uno slot (1-" + maxIndex + "): ");
        int choice = scanner.nextInt();
        return choice-1;
    }


    public void displayMessage(String message) {
        System.out.println("\n" + message);
    }

    public void displayUnpaidInvoices(List<InvoiceBean> invoices) {
        System.out.println("\n========================================");
        System.out.println("   FATTURE NON PAGATE DEL PAZIENTE");
        System.out.println("========================================");

        if (invoices.isEmpty()) {
            System.out.println("Nessuna fattura non pagata.");
        } else {
            for (int i = 0; i < invoices.size(); i++) {
                InvoiceBean invoice = invoices.get(i);
                System.out.println("\n[" + (i + 1) + "] ID: " + invoice.getId());
                System.out.println("    Importo: €" + invoice.getAmount());
            }
        }
        System.out.println("========================================");
    }

    public int showProceedOrRejectMenu() {
        System.out.println("\n--- DECISIONE ---");
        System.out.println("1. Continua con la procedura");
        System.out.println("2. Rifiuta richiesta");
        System.out.print("Scelta: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        return choice;
    }

    public String collectRejectionReason() {
        System.out.print("\nInserisci motivazione del rifiuto: ");
        return scanner.nextLine();
    }

}
