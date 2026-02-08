package maindir.controller;

import maindir.bean.*;
import maindir.exceptions.ControllerException;
import maindir.exceptions.DAOException;
import maindir.exceptions.ExpiredSlotException;
import maindir.model.*;
import maindir.model.enums.AppointmentStatus;
import maindir.persistance.dao.AppointmentDAO;
import maindir.persistance.DAOFactory;
import maindir.persistance.dao.ReservedSlotDAO;
import maindir.persistance.dao.SlotReservationDAO;
import maindir.utility.BeanModelConverter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PatientProfileController {

    public List<AppointmentBean> getActiveAppointments(UserBean patientBean) throws ControllerException {
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            assert appointmentDAO != null;

            User patient = BeanModelConverter.userToModel(patientBean);
            List<Appointment> allAppointments = appointmentDAO.getAll();
            List<AppointmentBean> activeAppointments = new ArrayList<>();

            for (Appointment app : allAppointments) {
                if (app.getPatient().getId().equals(patient.getId()) &&
                        app.getStatus() != AppointmentStatus.CANCELED &&
                        app.getStatus() != AppointmentStatus.REJECTED) {
                    activeAppointments.add(BeanModelConverter.appointmentToBean(app));
                }
            }
            return activeAppointments;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public List<AppointmentBean> getCancellableAppointments(UserBean patientBean) throws ControllerException {
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            assert appointmentDAO != null;

            User patient = BeanModelConverter.userToModel(patientBean);
            List<Appointment> allAppointments = appointmentDAO.getAll();
            List<AppointmentBean> cancellableAppointments = new ArrayList<>();

            for (Appointment app : allAppointments) {
                if (app.getPatient().getId().equals(patient.getId()) &&
                        app.getStatus() != AppointmentStatus.CANCELED &&
                        app.getStatus() != AppointmentStatus.REJECTED &&
                        app.getStatus() != AppointmentStatus.COMPLETED &&
                        app.getStatus() != AppointmentStatus.ARRIVED) {
                    cancellableAppointments.add(BeanModelConverter.appointmentToBean(app));
                }
            }
            return cancellableAppointments;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public List<AppointmentBean> getCompletedAppointments(UserBean patientBean) throws ControllerException {
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            assert appointmentDAO != null;

            User patient = BeanModelConverter.userToModel(patientBean);
            List<Appointment> allAppointments = appointmentDAO.getAll();
            List<AppointmentBean> completedAppointments = new ArrayList<>();

            for (Appointment app : allAppointments) {
                if (app.getPatient().getId().equals(patient.getId()) &&
                        app.getStatus() == AppointmentStatus.COMPLETED) {
                    completedAppointments.add(BeanModelConverter.appointmentToBean(app));
                }
            }

            if (completedAppointments.isEmpty()) {
                throw new ControllerException();
            }
            return completedAppointments;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public List<AppointmentBean> getReschedulableAppointments(UserBean patientBean) throws ControllerException {
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            assert appointmentDAO != null;

            User patient = BeanModelConverter.userToModel(patientBean);
            List<Appointment> allAppointments = appointmentDAO.getAll();
            List<AppointmentBean> reschedulableAppointments = new ArrayList<>();

            for (Appointment app : allAppointments) {
                if (app.getPatient().getId().equals(patient.getId()) &&
                        app.getStatus() == AppointmentStatus.RESCHEDULING) {
                    reschedulableAppointments.add(BeanModelConverter.appointmentToBean(app));
                }
            }

            if (reschedulableAppointments.isEmpty()) {
                throw new ControllerException("Nessun appuntamento da riprogrammare trovato.");
            }
            return reschedulableAppointments;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public boolean cancelAppointment(AppointmentBean appointmentBean) throws ControllerException {
        try {
            updateAppointmentToCanceled(appointmentBean);
            return deleteRelatedReservation(appointmentBean.getId());
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private void updateAppointmentToCanceled(AppointmentBean appointmentBean) throws DAOException {
        AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
        assert appointmentDAO != null;

        Appointment appointment = BeanModelConverter.appointmentToModel(appointmentBean);
        appointment.setCanceled();
        appointmentDAO.update(appointment);
    }

    private boolean deleteRelatedReservation(Long appointmentId) throws DAOException {
        SlotReservationDAO slotReservationDAO = DAOFactory.getSlotReservationDAO();
        ReservedSlotDAO reservedSlotDAO = DAOFactory.getReservedSlotDAO();
        assert slotReservationDAO != null && reservedSlotDAO != null;

        List<SlotReservation> allReservations = slotReservationDAO.getAll();
        for (SlotReservation reservation : allReservations) {
            if (reservation.getAppointment().getId().equals(appointmentId)) {
                reservedSlotDAO.delete(reservation.getSlot());
                return true;
            }
        }
        return false;
    }

    public List<DateBean> getAvailableSlots(AppointmentBean appointmentBean) throws ControllerException {
        try {
            SlotReservationDAO slotReservationDAO = DAOFactory.getSlotReservationDAO();
            assert slotReservationDAO != null;

            List<DateBean> availableSlotTimes = new ArrayList<>();
            List<SlotReservation> slotReservations = slotReservationDAO.getAll();

            for (SlotReservation reservation : slotReservations) {
                if (isReservationForAppointment(reservation, appointmentBean.getId())) {
                    addSlotIfAvailable(reservation.getSlot(), availableSlotTimes);
                }
            }

            if (availableSlotTimes.isEmpty()) {
                throw new ControllerException("Nessuno slot disponibile per la riprogrammazione.");
            }
            availableSlotTimes.sort(java.util.Comparator.comparing(DateBean::getDateTime));
            return availableSlotTimes;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private boolean isReservationForAppointment(SlotReservation reservation, Long appointmentId) {
        return reservation.getAppointment().getId().equals(appointmentId);
    }

    private void addSlotIfAvailable(ReservedSlot slot, List<DateBean> availableSlots) {
        if (!slot.isBooked()) {
            LocalDateTime slotDateTime = LocalDateTime.of(slot.getDate(), slot.getTime());
            DateBean bean = new DateBean();
            bean.setDateTime(slotDateTime);
            availableSlots.add(bean);
        }
    }

    public boolean confirmRescheduling(AppointmentBean appointmentBean) throws ControllerException, ExpiredSlotException {
        try {
            validateReschedulingRequest(appointmentBean);
            ReservedSlot selectedSlot = findAndValidateSlot(appointmentBean);
            updateAppointmentAndSlot(appointmentBean, selectedSlot);
            return true;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private void validateReschedulingRequest(AppointmentBean appointmentBean) throws ControllerException {
        if (appointmentBean.getRequestedDate() == null || appointmentBean.getConfirmedTime() == null) {
            throw new ControllerException("Data e ora non impostate per la riprogrammazione.");
        }
    }

    private ReservedSlot findAndValidateSlot(AppointmentBean appointmentBean) throws ControllerException, ExpiredSlotException, DAOException {
        SlotReservationDAO slotReservationDAO = DAOFactory.getSlotReservationDAO();
        assert slotReservationDAO != null;

        List<SlotReservation> slotReservations = slotReservationDAO.getAll();

        for (SlotReservation reservation : slotReservations) {
            if (!isReservationForAppointment(reservation, appointmentBean.getId())) continue;

            ReservedSlot slot = reservation.getSlot();
            if (isSlotMatching(slot, appointmentBean)) {
                validateSlotNotExpired(slot);
                return slot;
            }
        }
        throw new ControllerException("Slot selezionato non trovato o non più disponibile.");
    }

    private boolean isSlotMatching(ReservedSlot slot, AppointmentBean appointmentBean) {
        return !slot.isBooked() &&
                slot.getDate().equals(appointmentBean.getRequestedDate()) &&
                slot.getTime().equals(appointmentBean.getConfirmedTime());
    }

    private void validateSlotNotExpired(ReservedSlot slot) throws ExpiredSlotException {
        if (slot.getUnavailabilityPeriod().isExpired()) {
            throw new ExpiredSlotException("Sono passate 24 ore dalla richiesta di riprogrammazione, la riserva dello slot è scaduta");
        }
    }

    private void updateAppointmentAndSlot(AppointmentBean appointmentBean, ReservedSlot selectedSlot) throws ControllerException, DAOException {
        AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
        ReservedSlotDAO reservedSlotDAO = DAOFactory.getReservedSlotDAO();
        assert appointmentDAO != null && reservedSlotDAO != null;

        Appointment appointment = findAppointmentById(appointmentDAO, appointmentBean.getId());
        appointment.setRequestedDate(appointmentBean.getRequestedDate());
        appointment.setStatusConfirmed(appointmentBean.getConfirmedTime());
        appointmentDAO.update(appointment);

        selectedSlot.markAsBooked();
        reservedSlotDAO.update(selectedSlot);
    }

    private Appointment findAppointmentById(AppointmentDAO appointmentDAO, Long appointmentId) throws ControllerException, DAOException {
        List<Appointment> appointments = appointmentDAO.getAll();
        for (Appointment a : appointments) {
            if (a.getId().equals(appointmentId)) {
                return a;
            }
        }
        throw new ControllerException("Appuntamento non trovato.");
    }
}
