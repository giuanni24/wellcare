package maindir.view.cli;

import maindir.bean.AppointmentBean;
import maindir.bean.DateBean;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class PatientProfileCLIView {

    private final Scanner scanner;

    public PatientProfileCLIView() {
        this.scanner = new Scanner(System.in);
    }

    public void displayAppointments(List<AppointmentBean> appointments) {
        System.out.println("\n========================================");
        System.out.println("        I TUOI APPUNTAMENTI");
        System.out.println("========================================");
        if (appointments.isEmpty()) {
            System.out.println("Nessun appuntamento trovato.");
        } else {
            for (int i = 0; i < appointments.size(); i++) {
                AppointmentBean appointment = appointments.get(i);
                System.out.println("\n[" + (i + 1) + "]");
                System.out.println("  Medico: Dr. " + appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname());
                System.out.println("  Servizio: " + appointment.getService().getName());
                System.out.println("  Data: " + appointment.getRequestedDate());
                if (appointment.getConfirmedTime() != null) {
                    System.out.println("  Ora: " + appointment.getConfirmedTime());
                }
                System.out.println("  Stato: " + appointment.getStatus());
            }
        }
        System.out.println("========================================");
    }

    public void displayCompletedAppointments(List<AppointmentBean> appointments) {
        System.out.println("\n========================================");
        System.out.println("      APPUNTAMENTI COMPLETATI");
        System.out.println("========================================");
        if (appointments.isEmpty()) {
            System.out.println("Nessun appuntamento completato.");
        } else {
            for (int i = 0; i < appointments.size(); i++) {
                AppointmentBean appointment = appointments.get(i);
                System.out.println("\n[" + (i + 1) + "]");
                System.out.println("  Medico: Dr. " + appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname());
                System.out.println("  Servizio: " + appointment.getService().getName());
                System.out.println("  Data: " + appointment.getRequestedDate());
                System.out.println("  Ora: " + appointment.getConfirmedTime());
            }
        }
        System.out.println("========================================");
    }

    public void displayReschedulableAppointments(List<AppointmentBean> appointments) {
        System.out.println("\n========================================");
        System.out.println("   APPUNTAMENTI DA RIPROGRAMMARE");
        System.out.println("========================================");
        if (appointments.isEmpty()) {
            System.out.println("Nessun appuntamento da riprogrammare.");
        } else {
            for (int i = 0; i < appointments.size(); i++) {
                AppointmentBean appointment = appointments.get(i);
                System.out.println("\n[" + (i + 1) + "]");
                System.out.println("  Medico: Dr. " + appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname());
                System.out.println("  Servizio: " + appointment.getService().getName());
                System.out.println("  Data originale: " + appointment.getRequestedDate());
                System.out.println("  Stato: " + appointment.getStatus());
            }
        }
        System.out.println("========================================");
    }

    public void displayAvailableSlots(List<DateBean> slots) {
        System.out.println("\n========================================");
        System.out.println(" SLOT DISPONIBILI PER RIPROGRAMMAZIONE");
        System.out.println("========================================");
        if (slots.isEmpty()) {
            System.out.println("Nessuno slot disponibile.");
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle' HH:mm");
            for (int i = 0; i < slots.size(); i++) {
                System.out.println("[" + (i + 1) + "] " + slots.get(i).getDateTime().format(formatter));
            }
        }
        System.out.println("========================================");
    }


    public void displayReport(AppointmentBean appointment) {
        System.out.println("\n========================================");
        System.out.println("           REFERTO MEDICO");
        System.out.println("========================================");
        System.out.println("Appuntamento ID: " + appointment.getId());
        System.out.println("Medico: Dr. " + appointment.getDoctor().getName() + " " + appointment.getDoctor().getSurname());
        System.out.println("Servizio: " + appointment.getService().getName());
        System.out.println("Data: " + appointment.getRequestedDate());
        System.out.println("Ora: " + appointment.getConfirmedTime());
        System.out.println("\n--- REFERTO ---");
        if (appointment.getReport() != null && !appointment.getReport().isEmpty()) {
            System.out.println(appointment.getReport());
        } else {
            System.out.println("Nessun referto disponibile.");
        }
        System.out.println("========================================");
    }

    public int showProfileMenu() {
        System.out.println("\n--- MENU PROFILO ---");
        System.out.println("1. Annulla una prenotazione");
        System.out.println("2. Visualizza referti");
        System.out.println("3. Riprogramma visite");
        System.out.println("4. Ricarica");
        System.out.println("5. Torna al menu principale");
        System.out.print("Scelta: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public int selectAppointmentToCancel(int maxIndex) {
        System.out.print("\nSeleziona l'appuntamento da annullare (1-" + maxIndex + ") o 0 per annullare: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public int selectAppointmentToViewReport(int maxIndex) {
        System.out.print("\nSeleziona l'appuntamento di cui visualizzare il referto (1-" + maxIndex + ") o 0 per annullare: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public int selectAppointmentToReschedule(int maxIndex) {
        System.out.print("\nSeleziona l'appuntamento da riprogrammare (1-" + maxIndex + ") o 0 per annullare: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public int selectSlot(int maxIndex) {
        System.out.print("\nSeleziona lo slot desiderato (1-" + maxIndex + ") o 0 per annullare: ");
        return Integer.parseInt(scanner.nextLine());
    }

    public boolean confirmCancellation() {
        System.out.print("\nConfermi l'annullamento? (s/n): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("s");
    }

    public boolean confirmRescheduling() {
        System.out.print("\nConfermi la riprogrammazione? (s/n): ");
        String answer = scanner.nextLine();
        return answer.equalsIgnoreCase("s");
    }

    public void displayMessage(String message) {
        System.out.println("\n" + message);
    }
}
