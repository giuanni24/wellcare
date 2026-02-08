package maindir.persistance.file;

import maindir.exceptions.DAOException;
import maindir.model.ReservedSlot;
import maindir.persistance.dao.ReservedSlotDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FSReservedSlotDAO implements ReservedSlotDAO {

    private static final String FILE_PATH = "data/reservedslots.dat";
    private static FSReservedSlotDAO instance;
    private List<ReservedSlot> cache;
    private boolean cacheLoaded;
    private boolean fileChecked;

    private FSReservedSlotDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
        this.fileChecked = false;
    }

    public static synchronized FSReservedSlotDAO getInstance() {
        if (instance == null) {
            instance = new FSReservedSlotDAO();
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
    public List<ReservedSlot> getAll() throws DAOException {
        ensureFileExists();
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(ReservedSlot slot) throws DAOException {
        ensureFileExists();
        List<ReservedSlot> all = loadFromFile();
        Long newId = all.stream()
                .mapToLong(ReservedSlot::getId)
                .max()
                .orElse(0L) + 1;
        slot.setId(newId);
        all.add(slot);
        saveToFile(all);
        cache.add(slot);
    }

    @Override
    public void update(ReservedSlot slot) throws DAOException {
        ensureFileExists();
        List<ReservedSlot> all = loadFromFile();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(slot.getId())) {
                all.set(i, slot);
                break;
            }
        }
        saveToFile(all);
        updateInCache(slot);
    }

    @Override
    public void delete(ReservedSlot slot) throws DAOException {
        ensureFileExists();
        List<ReservedSlot> all = loadFromFile();
        all.removeIf(s -> s.getId().equals(slot.getId()));
        saveToFile(all);
        cache.removeIf(s -> s.getId().equals(slot.getId()));
    }

    private void loadCache() {
        cache.clear();
        cache.addAll(loadFromFile());
        cacheLoaded = true;
    }

    @SuppressWarnings("unchecked")
    private List<ReservedSlot> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<ReservedSlot>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveToFile(List<ReservedSlot> data) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void updateInCache(ReservedSlot slot) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(slot.getId())) {
                cache.set(i, slot);
                return;
            }
        }
    }
}
