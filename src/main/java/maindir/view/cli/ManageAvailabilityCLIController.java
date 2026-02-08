package maindir.view.cli;

import maindir.bean.DoctorBean;
import maindir.bean.UnavailabilityBean;
import maindir.bean.UserBean;
import maindir.controller.DoctorManageAvailabilityController;
import maindir.exceptions.ControllerException;

import java.time.LocalDate;

public class ManageAvailabilityCLIController {

    private final ManageAvailabilityCLIView view;
    private final DoctorManageAvailabilityController applicationController;

    public ManageAvailabilityCLIController() {
        this.view = new ManageAvailabilityCLIView();
        this.applicationController = new DoctorManageAvailabilityController();
    }

    public void start(UserBean logUser) {
        try{
        DoctorBean doctorBean = new DoctorBean();
        doctorBean.setId(logUser.getId());
        doctorBean.setName(logUser.getName());
        doctorBean.setSurname(logUser.getSurname());
        doctorBean.setEmail(logUser.getEmail());
        view.displayWelcome(doctorBean);

        // 1. Richiedi data inizio
        LocalDate startDate = requestStartDate();
        if (startDate == null) {
            view.displayMessage("Operazione annullata.");
            return;
        }

        // 2. Richiedi data fine
        LocalDate endDate = requestEndDate(startDate);
        if (endDate == null) {
            view.displayMessage("Operazione annullata.");
            return;
        }

        // 3. Crea il bean
        UnavailabilityBean bean = new UnavailabilityBean(doctorBean, startDate, endDate);

        // 4. Conta appuntamenti intaccati
        int affectedCount = applicationController.countAffectedAppointments(bean);

        view.displayAffectedAppointments(startDate, endDate, affectedCount);

        // 5. Chiedi conferma
        boolean confirmed = view.askConfirmation();

        if (!confirmed) {
            view.displayMessage("Operazione annullata.");
            return;
        }

        // 6. Procedi con il blocco
        view.displayMessage("\nElaborazione in corso...");
        boolean success = applicationController.blockUnavailabilityPeriod(bean);

        if (success) {
            view.displaySuccess();
        } else {
            view.displayError();
        }}catch(ControllerException e){
            view.displayMessage(e.getMessage());
        }
    }

    private LocalDate requestStartDate() {
        LocalDate minDate = LocalDate.now().plusDays(1);

        while (true) {
            String input = view.collectStartDate();

            if (input.equalsIgnoreCase("annulla")) {
                return null;
            }

            try {
                LocalDate date = LocalDate.parse(input);

                if (date.isBefore(minDate)) {
                    view.displayMessage("La data deve essere almeno domani (" + minDate + ")");
                    continue;
                }

                return date;

            } catch (Exception e) {
                view.displayMessage("Formato data non valido! Usa YYYY-MM-DD");
            }
        }
    }

    private LocalDate requestEndDate(LocalDate startDate) {
        while (true) {
            String input = view.collectEndDate();

            if (input.equalsIgnoreCase("annulla")) {
                return null;
            }

            try {
                LocalDate date = LocalDate.parse(input);

                if (date.isBefore(startDate)) {
                    view.displayMessage("La data di fine deve essere >= alla data di inizio");
                    continue;
                }

                return date;

            } catch (Exception e) {
                view.displayMessage("Formato data non valido! Usa YYYY-MM-DD");
            }
        }
    }
}
