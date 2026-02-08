package maindir.view.cli;

import maindir.bean.AppointmentBean;
import maindir.bean.DateBean;
import maindir.bean.UserBean;
import maindir.controller.PatientProfileController;
import maindir.exceptions.ControllerException;

import java.time.LocalDateTime;

import java.util.List;

public class PatientProfileCLIController {

    private final PatientProfileController profileController;
    private final PatientProfileCLIView view;

    public PatientProfileCLIController() {
        this.profileController = new PatientProfileController();
        this.view = new PatientProfileCLIView();
    }

    public void start(UserBean loggedUser) {
        boolean exit = false;
        while (!exit) {
            try {
                List<AppointmentBean> activeAppointments = profileController.getActiveAppointments(loggedUser);
                view.displayAppointments(activeAppointments);
                int menuChoice = view.showProfileMenu();

                if (menuChoice == 5) {
                    exit = true;
                } else if (menuChoice == 4) {
                    view.displayMessage("Ricaricamento in corso...");
                } else if (menuChoice == 3) {
                    handleRescheduling(loggedUser);
                } else if (menuChoice == 2) {
                    handleReportViewing(loggedUser);
                } else if (menuChoice == 1) {
                    handleCancellation(loggedUser);
                } else {
                    view.displayMessage("Scelta non valida!");
                }
            } catch (NumberFormatException e) {
                view.displayMessage("Input non valido! Inserisci un numero.");
            }catch (ControllerException e){
                view.displayMessage(e.getMessage());
            }
        }
    }

    private void handleCancellation(UserBean loggedUser) {
        try {
            List<AppointmentBean> cancellableAppointments = profileController.getCancellableAppointments(loggedUser);

            if (cancellableAppointments.isEmpty()) {
                view.displayMessage("Nessun appuntamento annullabile.");
                return;
            }

            view.displayAppointments(cancellableAppointments);
            int selectedIndex = view.selectAppointmentToCancel(cancellableAppointments.size()) - 1;

            if (selectedIndex < -1 || selectedIndex >= cancellableAppointments.size()) {
                view.displayMessage("Selezione non valida!");
                return;
            }

            if (selectedIndex == -1) {
                view.displayMessage("Operazione annullata.");
                return;
            }

            AppointmentBean selectedAppointment = cancellableAppointments.get(selectedIndex);

            if (view.confirmCancellation()) {
                boolean success = profileController.cancelAppointment(selectedAppointment);
                if (success) {
                    view.displayMessage("Appuntamento annullato con successo!");
                } else {
                    view.displayMessage("Errore durante l'annullamento!");
                }
            } else {
                view.displayMessage("Annullamento annullato.");
            }

        } catch (NumberFormatException e) {
            view.displayMessage("Input non valido! Inserisci un numero.");
        }catch (ControllerException e){
            view.displayMessage(e.getMessage());
        }
    }

    private void handleReportViewing(UserBean loggedUser) {
        try {
            List<AppointmentBean> completedAppointments = profileController.getCompletedAppointments(loggedUser);

            view.displayCompletedAppointments(completedAppointments);
            int selectedIndex = view.selectAppointmentToViewReport(completedAppointments.size()) - 1;

            if (selectedIndex < -1 || selectedIndex >= completedAppointments.size()) {
                view.displayMessage("Selezione non valida!");
                return;
            }

            if (selectedIndex == -1) {
                view.displayMessage("Operazione annullata.");
                return;
            }

            AppointmentBean selectedAppointment = completedAppointments.get(selectedIndex);
            view.displayReport(selectedAppointment);

        } catch (NumberFormatException e) {
            view.displayMessage("Input non valido! Inserisci un numero.");
        } catch (ControllerException e) {
            view.displayMessage("Errore: " + e.getMessage());
        }
    }

    private void handleRescheduling(UserBean loggedUser) {
        try {
            // 1. Ottieni appuntamenti da riprogrammare
            List<AppointmentBean> reschedulableAppointments = profileController.getReschedulableAppointments(loggedUser);

            // 2. Mostra gli appuntamenti e chiedi quale riprogrammare
            view.displayReschedulableAppointments(reschedulableAppointments);
            int selectedIndex = view.selectAppointmentToReschedule(reschedulableAppointments.size()) - 1;

            if (selectedIndex < -1 || selectedIndex >= reschedulableAppointments.size()) {
                view.displayMessage("Selezione non valida!");
                return;
            }

            if (selectedIndex == -1) {
                view.displayMessage("Operazione annullata.");
                return;
            }

            AppointmentBean selectedAppointment = reschedulableAppointments.get(selectedIndex);

            // 3. Ottieni gli slot disponibili come LocalDateTime
            List<DateBean> availableSlots = profileController.getAvailableSlots(selectedAppointment);

            // 4. Mostra gli slot e chiedi quale scegliere
            view.displayAvailableSlots(availableSlots);
            int slotIndex = view.selectSlot(availableSlots.size()) - 1;

            if (slotIndex < -1 || slotIndex >= availableSlots.size()) {
                view.displayMessage("Selezione non valida!");
                return;
            }

            if (slotIndex == -1) {
                view.displayMessage("Operazione annullata.");
                return;
            }

            // 5. Imposta la data e ora scelta nell'appuntamento
            LocalDateTime selectedSlot = availableSlots.get(slotIndex).getDateTime();
            selectedAppointment.setRequestedDate(selectedSlot.toLocalDate());
            selectedAppointment.setConfirmedTime(selectedSlot.toLocalTime());

            // 6. Conferma e procedi con la riprogrammazione
            if (view.confirmRescheduling()) {
                boolean success = profileController.confirmRescheduling(selectedAppointment);

                if (success) {
                    view.displayMessage("Appuntamento riprogrammato con successo!");
                } else {
                    view.displayMessage("Errore durante la riprogrammazione. Riprova.");
                }
            } else {
                view.displayMessage("Riprogrammazione annullata.");
            }

        } catch (NumberFormatException e) {
            view.displayMessage("Input non valido! Inserisci un numero.");
        } catch (ControllerException e) {
            view.displayMessage("Errore: " + e.getMessage());
        } catch (Exception e) {
            view.displayMessage("Errore imprevisto: " + e.getMessage());
        }
    }

}
