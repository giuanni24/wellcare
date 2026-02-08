package maindir.view.cli;

import maindir.bean.InvoiceBean;
import maindir.bean.AppointmentBean;
import java.util.List;
import java.util.Scanner;

public class SecretaryRegisterInvoiceCLIView {
    private final Scanner scanner;

    public SecretaryRegisterInvoiceCLIView() {
        this.scanner = new Scanner(System.in);
    }

    public String collectFiscalCode() {
        System.out.print("\nInserisci il codice fiscale del paziente: ");
        return scanner.nextLine();
    }

    public void displayUnpaidInvoices(List<InvoiceBean> invoices) {
        System.out.println("\n========================================");
        System.out.println("   FATTURE NON PAGATE");
        System.out.println("========================================");

        if (invoices.isEmpty()) {
            System.out.println("Nessuna fattura non pagata trovata.");
        } else {
            for (int i = 0; i < invoices.size(); i++) {
                InvoiceBean invoice = invoices.get(i);
                System.out.println("\n[" + (i + 1) + "]");
                System.out.println("    ID Fattura: " + invoice.getId());
                System.out.println("    Importo: €" + String.format("%.2f", invoice.getAmount()));
                System.out.println("    Stato: " + invoice.getPaymentStatus());
            }
        }
        System.out.println("========================================");
    }

    public int selectInvoice(int maxIndex) {
        System.out.print("\nSeleziona la fattura da regolarizzare (1-" + maxIndex + "): ");
        try {
            return Integer.parseInt(scanner.nextLine()) - 1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void displayInvoiceDetails(InvoiceBean invoice, AppointmentBean appointment) {
        System.out.println("\n========================================");
        System.out.println("   DETTAGLI FATTURA");
        System.out.println("========================================");
        System.out.println("ID Fattura: " + invoice.getId());
        System.out.println("Importo: €" + String.format("%.2f", invoice.getAmount()));
        System.out.println("Stato: " + invoice.getPaymentStatus());
        System.out.println("\n--- APPUNTAMENTO ASSOCIATO ---");
        System.out.println("Paziente: " + appointment.getPatient().getName() + " " + appointment.getPatient().getSurname());
        System.out.println("Medico: Dr. " + appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname());
        System.out.println("Servizio: " + appointment.getService().getName());
        System.out.println("Data: " + appointment.getRequestedDate());
        if (appointment.getConfirmedTime() != null) {
            System.out.println("Ora: " + appointment.getConfirmedTime());
        }
        System.out.println("========================================");
    }

    public boolean confirmPayment() {
        System.out.print("\nConfermi la regolarizzazione del pagamento? (s/n): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("s");
    }

    public void displayMessage(String message) {
        System.out.println("\n" + message);
    }

    public void displayError(String message) {
        System.out.println("\nERRORE: " + message);
    }
}
