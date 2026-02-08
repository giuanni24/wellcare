package maindir.controller;

import javafx.scene.control.Alert;
import maindir.bean.*;
import maindir.exceptions.ControllerException;
import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.model.enums.AppointmentStatus;
import maindir.model.enums.InvoiceStatus;
import maindir.persistance.DAOFactory;
import maindir.persistance.dao.*;
import maindir.utility.BeanModelConverter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookingRequestController {
    public List<ServiceBean> retrieveAllServices() throws ControllerException {
        try {
            ServiceDAO serviceDAO = DAOFactory.getServiceDAO();
            List<Service> services = serviceDAO.getAll();

            List<ServiceBean> beans = new ArrayList<>();
            for (Service service : services) {
                beans.add(BeanModelConverter.serviceToBean(service));
            }
            return beans;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public List<DoctorBean> findDoctorByService(ServiceBean askedService) throws ControllerException {
        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            if (userDAO == null) {
                return Collections.emptyList();
            }
            List<Doctor> doctors = userDAO.getAll().stream()
                    .filter(Doctor.class::isInstance)
                    .map(Doctor.class::cast)
                    .toList();
            List<DoctorBean> beans = new ArrayList<>();

            for (Doctor doctor : doctors) {
                for (Service service : doctor.getServices()) {
                    if (service.getId().equals(askedService.getId())) {
                        beans.add(BeanModelConverter.doctorToBean(doctor));
                        break;
                    }
                }
            }

            return beans;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public List<AppointmentBean> getPendingRequests() throws ControllerException {
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            List<Appointment> allAppointments = appointmentDAO.getAll();

            List<AppointmentBean> beans = new ArrayList<>();

            for (Appointment appointment : allAppointments) {
                if (appointment.getStatus() == AppointmentStatus.PENDING) {
                    beans.add(BeanModelConverter.appointmentToBean(appointment));
                }
            }

            return beans;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public List<InvoiceBean> getUnpaidInvoices(UserBean patientBean) throws ControllerException {
        try {
            InvoiceDAO invoiceDAO = DAOFactory.getInvoiceDAO();
            List<Invoice> allInvoices = invoiceDAO.getAll();

            List<InvoiceBean> unpaidBeans = new ArrayList<>();

            for (Invoice invoice : allInvoices) {
                // Accedi al paziente attraverso l'appointment
                if (invoice.getAppointment().getPatient().getId().equals(patientBean.getId()) &&
                        invoice.getPaymentStatus() == InvoiceStatus.UNPAID) {
                    unpaidBeans.add(BeanModelConverter.invoiceToBean(invoice));
                }
            }

            return unpaidBeans;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public boolean acceptRequest(AppointmentBean appointmentBean) throws ControllerException {
        try {
            Appointment appointment = findAppointmentById(appointmentBean.getId());
            Invoice invoice = new Invoice(BeanModelConverter.appointmentToModel(appointmentBean), BeanModelConverter.appointmentToModel(appointmentBean).getService().getBasePrice());
            if (appointment == null) {
                return false;
            }

            appointment.setStatusConfirmed(appointmentBean.getConfirmedTime());

            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            appointmentDAO.update(appointment);
            InvoiceDAO invoiceDAO = DAOFactory.getInvoiceDAO();
            invoiceDAO.save(invoice);

            return true;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public boolean rejectRequest(AppointmentBean appointmentBean) throws ControllerException {
        try {
            Appointment appointment = findAppointmentById(appointmentBean.getId());

            if (appointment == null) {
                return false;
            }

            appointment.setStatusRejected(appointmentBean.getRejectionReason());

            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            appointmentDAO.update(appointment);

            return true;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }



    private Appointment findAppointmentById(Long appointmentId) throws ControllerException {
        try {
            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            List<Appointment> appointments = appointmentDAO.getAll();

            for (Appointment a : appointments) {
                if (a.getId().equals(appointmentId)) {
                    return a;
                }
            }

            return null;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public boolean isDoctorAvailableOnDate(AppointmentBean bean) {
        DayOfWeek dayOfWeek = bean.getRequestedDate().getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;
        }

        List<DayOfWeek> availableDays = bean.getDoctor().getAvailableDays();
        if (availableDays == null || availableDays.isEmpty()) {
            return false;
        }

        if (!availableDays.contains(dayOfWeek)) {
            return false;
        }
        try {
            // 3. NUOVO: Controlla se il dottore è in ferie quel giorno
            DoctorUnavailabilityDAO unavailabilityDAO = DAOFactory.getDoctorUnavailabilityDAO();
            assert unavailabilityDAO != null;
            List<DoctorUnavailability> unavailabilities = unavailabilityDAO.getAll();

            for (DoctorUnavailability unavailability : unavailabilities) {
                if (unavailability.getDoctor().getId().equals(bean.getDoctor().getId()) &&
                        !bean.getRequestedDate().isBefore(unavailability.getStartDate()) &&
                        !bean.getRequestedDate().isAfter(unavailability.getEndDate())) {
                    return false;
                }
            }
        }catch (DAOException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(e.getMessage());
            alert.show();
        }
        return true;
    }

    public void createBookingRequest(AppointmentBean bean) throws ControllerException {
        try {
            User patient = BeanModelConverter.userToModel(bean.getPatient());

            Doctor doctor = BeanModelConverter.doctorToModel(bean.getDoctor());

            Service service = BeanModelConverter.serviceToModel(bean.getService());

            Appointment appointment = new Appointment(
                    patient,
                    doctor,
                    service,
                    bean.getRequestedDate()
            );

            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            assert appointmentDAO != null;
            appointment = appointmentDAO.save(appointment);
            appointment.notifyExternal();
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    public List<LocalTime> getAvailableSlots(AppointmentBean requestBean) throws ControllerException {
        try {
            Long doctorId = requestBean.getDoctor().getId();
            LocalDate date = requestBean.getRequestedDate();

            List<LocalTime> allSlots = generateDailySlots(date);

            AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
            List<Appointment> allAppointments = appointmentDAO.getAll();

            List<LocalTime> occupiedSlots = new ArrayList<>();
            for (Appointment appointment : allAppointments) {
                if (appointment.getDoctor().getId().equals(doctorId) &&
                        appointment.getRequestedDate() != null &&
                        appointment.getRequestedDate().equals(date) &&
                        appointment.getStatus() == AppointmentStatus.CONFIRMED) {

                    occupiedSlots.add(appointment.getConfirmedTime());  // ← Estrai LocalTime
                }
            }

            allSlots.removeAll(occupiedSlots);

            ReservedSlotDAO reservedSlotDAO = DAOFactory.getReservedSlotDAO();
            assert reservedSlotDAO != null;
            List<ReservedSlot> reservedSlots = reservedSlotDAO.getAll();

            List<LocalTime> reservedTimes = new ArrayList<>();
            for (ReservedSlot slot : reservedSlots) {
                if (slot.getDoctor().getId().equals(doctorId) &&
                        slot.getDate().equals(date) &&
                        !slot.getUnavailabilityPeriod().isExpired()) {  // Solo quelli non ancora prenotati
                    reservedTimes.add(slot.getTime());
                }
            }

            allSlots.removeAll(reservedTimes);
            return allSlots;
        } catch (DAOException e) {
            throw new ControllerException(e.getMessage(), e);
        }
    }

    private List<LocalTime> generateDailySlots(LocalDate date) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(18, 0);
        int slotDuration = 30;

        if(date.equals(LocalDate.now()))
            while(start.isBefore(LocalTime.now()))
                start = start.plusMinutes(slotDuration);

        while (start.isBefore(end)){
            slots.add(start);
            start = start.plusMinutes(slotDuration);
        }

        return slots;
    }


}
