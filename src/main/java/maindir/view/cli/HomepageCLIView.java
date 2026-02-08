package maindir.view.cli;

import maindir.bean.NotificationBean;

import java.util.List;
import java.util.Scanner;

public class HomepageCLIView {
    private final Scanner input;

    public HomepageCLIView(){
        input = new Scanner(System.in);
    }
    public void displayWelcome(String userName) {
        System.out.println("=== BENVENUTO " + userName.toUpperCase() + " ===");
    }

    public int showPatientMenu() {
        System.out.println("\n1. Prenota Visita");
        System.out.println("2. Visualizza Prenotazioni");
        System.out.println("3. Logout");
        System.out.print("Scelta: ");
        return input.nextInt();
    }

    public int showSecretaryMenu() {
        System.out.println("\n1. Gestisci Richieste");
        System.out.println("2. Accettazione Paziente");
        System.out.println("3. Logout");
        System.out.print("Scelta: ");
        return input.nextInt();
    }

    public int showDoctorMenu() {
        System.out.println("\n1. Visualizza Agenda");
        System.out.println("2. Gestisci Disponibilità");
        System.out.println("3. Logout");
        System.out.print("Scelta: ");
        return input.nextInt();
    }

    public void displayMessage(String message){
        System.out.println(message);
    }

    public void displayNotifications(List<NotificationBean> notifications) {
        System.out.println("\n========================================");
        System.out.println("   🔔 HAI NUOVE NOTIFICHE");
        System.out.println("========================================");

        for (int i = 0; i < notifications.size(); i++) {
            NotificationBean notification = notifications.get(i);
            System.out.println("\n[" + (i + 1) + "] " + notification.getMessage());
        }

        System.out.println("\n========================================");
    }

    public void waitForEnter() {
        input.nextLine();
    }

}

