package maindir.view.cli;

import maindir.bean.DoctorBean;
import maindir.bean.ServiceBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class PatientBookingCLIView {
    private static final Scanner input = new Scanner(System.in);

    public void displayServices(List<ServiceBean> services) {
        System.out.println("\n=== SERVIZI DISPONIBILI ===");
        int i = 1;
        for (ServiceBean service : services) {
            System.out.println(i++ + ") " + service.getName() +
                    " - " + service.getBasePrice() + " EUR");
        }
        System.out.println();
    }

    public int collectServiceChoice(){
        System.out.print("Inserire il numero associato alla visita: ");
            int choice = input.nextInt();
            input.nextLine();
            return choice;
    }

    public void displayMessage(String message){
        System.out.println(message);
    }

    public void displayDoctors(List<DoctorBean> doctors){
        System.out.println("\n=== DOTTORI DISPONIBILI ===");
        int i = 1;
        for (DoctorBean doctor : doctors) {
            System.out.println(i++ + ") " + doctor.getName() +
                    " " + doctor.getSurname());
        }
        System.out.println();
    }

    public int collectDoctorChoice(){
        System.out.print("Inserire il numero associato al dottore: ");
        int choice = input.nextInt();
        input.nextLine();
        return choice;
    }

    public String collectDateChoice() {
        System.out.print("Inserisci data appuntamento (formato YYYY-MM-DD): ");
        return input.next();
    }

    public void displayBookingSummary(ServiceBean service, DoctorBean doctor, LocalDate date) {
        System.out.println("\n========================================");
        System.out.println("       RIEPILOGO PRENOTAZIONE");
        System.out.println("========================================");
        System.out.println("Servizio:    " + service.getName());
        System.out.println("Costo:       " + service.getBasePrice() + " EUR");
        System.out.println("Medico:      Dr. " + doctor.getName() + " " + doctor.getSurname());
        System.out.println("Data:        " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        System.out.println("========================================\n");
    }

    public String collectConfirmation() {
        System.out.print("Confermi la prenotazione? (S/N): ");
        return input.next();
    }
}
