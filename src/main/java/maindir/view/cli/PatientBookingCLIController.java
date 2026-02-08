package maindir.view.cli;

import maindir.bean.AppointmentBean;
import maindir.bean.DoctorBean;
import maindir.bean.ServiceBean;
import maindir.bean.UserBean;
import maindir.controller.BookingRequestController;
import maindir.exceptions.ControllerException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class PatientBookingCLIController {
    private final BookingRequestController bookingController;
    private final PatientBookingCLIView view;

    public PatientBookingCLIController() {
        this.bookingController = new BookingRequestController();
        this.view = new PatientBookingCLIView();
    }

    private ServiceBean selectService() {
        ServiceBean selectedService = null;

        while (selectedService == null) {
            try {
                List<ServiceBean> services = bookingController.retrieveAllServices();
                view.displayServices(services);

                int choice = -1;
                boolean valid = false;

                while (!valid) {
                    choice = view.collectServiceChoice();
                    if (choice >= 1 && choice <= services.size()) {
                        valid = true;
                    } else {
                        view.displayMessage("Scelta non valida!");
                    }
                }
                selectedService = services.get(choice - 1);
            } catch (ControllerException e) {
                view.displayMessage("Errore: " + e.getMessage());
            }
        }

        return selectedService;
    }


    private DoctorBean selectDoctor(ServiceBean service) {
        DoctorBean selectedDoctor = null;

        while (selectedDoctor == null) {
            try {
                List<DoctorBean> doctors = bookingController.findDoctorByService(service);

                if (doctors == null || doctors.isEmpty()) {
                    view.displayMessage("Nessun dottore disponibile per questo servizio");
                    return null; // Non c'è nulla da selezionare
                }

                view.displayDoctors(doctors);

                int choice = -1;
                boolean valid = false;

                while (!valid) {
                    choice = view.collectDoctorChoice();

                    if (choice >= 1 && choice <= doctors.size()) {
                        valid = true;
                    } else {
                        view.displayMessage("Scelta non valida! Inserisci un numero tra 1 e " + doctors.size());
                    }
                }

                selectedDoctor = doctors.get(choice - 1);

            } catch (ControllerException e) {
                view.displayMessage("Errore: " + e.getMessage());
            }
        }

        return selectedDoctor;
    }


    private LocalDate selectDate(DoctorBean doctorBean) {
        LocalDate selectedDate = null;
        boolean valid = false;
        view.displayMessage("Giorni disponibili: " + doctorBean.getAvailableDays());
        while (!valid) {
            String dateInput = view.collectDateChoice();

            try {
                selectedDate = LocalDate.parse(dateInput);

                if (selectedDate.isBefore(LocalDate.now())) {
                    view.displayMessage("Non puoi prenotare nel passato!");
                } else {
                    // Crea un bean temporaneo per la validazione
                    AppointmentBean tempBean = new AppointmentBean();
                    tempBean.setDoctor(doctorBean);
                    tempBean.setRequestedDate(selectedDate);

                    if (!bookingController.isDoctorAvailableOnDate(tempBean)) {
                        view.displayMessage("Il dottore non è disponibile in questa data! Scegli un'altra data.");
                    } else {
                        valid = true;
                    }
                }

            } catch (DateTimeParseException e) {
                view.displayMessage("Formato data non valido! Usa YYYY-MM-DD (es. 2026-03-15)");
            }
        }

        return selectedDate;
    }



    private boolean showSummaryAndConfirm(ServiceBean service, DoctorBean doctor, LocalDate date) {
        view.displayBookingSummary(service, doctor, date);
        String response = "";
        boolean validInput = false;

        while (!validInput) {
            response = view.collectConfirmation().trim().toUpperCase();

            if (response.equals("S") || response.equals("N")) {
                validInput = true;
            } else {
                view.displayMessage("Risposta non valida. Inserisci S (Sì) o N (No).");
            }
        }

        return response.equals("S");
    }

    private void submitRequest(UserBean user, ServiceBean service, DoctorBean doctor, LocalDate date) {
        AppointmentBean appointment = new AppointmentBean();
        try{
        appointment.setPatient(user);
        appointment.setService(service);
        appointment.setDoctor(doctor);
        appointment.setRequestedDate(date);
        bookingController.createBookingRequest(appointment);

        view.displayMessage("Richiesta di prenotazione inviata con successo!");
        view.displayMessage("Riceverai conferma dalla segreteria.");
        }catch (ControllerException e){
            view.displayMessage("Errore: " + e.getMessage());
        }
    }

    public void start(UserBean logUser) {
        ServiceBean selectedService = selectService();
        DoctorBean selectedDoctor = selectDoctor(selectedService);

        if (selectedDoctor == null) {
            view.displayMessage("Impossibile procedere: nessun dottore disponibile.");
            return;
        }
        LocalDate selectedDate = selectDate(selectedDoctor);
        boolean isConfirmed = showSummaryAndConfirm(selectedService, selectedDoctor, selectedDate);

        if (isConfirmed) {
            submitRequest(logUser, selectedService, selectedDoctor, selectedDate);
        } else {
            view.displayMessage("Prenotazione annullata");
        }
    }

}
