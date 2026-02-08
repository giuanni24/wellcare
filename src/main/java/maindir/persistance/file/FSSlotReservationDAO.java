package maindir.persistance.file;

import maindir.exceptions.DAOException;
import maindir.model.SlotReservation;
import maindir.persistance.dao.SlotReservationDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FSSlotReservationDAO implements SlotReservationDAO {

    private static final String FILE_PATH = "data/slotreservations.dat";
    private static FSSlotReservationDAO instance;
    private List<SlotReservation> cache;
    private boolean cacheLoaded;
    private boolean fileChecked;

    private FSSlotReservationDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
        this.fileChecked = false;
    }

    public static synchronized FSSlotReservationDAO getInstance() {
        if (instance == null) {
            instance = new FSSlotReservationDAO();
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
    public List<SlotReservation> getAll() throws DAOException {
        ensureFileExists();
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(SlotReservation reservation) throws DAOException {
        ensureFileExists();
        List<SlotReservation> all = loadFromFile();
        Long newId = all.stream()
                .mapToLong(SlotReservation::getId)
                .max()
                .orElse(0L) + 1;
        reservation.setId(newId);
        all.add(reservation);
        saveToFile(all);
        cache.add(reservation);
    }

    @Override
    public void delete(SlotReservation reservation) throws DAOException {
        ensureFileExists();
        List<SlotReservation> all = loadFromFile();
        all.removeIf(r -> r.getId().equals(reservation.getId()));
        saveToFile(all);
        cache.removeIf(r -> r.getId().equals(reservation.getId()));
    }

    private void loadCache() {
        cache.clear();
        cache.addAll(loadFromFile());
        cacheLoaded = true;
    }

    @SuppressWarnings("unchecked")
    private List<SlotReservation> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<SlotReservation>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveToFile(List<SlotReservation> data) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }
}
