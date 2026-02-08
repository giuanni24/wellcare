package maindir.persistance.file;

import maindir.exceptions.DAOException;
import maindir.exceptions.DuplicateUserException;
import maindir.model.*;
import maindir.model.enums.Role;
import maindir.persistance.dao.UserDAO;

import java.io.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class FSUserDAO implements UserDAO {
    private static FSUserDAO instance;
    private List<User> cache;
    private boolean cacheLoaded;
    private boolean fileChecked;
    private static final String FILE_PATH = "data/users.dat";

    private FSUserDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
        this.fileChecked = false;
    }

    public static synchronized FSUserDAO getInstance() {
        if (instance == null) {
            instance = new FSUserDAO();
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
    public List<User> getAll() throws DAOException {
        ensureFileExists();
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(User user) throws DuplicateUserException, DAOException {
        if (!cacheLoaded) {
            loadCache();
        }

        // Controlla se email già esiste
        for (User existingUser : cache) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                throw new DuplicateUserException("Email già registrata: " + user.getEmail());
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
    public void update(User user) throws DAOException {
        ensureFileExists();
        List<User> allUsers = loadFromFile();
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getId().equals(user.getId())) {
                allUsers.set(i, user);
                break;
            }
        }
        saveToFile(allUsers);
        updateInCache(user);
    }

    @Override
    public User authenticate(User user) throws DAOException {
        ensureFileExists();
        List<User> allUsers = loadFromFile();
        for (User u : allUsers) {
            if (u.getEmail().equals(user.getEmail()) &&
                    u.getPassword().equals(user.getPassword())) {
                return mapUser(u);
            }
        }
        return null;
    }

    private void loadCache() throws DAOException {
        cache.clear();
        List<User> allUsers = loadFromFile();
        for (User user : allUsers) {
            cache.add(mapUser(user));
        }
        cacheLoaded = true;
    }

    private User mapUser(User user) {
        Long id = user.getId();
        Role role = user.getRole();
        String email = user.getEmail();
        String fiscalCode = user.getFiscalCode();
        String name = user.getName();
        String surname = user.getSurname();

        if (Role.DOCTOR.equals(role) && user instanceof Doctor doctor) {
            List<Service> services = new ArrayList<>(doctor.getServices());
            List<DayOfWeek> availableDays = new ArrayList<>(doctor.getAvailableDays());
            Doctor newDoctor =  new Doctor(id, role, email, fiscalCode, name, surname);
            newDoctor.setServices(services);
            newDoctor.setAvailableDays(availableDays);
            return newDoctor;
        } else {
            return new User(id, role, email, fiscalCode, name, surname);
        }
    }

    @SuppressWarnings("unchecked")
    private List<User> loadFromFile() throws DAOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void saveToFile(List<User> users) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(users);
        } catch (IOException e) {
            throw new DAOException(e.getMessage(), e);
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
}
