package maindir.view.cli;

import maindir.bean.AppointmentBean;
import maindir.bean.UserBean;
import maindir.controller.AcceptController;
import maindir.exceptions.ControllerException;

import java.util.Collections;
import java.util.List;

public class SecretaryAcceptCLIController {
    private final SecretaryAcceptCLIView view;
    private final AcceptController acceptController;

    public SecretaryAcceptCLIController() {
        this.view = new SecretaryAcceptCLIView();
        this.acceptController = new AcceptController();
    }

    public void start() {
        List<AppointmentBean> appointments = selectPatientAppointments();

        if (appointments == null || appointments.isEmpty()) {
            view.displayMessage("Nessun appuntamento trovato per oggi.");
            return;
        }

        view.displayAppointments(appointments);
        try {
            int choice = selectAppointment(appointments.size());

            if (choice == -1) {
                view.displayMessage("Selezione non valida!");
                return;
            }

            AppointmentBean selectedAppointment = appointments.get(choice);
            acceptController.acceptPatient(selectedAppointment);

            view.displayMessage("Paziente accettato con successo!");
        }catch (ControllerException e){
            view.displayMessage(e.getMessage());
        }

    }

    private List<AppointmentBean> selectPatientAppointments(){
        String cf = view.requestFiscalCode();
        if (cf.length() != 16) {
            view.displayMessage("Inserire un codice fiscale valido");
            return Collections.emptyList();
        }
    try{
        UserBean patient = acceptController.findByCF(cf);

        return acceptController.findTodayAppointmentsByPatient(patient);

    }catch(ControllerException e){
        view.displayMessage(e.getMessage());
    }
        return Collections.emptyList();
    }

    private int selectAppointment(int size) {
        int choice = view.getChoice(size);

        if (choice < 1 || choice > size) {
            return -1;
        }

        return choice - 1; // Converte da 1-based a 0-based index
    }
}
