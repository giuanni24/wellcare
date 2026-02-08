package maindir.controller;

import maindir.bean.*;
import maindir.exceptions.ExpiredSlotException;
import maindir.model.*;
import maindir.model.enums.AppointmentStatus;
import maindir.model.enums.Role;
import maindir.persistance.DAOFactory;
import maindir.persistance.dao.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;
class ExpiredSlotTest {

    private PatientProfileController controller;

    @BeforeAll
    static void setupDAODemo(){
        DAOFactory.setMode(DAOFactory.PersistenceMode.MEMORY);
    }

    @BeforeEach
    void setUp() {
        controller = new PatientProfileController();
    }

    @Test
    void testConfirmRescheduling_ExpiredSlot_ThrowsException() throws Exception {
        // 1. Crea oggetti direttamente
        Doctor doctor = new Doctor(99L, Role.DOCTOR, "doctor@test.com", "FC", "Dr", "Test");
        User patient = new User(98L, Role.PATIENT, "patient@test.com", "FC", "Patient", "Test");
        Service service = new Service(1L, "Test Service", 100.0);

        // 2. Crea un DoctorUnavailability con data di creazione prima di ieri (scaduto)
        LocalDateTime creationDate = LocalDateTime.now().minusDays(2); // Prima di ieri
        DoctorUnavailability unavailability = new DoctorUnavailability(1000L,
                doctor,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                creationDate
        );
        DoctorUnavailabilityDAO unavailabilityDAO = DAOFactory.getDoctorUnavailabilityDAO();
        assert unavailabilityDAO != null;
        unavailabilityDAO.save(unavailability);

        // 3. Crea un ReservedSlot associato a questo periodo scaduto
        ReservedSlot expiredSlot = new ReservedSlot(null,
                doctor,
                LocalDate.now().plusDays(2),
                LocalTime.of(10, 0),
                unavailability,
                false
        );
        ReservedSlotDAO reservedSlotDAO = DAOFactory.getReservedSlotDAO();
        assert reservedSlotDAO != null;
        reservedSlotDAO.save(expiredSlot);

        // 4. Crea un Appointment e SlotReservation
        Appointment appointment = new Appointment(patient, doctor, service, LocalDate.now());
        appointment.setStatus(AppointmentStatus.RESCHEDULING);
        AppointmentDAO appointmentDAO = DAOFactory.getAppointmentDAO();
        appointmentDAO.save(appointment);

        SlotReservation reservation = new SlotReservation(1000L,expiredSlot, appointment);
        SlotReservationDAO slotReservationDAO = DAOFactory.getSlotReservationDAO();
        assert slotReservationDAO!=null;
        slotReservationDAO.save(reservation);

        // 5. Tenta di confermare il rescheduling con lo slot scaduto
        AppointmentBean appointmentBean = new AppointmentBean();
        appointmentBean.setId(appointment.getId());
        appointmentBean.setRequestedDate(LocalDate.now().plusDays(2));
        appointmentBean.setConfirmedTime(LocalTime.of(10, 0));

        boolean expiredExceptionThrown = false;
        try {
            controller.confirmRescheduling(appointmentBean);
        } catch (ExpiredSlotException e) {
            expiredExceptionThrown = true;
        }

        assertTrue(expiredExceptionThrown);
    }
}
