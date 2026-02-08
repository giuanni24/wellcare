package maindir.persistance.memory;

import maindir.model.DoctorUnavailability;
import maindir.persistance.dao.DoctorUnavailabilityDAO;

import java.util.ArrayList;
import java.util.List;

public class MemoryDoctorUnavailabilityDAO implements DoctorUnavailabilityDAO {

    private static MemoryDoctorUnavailabilityDAO instance;
    private List<DoctorUnavailability> cache;
    private boolean cacheLoaded;

    private MemoryDoctorUnavailabilityDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized MemoryDoctorUnavailabilityDAO getInstance() {
        if (instance == null) {
            instance = new MemoryDoctorUnavailabilityDAO();
        }
        return instance;
    }

    @Override
    public List<DoctorUnavailability> getAll() {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(DoctorUnavailability unavailability) {
        if (!cacheLoaded) {
            loadCache();
        }

        Long newId = cache.stream()
                .mapToLong(DoctorUnavailability::getId)
                .max()
                .orElse(0L) + 1;
        unavailability.setId(newId);
        cache.add(unavailability);
    }

    @Override
    public void update(DoctorUnavailability unavailability) {
        if (!cacheLoaded) {
            loadCache();
        }
        updateInCache(unavailability);
    }

    @Override
    public void delete(DoctorUnavailability unavailability) {
        if (!cacheLoaded) {
            loadCache();
        }
        cache.removeIf(u -> u.getId().equals(unavailability.getId()));
    }

    private void loadCache() {
        cache.clear();
        cacheLoaded = true;
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
