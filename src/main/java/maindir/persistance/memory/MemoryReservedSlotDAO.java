package maindir.persistance.memory;

import maindir.model.ReservedSlot;
import maindir.persistance.dao.ReservedSlotDAO;

import java.util.ArrayList;
import java.util.List;

public class MemoryReservedSlotDAO implements ReservedSlotDAO {

    private static MemoryReservedSlotDAO instance;
    private List<ReservedSlot> cache;
    private boolean cacheLoaded;

    private MemoryReservedSlotDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized MemoryReservedSlotDAO getInstance() {
        if (instance == null) {
            instance = new MemoryReservedSlotDAO();
        }
        return instance;
    }

    @Override
    public List<ReservedSlot> getAll() {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(ReservedSlot slot) {
        if (!cacheLoaded) {
            loadCache();
        }

        Long newId = cache.stream()
                .mapToLong(ReservedSlot::getId)
                .max()
                .orElse(0L) + 1;
        slot.setId(newId);
        cache.add(slot);
    }

    @Override
    public void update(ReservedSlot slot) {
        if (!cacheLoaded) {
            loadCache();
        }
        updateInCache(slot);
    }

    @Override
    public void delete(ReservedSlot slot) {
        if (!cacheLoaded) {
            loadCache();
        }
        cache.removeIf(s -> s.getId().equals(slot.getId()));
    }

    private void loadCache() {
        cache.clear();
        cacheLoaded = true;
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
