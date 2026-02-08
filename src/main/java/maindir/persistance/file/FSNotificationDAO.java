package maindir.persistance.file;

import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.model.enums.Role;
import maindir.persistance.dao.NotificationDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FSNotificationDAO implements NotificationDAO {
    private static FSNotificationDAO instance;
    private List<Notification> cache;
    private boolean cacheLoaded;
    private boolean fileChecked;
    private static final String FILE_PATH = "data/notifications.dat";

    private FSNotificationDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
        this.fileChecked = false;
    }

    public static synchronized FSNotificationDAO getInstance() {
        if (instance == null) {
            instance = new FSNotificationDAO();
        }
        return instance;
    }

    private void ensureFileExists() throws DAOException {
        if (fileChecked) {
            return;
        }

        File file = new File(FILE_PATH);
        file.getParentFile().mkdirs();
        if (!file.exists()) {
            saveToFile(new ArrayList<>());
        }
        fileChecked = true;
    }

    @Override
    public List<Notification> getAll() throws DAOException {
        ensureFileExists();
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Notification notification) throws DAOException {
        ensureFileExists();
        List<Notification> allNotifications = loadFromFile();

        Long newId = allNotifications.stream()
                .mapToLong(Notification::getId)
                .max()
                .orElse(0L) + 1;
        notification.setId(newId);

        allNotifications.add(notification);
        saveToFile(allNotifications);
        cache.add(notification);
    }

    @Override
    public void update(Notification notification) throws DAOException {
        ensureFileExists();
        List<Notification> allNotifications = loadFromFile();
        for (int i = 0; i < allNotifications.size(); i++) {
            if (allNotifications.get(i).getId().equals(notification.getId())) {
                allNotifications.set(i, notification);
                break;
            }
        }
        saveToFile(allNotifications);
        updateInCache(notification);
    }

    private void loadCache() {
        cache.clear();
        List<Notification> allNotifications = loadFromFile();
        for (Notification notif : allNotifications) {
            Long id = notif.getId();
            Appointment appointment = notif.getAppointment();
            String message = notif.getMessage();
            Role targetRole = notif.getTargetRole();
            boolean isRead = notif.isRead();

            Notification notification = new Notification(id, appointment, message, targetRole, isRead);
            cache.add(notification);
        }
        cacheLoaded = true;
    }

    @SuppressWarnings("unchecked")
    private List<Notification> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Notification>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveToFile(List<Notification> notifications) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(notifications);
        } catch (IOException e) {
            throw new DAOException(e.getMessage(), e);
        }
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
