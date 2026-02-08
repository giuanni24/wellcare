package maindir.view.cli;

import maindir.bean.AppointmentBean;
import maindir.bean.InvoiceBean;
import maindir.controller.BookingRequestController;
import maindir.exceptions.ControllerException;

import java.time.LocalTime;
import java.util.List;

public class SecretaryBookingCLIController {

    private final BookingRequestController bookingController;
    private final SecretaryBookingCLIView view;

    public SecretaryBookingCLIController() {
        this.bookingController = new BookingRequestController();
        this.view = new SecretaryBookingCLIView();
    }

    public void start() {
        try{
        while (true) {
            List<AppointmentBean> pendingRequests = bookingController.getPendingRequests();

            if (pendingRequests.isEmpty()) {
                view.displayNoRequests();
                return;
            }

            view.displayPendingRequests(pendingRequests);
            int menuChoice = view.showManageMenu();

            if (menuChoice == 2) {
                return;
            }

            if (menuChoice == 1) {
                processSelectedRequest(pendingRequests);
            } else {
                view.displayMessage("Scelta non valida!");
            }
        }}catch (ControllerException e){
            view.displayMessage(e.getMessage());
        }
    }

    private void processSelectedRequest(List<AppointmentBean> pendingRequests) {
        AppointmentBean selectedRequest = selectRequest(pendingRequests);

        if (selectedRequest == null) {
            return;
        }

        if (!handleUnpaidInvoicesDecision(selectedRequest)) {
            return;
        }

        LocalTime selectedSlot = handleSlotSelection(selectedRequest);

        if (selectedSlot != null) {
            selectedRequest.setConfirmedTime(selectedSlot);
            finalizeApproval(selectedRequest);
        }
    }


    private AppointmentBean selectRequest(List<AppointmentBean> requests) {
        int selectedIndex = view.selectRequest(requests.size());

        if (selectedIndex < 0 || selectedIndex >= requests.size()) {
            view.displayMessage("Selezione non valida!");
            return null;
        }

        AppointmentBean selected = requests.get(selectedIndex);
        view.displayRequestDetails(selected);
        return selected;
    }

    private boolean handleUnpaidInvoicesDecision(AppointmentBean request) {
        try {
            List<InvoiceBean> unpaidInvoices = bookingController.getUnpaidInvoices(request.getPatient());
            view.displayUnpaidInvoices(unpaidInvoices);

            int decision = view.showProceedOrRejectMenu();

            if (decision == 2) {
                String reason = view.collectRejectionReason();
                request.setRejectionReason(reason);
                if (view.confirmRejection()) {
                    bookingController.rejectRequest(request);
                    view.displayMessage("Richiesta rifiutata con successo!");
                }
                return false;
            }

            if (decision != 1) {
                view.displayMessage("Scelta non valida!");
                return false;
            }

            return true;
        }catch (ControllerException e){
            view.displayMessage(e.getMessage());
        }
        return false;
    }

    private LocalTime handleSlotSelection(AppointmentBean request) {
        try {
            List<LocalTime> availableSlots = bookingController.getAvailableSlots(request);

            if (availableSlots.isEmpty()) {
                request.setRejectionReason("Tutti i posti occupati per la data richiesta");
                bookingController.rejectRequest(request);
                view.displayMessage("Nessuno slot disponibile. Richiesta rifiutata automaticamente.");
                return null;
            }

            view.displayAvailableSlots(availableSlots);
            int selectedIndex = view.selectSlot(availableSlots.size());

            if (selectedIndex < 0 || selectedIndex >= availableSlots.size()) {
                view.displayMessage("Selezione slot non valida!");
                return null;
            }

            return availableSlots.get(selectedIndex);
        }catch (ControllerException e){
            view.displayMessage(e.getMessage());
        }
        return null;
    }

    private void finalizeApproval(AppointmentBean request) {
        try{
        boolean success = bookingController.acceptRequest(request);

        if (success) {
            view.displayMessage("Richiesta approvata per le " + request.getConfirmedTime() + "!");
        } else {
            view.displayMessage("Errore durante l'approvazione!");
        }}catch(ControllerException e){
            view.displayMessage(e.getMessage());
        }
    }
}
