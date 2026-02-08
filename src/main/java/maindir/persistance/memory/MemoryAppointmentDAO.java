package maindir.persistance.memory;

import maindir.model.*;
import maindir.model.enums.AppointmentStatus;
import maindir.observer.NotificationObserver;
import maindir.persistance.dao.AppointmentDAO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class MemoryAppointmentDAO implements AppointmentDAO {
    private static MemoryAppointmentDAO instance;
    private List<Appointment> cache;
    private boolean cacheLoaded;

    private MemoryAppointmentDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized MemoryAppointmentDAO getInstance() {
        if (instance == null) {
            instance = new MemoryAppointmentDAO();
        }
        return instance;
    }

    @Override
    public List<Appointment> getAll() {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public Appointment save(Appointment appointment) {
        if (!cacheLoaded) {
            loadCache();
        }

        Long newId = cache.stream()
                .mapToLong(Appointment::getId)
                .max()
                .orElse(0L) + 1;
        appointment.setId(newId);

        cache.add(appointment);
        return appointment;
    }

    @Override
    public void update(Appointment appointment) {
        if (!cacheLoaded) {
            loadCache();
        }
        updateInCache(appointment);
    }

    private void loadCache() {
        cache.clear();
        loadInitialAppointments();
        cacheLoaded = true;
    }

    private void loadInitialAppointments() {
        MemoryUserDAO userDAO = MemoryUserDAO.getInstance();
        MemoryServiceDAO serviceDAO = MemoryServiceDAO.getInstance();

        List<User> users = null;
        users = userDAO.getAll();
        List<Service> services = serviceDAO.getAll();

        User paziente = findUserByEmail(users, "paziente@email.com");
        Doctor drRossi = (Doctor) findUserByEmail(users, "mario.rossi@hospital.com");
        Doctor drBianchi = (Doctor) findUserByEmail(users, "giulia.bianchi@hospital.com");
        Doctor drVerdi = (Doctor) findUserByEmail(users, "luca.verdi@hospital.com");

        Service cardiologia = findServiceByName(services, "Visita Cardiologica");
        Service dermatologia = findServiceByName(services, "Visita Dermatologica");
        Service pediatria = findServiceByName(services, "Visita Pediatrica");

        // Appuntamento 1: PENDING
        Appointment apt1 = new Appointment(
                1L,
                paziente,
                drRossi,
                cardiologia,
                LocalDate.now().plusDays(5),
                null,
                AppointmentStatus.PENDING
        );
        apt1.attach(NotificationObserver.getInstance());
        cache.add(apt1);

        // Appuntamento 2: CONFIRMED
        Appointment apt2 = new Appointment(
                2L,
                paziente,
                drBianchi,
                dermatologia,
                LocalDate.now().plusDays(7),
                LocalTime.of(10, 30),
                AppointmentStatus.CONFIRMED
        );
        cache.add(apt2);

        // Appuntamento 3: COMPLETED
        Appointment apt3 = new Appointment(
                3L,
                paziente,
                drVerdi,
                pediatria,
                LocalDate.now().minusDays(3),
                LocalTime.of(14, 0),
                AppointmentStatus.COMPLETED
        );
        cache.add(apt3);

        // Appuntamento 4: ARRIVED
        Appointment apt4 = new Appointment(
                4L,
                paziente,
                drRossi,
                cardiologia,
                LocalDate.now(),
                LocalTime.of(9, 0),
                AppointmentStatus.ARRIVED
        );
        apt4.attach(NotificationObserver.getInstance());
        cache.add(apt4);

        // Appuntamento 5: CANCELLED
        Appointment apt5 = new Appointment(
                5L,
                paziente,
                drBianchi,
                dermatologia,
                LocalDate.now().plusDays(10),
                null,
                AppointmentStatus.CANCELED
        );
        cache.add(apt5);
    }

    private void updateInCache(Appointment appointment) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(appointment.getId())) {
                cache.set(i, appointment);
                if (appointment.isPending() || appointment.isConfirmed() || appointment.isRescheduled()) {
                    appointment.attach(NotificationObserver.getInstance());
                }
                return;
            }
        }
    }

    private User findUserByEmail(List<User> users, String email) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    private Service findServiceByName(List<Service> services, String name) {
        return services.stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
