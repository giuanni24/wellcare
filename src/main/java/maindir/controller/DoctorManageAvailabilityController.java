package maindir.controller;

import maindir.bean.UnavailabilityBean;
import maindir.exceptions.ControllerException;
import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.model.enums.AppointmentStatus;
import maindir.model.enums.Role;
import maindir.persistance.DAOFactory;
import maindir.persistance.dao.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DoctorManageAvailabilityController {



    public int countAffectedAppointments(UnavailabilityBean bean) throws ControllerException {
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            UserDAO userDAO = DAOFactory.getUserDAO();
            assert appointmentDAO != null;
            assert userDAO != null;

            Doctor doctor = null;
            List<User> users = userDAO.getAll();
            for (User u : users) {
                if (u.getRole().equals(Role.DOCTOR) && u.getId().equals(bean.getDoctor().getId())) {
                    doctor = (Doctor) u;
                }
            }
            if (doctor == null) {
                throw new ControllerException("Doctor not found with id: " + bean.getDoctor().getId());
            }

            List<Appointment> allAppointments = appointmentDAO.getAll();

            int count = 0;
            for (Appointment apt : allAppointments) {
                if (apt.getDoctor().getId().equals(doctor.getId()) &&
                        (apt.isPending() || apt.isConfirmed()) &&
                        !apt.getRequestedDate().isBefore(bean.getStartDate()) &&
                        !apt.getRequestedDate().isAfter(bean.getEndDate())) {
                    count++;
                }
            }

            return count;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public boolean blockUnavailabilityPeriod(UnavailabilityBean bean) throws ControllerException {
        DoctorUnavailabilityDAO unavailabilityDAO = DAOFactory.getDoctorUnavailabilityDAO();
        AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
        SlotReservationDAO slotReservationDAO = DAOFactory.getSlotReservationDAO();
        ReservedSlotDAO reservedSlotDAO = DAOFactory.getReservedSlotDAO();
        UserDAO userDAO = DAOFactory.getUserDAO();

        assert unavailabilityDAO != null;
        assert appointmentDAO != null;
        assert slotReservationDAO != null;
        assert reservedSlotDAO != null;
        assert userDAO != null;

        try {
            // Recupera il Doctor completo PRIMA del try o DENTRO ma UNA SOLA VOLTA
            Doctor doctor = null;
            List<User> users = userDAO.getAll();
            for (User u : users) {
                if (u.getRole().equals(Role.DOCTOR) && u.getId().equals(bean.getDoctor().getId())) {
                    doctor = (Doctor) u;
                    break;
                }
            }

            if (doctor == null) {
                throw new ControllerException("Doctor not found");
            }

            // 1. Crea il periodo di indisponibilità
            DoctorUnavailability unavailability = new DoctorUnavailability(
                    null,
                    doctor,
                    bean.getStartDate(),
                    bean.getEndDate(),
                    LocalDateTime.now()
            );
            unavailabilityDAO.save(unavailability);

            // 2. Trova e metti in RESCHEDULING tutti gli appuntamenti intaccati
            List<Appointment> affectedAppointments = findAffectedAppointments(doctor, bean.getStartDate(), bean.getEndDate());
            List<Appointment> toReschedule = new ArrayList<>();
            for (Appointment apt : affectedAppointments) {
                if (apt.isPending())
                    apt.setStatusRejected("Il dottore non è più disponibile in quella data");
                else {
                    toReschedule.add(apt);
                    apt.setStatusRescheduling();
                }
                appointmentDAO.update(apt);
            }

            // 3. Crea slot riservati per la riprogrammazione
            int numSlotsNeeded = toReschedule.size();
            List<ReservedSlot> reservedSlots = createReservedSlots(doctor, bean.getEndDate().plusDays(1), numSlotsNeeded, unavailability);

            // 4. Collega ogni appuntamento a tutti gli slot riservati (molti-a-molti)
            for (Appointment apt : toReschedule) {
                for (ReservedSlot slot : reservedSlots) {
                    SlotReservation reservation = new SlotReservation(null, slot, apt);
                    slotReservationDAO.save(reservation);
                }
            }

            return true;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private List<Appointment> findAffectedAppointments(Doctor doctor, LocalDate startDate, LocalDate endDate) throws ControllerException {
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            List<Appointment> allAppointments = appointmentDAO.getAll();
            List<Appointment> affected = new ArrayList<>();

            for (Appointment apt : allAppointments) {
                if (apt.getDoctor().getId().equals(doctor.getId()) &&
                        (apt.isConfirmed() || apt.isPending()) &&
                        !apt.getRequestedDate().isBefore(startDate) &&
                        !apt.getRequestedDate().isAfter(endDate)) {
                    affected.add(apt);
                }
            }

            return affected;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private List<ReservedSlot> createReservedSlots(Doctor doctor, LocalDate startDate, int numberOfSlots, DoctorUnavailability unavailability) throws ControllerException {
        List<ReservedSlot> reservedSlots = new ArrayList<>();
        LocalDate currentDate = startDate;
        int created = 0;
        int maxDaysToSearch = 30;
        int daysSearched = 0;

        while (created < numberOfSlots && daysSearched < maxDaysToSearch) {
            if (canSearchSlotsOnDate(doctor, currentDate)) {
                created += reserveSlotsForDate(doctor, currentDate, numberOfSlots - created, unavailability, reservedSlots);
            }
            currentDate = currentDate.plusDays(1);
            daysSearched++;
        }

        return reservedSlots;
    }

    private boolean canSearchSlotsOnDate(Doctor doctor, LocalDate date) throws ControllerException {
        return doctor.getAvailableDays().contains(date.getDayOfWeek()) &&
                !isDoctorUnavailableOnDate(doctor, date);
    }

    private int reserveSlotsForDate(Doctor doctor, LocalDate date, int slotsNeeded, DoctorUnavailability unavailability, List<ReservedSlot> reservedSlots) throws ControllerException {
        try {
            ReservedSlotDAO reservedSlotDAO = DAOFactory.getReservedSlotDAO();
            assert reservedSlotDAO != null;
            List<LocalTime> timeSlots = generateTimeSlots();
            int created = 0;

            for (LocalTime time : timeSlots) {
                if (created >= slotsNeeded) break;

                if (isSlotFree(doctor, date, time)) {
                    ReservedSlot slot = new ReservedSlot(null, doctor, date, time, unavailability, false);
                    reservedSlotDAO.save(slot);
                    reservedSlots.add(slot);
                    created++;
                }
            }

            return created;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private boolean isDoctorUnavailableOnDate(Doctor doctor, LocalDate date) throws ControllerException {
            DoctorUnavailabilityDAO unavailabilityDAO = DAOFactory.getDoctorUnavailabilityDAO();
            assert unavailabilityDAO != null;
            try{
            List<DoctorUnavailability> unavailabilities = unavailabilityDAO.getAll();

            for (DoctorUnavailability unavailability : unavailabilities) {
                if (unavailability.getDoctor().getId().equals(doctor.getId()) &&
                        !date.isBefore(unavailability.getStartDate()) &&
                        !date.isAfter(unavailability.getEndDate())) {
                    return true;
                }
            }}catch (DAOException e){
                throw new ControllerException(e.getMessage(), e);
            }

            return false;
    }

    private boolean isSlotFree(Doctor doctor, LocalDate date, LocalTime time) throws ControllerException {
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            assert appointmentDAO != null;
            List<Appointment> appointments = appointmentDAO.getAll();

            for (Appointment apt : appointments) {
                if (apt.getDoctor().getId().equals(doctor.getId()) &&
                        apt.getRequestedDate().equals(date) &&
                        apt.getConfirmedTime() != null &&
                        apt.getConfirmedTime().equals(time) &&
                        apt.getStatus() != AppointmentStatus.CANCELED &&
                        apt.getStatus() != AppointmentStatus.RESCHEDULING) {
                    return false;
                }
            }

            return true;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private List<LocalTime> generateTimeSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(18, 0);

        while (start.isBefore(end)) {
            slots.add(start);
            start = start.plusMinutes(30);
        }

        return slots;
    }


}
