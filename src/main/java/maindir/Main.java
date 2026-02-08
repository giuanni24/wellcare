package maindir;

import javafx.application.Application;
import maindir.controller.ApplicationController;
import maindir.persistance.DAOFactory;
import maindir.view.gui.JavaFXStarter; // Importa la classe creata sopra

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- 1. CONFIGURAZIONE PERSISTENZA (DB) ---
        System.out.println("Seleziona la modalità di esecuzione:");
        System.out.println("1. FULL (database JDBC)");
        System.out.println("2. DEMO (in-memory)");
        System.out.println("3. FULL (database FileSystem)");
        System.out.print("Scelta: ");

        int dbChoice = -1;
        if (scanner.hasNextInt()) {
            dbChoice = scanner.nextInt();
            scanner.nextLine(); // Consuma il resto della riga
        }

        switch (dbChoice) {
            case 1 -> {
                DAOFactory.setMode(DAOFactory.PersistenceMode.JDBC);
                System.out.println("--> Modalità JDBC attiva");
            }
            case 3 -> {
                DAOFactory.setMode(DAOFactory.PersistenceMode.FILE);
                System.out.println("--> Modalità FILE SYSTEM attiva");
            }
            default -> {
                DAOFactory.setMode(DAOFactory.PersistenceMode.MEMORY);
                System.out.println("--> Modalità MEMORY (Demo) attiva");
            }
        }

        // --- 2. SCELTA INTERFACCIA (CLI o GUI) ---
        System.out.println("\nScegli interfaccia:");
        System.out.println("1. CLI (Riga di comando)");
        System.out.println("2. GUI (Interfaccia Grafica)");
        System.out.print("Scelta: ");

        int uiChoice = scanner.nextInt();

        if (uiChoice == 1) {
            // --- AVVIO CLI ---
            System.out.println("Avvio CLI...");
            // Qui usi il tuo controller esistente che gestisce il loop della console
            new ApplicationController().startCLI();

        } else {
            // --- AVVIO GUI ---
            System.out.println("Avvio GUI...");
            // Delegamo l'avvio alla classe JavaFXStarter creata prima
            Application.launch(JavaFXStarter.class, args);
        }
    }
}