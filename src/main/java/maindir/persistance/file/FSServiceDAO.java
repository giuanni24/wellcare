package maindir.persistance.file;

import maindir.exceptions.DAOException;
import maindir.model.Service;
import maindir.persistance.dao.ServiceDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FSServiceDAO implements ServiceDAO {
    private static FSServiceDAO instance;
    private List<Service> cache;
    private boolean cacheLoaded;
    private boolean fileChecked;
    private static final String FILE_PATH = "data/services.dat";

    private FSServiceDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
        this.fileChecked = false;
    }

    public static synchronized FSServiceDAO getInstance() {
        if (instance == null) {
            instance = new FSServiceDAO();
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
    public List<Service> getAll() throws DAOException {
        ensureFileExists();
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Service service) throws DAOException {
        ensureFileExists();
        List<Service> allServices = loadFromFile();

        Long newId = allServices.stream()
                .mapToLong(Service::getId)
                .max()
                .orElse(0L) + 1;
        service.setId(newId);

        allServices.add(service);
        saveToFile(allServices);
        cache.add(service);
    }

    @Override
    public void update(Service service) throws DAOException {
        ensureFileExists();
        List<Service> allServices = loadFromFile();
        for (int i = 0; i < allServices.size(); i++) {
            if (allServices.get(i).getId().equals(service.getId())) {
                allServices.set(i, service);
                break;
            }
        }
        saveToFile(allServices);
        updateInCache(service);
    }

    private void loadCache() {
        cache.clear();
        List<Service> allServices = loadFromFile();
        for (Service serv : allServices) {
            Long id = serv.getId();
            String name = serv.getName();
            double basePrice = serv.getBasePrice();

            Service service = new Service(id, name, basePrice);
            cache.add(service);
        }
        cacheLoaded = true;
    }

    @SuppressWarnings("unchecked")
    private List<Service> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Service>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveToFile(List<Service> services) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(services);
        } catch (IOException e) {
            throw new DAOException(e.getMessage(), e);
        }
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
