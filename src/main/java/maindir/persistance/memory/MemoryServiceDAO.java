package maindir.persistance.memory;

import maindir.model.Service;
import maindir.persistance.dao.ServiceDAO;

import java.util.ArrayList;
import java.util.List;

public class MemoryServiceDAO implements ServiceDAO {
    private static MemoryServiceDAO instance;
    private List<Service> cache;
    private boolean cacheLoaded;

    private MemoryServiceDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized MemoryServiceDAO getInstance() {
        if (instance == null) {
            instance = new MemoryServiceDAO();
        }
        return instance;
    }

    @Override
    public List<Service> getAll() {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Service service) {
        if (!cacheLoaded) {
            loadCache();
        }

        Long newId = cache.stream()
                .mapToLong(Service::getId)
                .max()
                .orElse(0L) + 1;
        service.setId(newId);

        cache.add(service);
    }

    @Override
    public void update(Service service) {
        if (!cacheLoaded) {
            loadCache();
        }
        updateInCache(service);
    }

    private void loadCache() {
        cache.clear();

        cache.add(new Service(1L, "Visita Cardiologica", 80.0));
        cache.add(new Service(2L, "Visita Dermatologica", 70.0));
        cache.add(new Service(3L, "Visita Ortopedica", 75.0));
        cache.add(new Service(4L, "Visita Oculistica", 65.0));
        cache.add(new Service(5L, "Visita Pediatrica", 60.0));

        cacheLoaded = true;
    }

    private void updateInCache(Service service) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(service.getId())) {
                cache.set(i, service);
                return;
            }
        }
    }
}
