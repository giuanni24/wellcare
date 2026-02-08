package maindir.observer;

import maindir.exceptions.DAOException;
import maindir.model.Appointment;
import maindir.model.Notification;
import maindir.model.enums.AppointmentStatus;
import maindir.model.enums.Role;
import maindir.persistance.dao.NotificationDAO;
import maindir.persistance.DAOFactory;

public class NotificationObserver implements Observer {
    private static NotificationObserver instance;
    private final NotificationDAO notificationDAO;

    private NotificationObserver() {
        this.notificationDAO = DAOFactory.getNotificationDAO();
    }

    public static NotificationObserver getInstance() {
        if (instance == null) {
            instance = new NotificationObserver();
        }
        return instance;
    }

    @Override
    public void update(Appointment appointment) throws DAOException {
        AppointmentStatus status = appointment.getStatus();

        if (status == AppointmentStatus.PENDING) {
            notifySecretaries(appointment);
        } else if (status == AppointmentStatus.REJECTED) {
            notifyPatientRejection(appointment);
        } else if (status == AppointmentStatus.CONFIRMED) {
            notifyPatientConfirmation(appointment);
        } else if (status == AppointmentStatus.RESCHEDULING) {
            notifyPatientRescheduling(appointment);
        }
    }

    private void notifySecretaries(Appointment appointment) throws DAOException {
        String message = "Nuova richiesta di prenotazione dal paziente " +
                appointment.getPatient().getName() + " " +
                appointment.getPatient().getSurname() +
                " per il servizio " + appointment.getService().getName();

        Notification notification = new Notification(
                appointment,
                message,
                Role.SECRETARY  // ← Per tutte le segretarie
        );

        notificationDAO.save(notification);
    }

    private void notifyPatientRejection(Appointment appointment) throws DAOException {
        String message = "La tua prenotazione è stata rifiutata. Motivo: " +
                appointment.getRejectionReason();

        Notification notification = new Notification(
                appointment,
                message,
                Role.PATIENT  // ← Per il paziente
        );

        notificationDAO.save(notification);
    }

    private void notifyPatientConfirmation(Appointment appointment) throws DAOException {
        String message = "Prenotazione confermata per il " +
                appointment.getRequestedDate() +
                " alle " + appointment.getConfirmedTime();

        Notification notification = new Notification(
                appointment,
                message,
                Role.PATIENT
        );

        notificationDAO.save(notification);
    }

    private void notifyPatientRescheduling(Appointment appointment) throws DAOException {
        String message = "C'è una prenotazione da riprogrammare per il servizio " +
                appointment.getService().getName();

        Notification notification = new Notification(
                appointment,
                message,
                Role.PATIENT
        );

        notificationDAO.save(notification);
    }
}
