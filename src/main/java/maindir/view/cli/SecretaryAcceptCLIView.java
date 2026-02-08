package maindir.view.cli;

import maindir.bean.AppointmentBean;

import java.util.List;
import java.util.Scanner;

public class SecretaryAcceptCLIView {
    private final Scanner input;

    public SecretaryAcceptCLIView(){
        this.input = new Scanner(System.in);
    }

    public String requestFiscalCode(){
        System.out.println("Inserisci il codice fiscale del paziente");
        return input.nextLine();
    }

    public void displayMessage(String message){
        System.out.println(message);
    }

    public void displayAppointments(List<AppointmentBean> appointments){
        int i = 1;
        for(AppointmentBean bean: appointments)
            System.out.println(i++ + ") " + bean.getService().getName() + "Dottore: " + bean.getDoctor().getName()+ " " + bean.getDoctor().getSurname());
    }

    public int getChoice(int maxIndex){
        System.out.println("\nSeleziona una prenotazione (1-" + maxIndex + "): ");
        return input.nextInt();
    }
}
