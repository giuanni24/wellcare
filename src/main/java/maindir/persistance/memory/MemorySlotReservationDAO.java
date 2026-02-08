package maindir.persistance.memory;

import maindir.model.SlotReservation;
import maindir.persistance.dao.SlotReservationDAO;

import java.util.ArrayList;
import java.util.List;

public class MemorySlotReservationDAO implements SlotReservationDAO {

    private static MemorySlotReservationDAO instance;
    private List<SlotReservation> cache;
    private boolean cacheLoaded;

    private MemorySlotReservationDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized MemorySlotReservationDAO getInstance() {
        if (instance == null) {
            instance = new MemorySlotReservationDAO();
        }
        return instance;
    }

    @Override
    public List<SlotReservation> getAll() {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(SlotReservation reservation) {
        if (!cacheLoaded) {
            loadCache();
        }

        Long newId = cache.stream()
                .mapToLong(SlotReservation::getId)
                .max()
                .orElse(0L) + 1;
        reservation.setId(newId);
        cache.add(reservation);
    }

    @Override
    public void delete(SlotReservation reservation) {
        if (!cacheLoaded) {
            loadCache();
        }
        cache.removeIf(r -> r.getId().equals(reservation.getId()));
    }

    private void loadCache() {
        cache.clear();
        cacheLoaded = true;
    }

}
