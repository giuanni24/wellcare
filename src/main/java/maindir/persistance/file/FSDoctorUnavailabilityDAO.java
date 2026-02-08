package maindir.persistance.file;

import maindir.exceptions.DAOException;
import maindir.model.DoctorUnavailability;
import maindir.persistance.dao.DoctorUnavailabilityDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FSDoctorUnavailabilityDAO implements DoctorUnavailabilityDAO {

    private static final String FILE_PATH = "data/doctorunavailability.dat";
    private static FSDoctorUnavailabilityDAO instance;
    private List<DoctorUnavailability> cache;
    private boolean cacheLoaded;
    private boolean fileChecked;

    private FSDoctorUnavailabilityDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
        this.fileChecked = false;
    }

    public static synchronized FSDoctorUnavailabilityDAO getInstance() {
        if (instance == null) {
            instance = new FSDoctorUnavailabilityDAO();
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
    public List<DoctorUnavailability> getAll() throws DAOException {
        ensureFileExists();
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(DoctorUnavailability unavailability) throws DAOException {
        ensureFileExists();
        List<DoctorUnavailability> all = loadFromFile();
        Long newId = all.stream()
                .mapToLong(DoctorUnavailability::getId)
                .max()
                .orElse(0L) + 1;
        unavailability.setId(newId);
        all.add(unavailability);
        saveToFile(all);
        cache.add(unavailability);
    }

    @Override
    public void update(DoctorUnavailability unavailability) throws DAOException {
        ensureFileExists();
        List<DoctorUnavailability> all = loadFromFile();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(unavailability.getId())) {
                all.set(i, unavailability);
                break;
            }
        }
        saveToFile(all);
        updateInCache(unavailability);
    }

    @Override
    public void delete(DoctorUnavailability unavailability) throws DAOException {
        ensureFileExists();
        List<DoctorUnavailability> all = loadFromFile();
        all.removeIf(u -> u.getId().equals(unavailability.getId()));
        saveToFile(all);
        cache.removeIf(u -> u.getId().equals(unavailability.getId()));
    }

    private void loadCache() throws DAOException {
        cache.clear();
        cache.addAll(loadFromFile());
        cacheLoaded = true;
    }

    @SuppressWarnings("unchecked")
    private List<DoctorUnavailability> loadFromFile() throws DAOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<DoctorUnavailability>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void saveToFile(List<DoctorUnavailability> data) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void updateInCache(DoctorUnavailability unavailability) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(unavailability.getId())) {
                cache.set(i, unavailability);
                return;
            }
        }
    }
}
