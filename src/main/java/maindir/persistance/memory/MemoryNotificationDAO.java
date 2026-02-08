package maindir.persistance.memory;

import maindir.model.*;
import maindir.model.enums.Role;
import maindir.persistance.dao.NotificationDAO;

import java.util.ArrayList;
import java.util.List;

public class MemoryNotificationDAO implements NotificationDAO {
    private static MemoryNotificationDAO instance;
    private List<Notification> cache;
    private boolean cacheLoaded;
    private long idCounter;

    private MemoryNotificationDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
        this.idCounter = 0L;
    }

    public static synchronized MemoryNotificationDAO getInstance() {
        if (instance == null) {
            instance = new MemoryNotificationDAO();
        }
        return instance;
    }

    @Override
    public List<Notification> getAll() {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Notification notification) {
        if (!cacheLoaded) {
            loadCache();
        }

        Long newId = ++idCounter;
        notification.setId(newId);
        cache.add(notification);
    }

    @Override
    public void update(Notification notification) {
        if (!cacheLoaded) {
            loadCache();
        }
        updateInCache(notification);
    }

    private void loadCache() {
        cache.clear();
        loadInitialNotifications();
        cacheLoaded = true;
    }

    private void loadInitialNotifications() {
        MemoryAppointmentDAO appointmentDAO = MemoryAppointmentDAO.getInstance();
        List<Appointment> appointments = appointmentDAO.getAll();

        if (appointments.isEmpty()) {
            return;
        }

        // Notifica 1: Per il paziente - appuntamento pending
            Appointment apt = appointments.get(0);
            addNotification(apt,
                    "La tua richiesta di appuntamento è in attesa di conferma",
                    Role.PATIENT, false);

        // Notifica 2: Per la segretaria - nuovo appuntamento
        if (!appointments.isEmpty()) {
             apt = appointments.get(0);
            addNotification(apt,
                    "Nuova richiesta di appuntamento da confermare",
                    Role.SECRETARY, false);
        }

        // Notifica 3: Per il paziente - appuntamento confermato
        if (appointments.size() > 1) {
             apt = appointments.get(1);
            addNotification(apt,
                    "Il tuo appuntamento è stato confermato per il " + apt.getRequestedDate(),
                    Role.PATIENT, true);
        }

        // Notifica 4: Per il dottore - paziente arrivato
        if (appointments.size() > 3) {
             apt = appointments.get(3);
            addNotification(apt,
                    "Il paziente è arrivato per l'appuntamento",
                    Role.DOCTOR, false);
        }

        // Notifica 5: Per il paziente - promemoria
        if (appointments.size() > 1) {
             apt = appointments.get(1);
            addNotification(apt,
                    "Promemoria: hai un appuntamento domani alle " + apt.getConfirmedTime(),
                    Role.PATIENT, false);
        }
    }

    private void addNotification(Appointment appointment, String message, Role targetRole, boolean isRead) {
        Long newId = ++idCounter;
        Notification notification = new Notification(newId, appointment, message, targetRole, isRead);
        cache.add(notification);
    }

    private void updateInCache(Notification notification) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(notification.getId())) {
                cache.set(i, notification);
                return;
            }
        }
    }
}
