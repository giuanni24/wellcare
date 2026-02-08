package maindir.persistance.memory;

import maindir.exceptions.DuplicateUserException;
import maindir.model.*;
import maindir.model.enums.Role;
import maindir.persistance.dao.UserDAO;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class MemoryUserDAO implements UserDAO {
    private static MemoryUserDAO instance;
    private List<User> cache;
    private boolean cacheLoaded;

    private MemoryUserDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized MemoryUserDAO getInstance() {
        if (instance == null) {
            instance = new MemoryUserDAO();
        }
        return instance;
    }

    @Override
    public List<User> getAll() {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(User user) throws DuplicateUserException {
        if (!cacheLoaded) {
            loadCache();
        }

        // Controlla se email già esiste
        for (User existingUser : cache) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                throw new DuplicateUserException("Email già registrata");
            }
        }

        Long newId = cache.stream()
                .mapToLong(User::getId)
                .max()
                .orElse(0L) + 1;
        user.setId(newId);
        cache.add(user);
    }


    @Override
    public void update(User user) {
        if (!cacheLoaded) {
            loadCache();
        }
        updateInCache(user);
    }

    @Override
    public User authenticate(User user) {
        if (!cacheLoaded) {
            loadCache();
        }

        for (User u : cache) {
            if (u.getEmail().equals(user.getEmail()) &&
                    u.getPassword().equals(user.getPassword())) {
                return mapUser(u);
            }
        }
        return null;
    }

    private void loadCache() {
        cache.clear();
        loadInitialData();
        cacheLoaded = true;
    }

    private void loadInitialData() {
        MemoryServiceDAO serviceDAO = MemoryServiceDAO.getInstance();
        List<Service> allServices = serviceDAO.getAll();

        Service cardiologia = findServiceByName(allServices, "Visita Cardiologica");
        Service dermatologia = findServiceByName(allServices, "Visita Dermatologica");
        Service ortopedia = findServiceByName(allServices, "Visita Ortopedica");
        Service oculistica = findServiceByName(allServices, "Visita Oculistica");
        Service pediatria = findServiceByName(allServices, "Visita Pediatrica");

        // Dottore 1: Dr. Mario Rossi
        Doctor drRossi = new Doctor(
                1L,
                Role.DOCTOR,
                "mario.rossi@hospital.com",
                "RSSMRA80A01H501Z",
                "Mario",
                "Rossi"
        );
        drRossi.setPassword("password123");
        drRossi.setServices(new ArrayList<>(List.of(cardiologia, ortopedia)));
        drRossi.setAvailableDays(new ArrayList<>(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)));
        cache.add(drRossi);

        // Dottore 2: Dr.ssa Giulia Bianchi
        Doctor drBianchi = new Doctor(
                2L,
                Role.DOCTOR,
                "giulia.bianchi@hospital.com",
                "BNCGLI85B15F205X",
                "Giulia",
                "Bianchi"
        );
        drBianchi.setPassword("password123");
        drBianchi.setServices(new ArrayList<>(List.of(dermatologia, oculistica)));
        drBianchi.setAvailableDays(new ArrayList<>(List.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY)));
        cache.add(drBianchi);

        // Dottore 3: Dr. Luca Verdi
        Doctor drVerdi = new Doctor(
                3L,
                Role.DOCTOR,
                "luca.verdi@hospital.com",
                "VRDLCU90C20L219Y",
                "Luca",
                "Verdi"
        );
        drVerdi.setPassword("password123");
        drVerdi.setServices(new ArrayList<>(List.of(pediatria, cardiologia)));
        drVerdi.setAvailableDays(new ArrayList<>(List.of(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        )));
        cache.add(drVerdi);

        // Paziente
        User paziente = new User(
                4L,
                Role.PATIENT,
                "paziente@email.com",
                "PZNMRC95D15H501W",
                "Marco",
                "Paziente"
        );
        paziente.setPassword("password123");
        cache.add(paziente);

        // Segretaria
        User segretaria = new User(
                5L,
                Role.SECRETARY,
                "segretaria@hospital.com",
                "SGRLRA88E50H501K",
                "Laura",
                "Segretari"
        );
        segretaria.setPassword("password123");
        cache.add(segretaria);
    }

    private User mapUser(User user) {
        Long id = user.getId();
        Role role = user.getRole();
        String email = user.getEmail();
        String fiscalCode = user.getFiscalCode();
        String name = user.getName();
        String surname = user.getSurname();

        if (Role.DOCTOR.equals(role) && user instanceof Doctor doctor) {
            Doctor newDoctor = new Doctor(id, role, email, fiscalCode, name, surname);
            newDoctor.setServices(new ArrayList<>(doctor.getServices()));
            newDoctor.setAvailableDays(new ArrayList<>(doctor.getAvailableDays()));
            return newDoctor;
        } else {
            return new User(id, role, email, fiscalCode, name, surname);
        }
    }

    private void updateInCache(User user) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(user.getId())) {
                cache.set(i, user);
                return;
            }
        }
    }

    private Service findServiceByName(List<Service> services, String name) {
        return services.stream()
                .filter(s -> s.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
