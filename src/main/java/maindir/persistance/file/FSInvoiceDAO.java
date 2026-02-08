package maindir.persistance.file;

import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.model.enums.InvoiceStatus;
import maindir.persistance.dao.InvoiceDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FSInvoiceDAO implements InvoiceDAO {
    private static FSInvoiceDAO instance;
    private List<Invoice> cache;
    private boolean cacheLoaded;
    private boolean fileChecked;
    private static final String FILE_PATH = "data/invoices.dat";

    private FSInvoiceDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
        this.fileChecked = false;
    }

    public static synchronized FSInvoiceDAO getInstance() {
        if (instance == null) {
            instance = new FSInvoiceDAO();
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
    public List<Invoice> getAll() throws DAOException {
        ensureFileExists();
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Invoice invoice) throws DAOException {
        ensureFileExists();
        List<Invoice> allInvoices = loadFromFile();

        Long newId = allInvoices.stream()
                .mapToLong(Invoice::getId)
                .max()
                .orElse(0L) + 1;
        invoice.setId(newId);

        allInvoices.add(invoice);
        saveToFile(allInvoices);
        cache.add(invoice);
    }

    @Override
    public void update(Invoice invoice) throws DAOException {
        ensureFileExists();
        List<Invoice> allInvoices = loadFromFile();
        for (int i = 0; i < allInvoices.size(); i++) {
            if (allInvoices.get(i).getId().equals(invoice.getId())) {
                allInvoices.set(i, invoice);
                break;
            }
        }
        saveToFile(allInvoices);
        updateInCache(invoice);
    }

    private void loadCache() {
        cache.clear();
        List<Invoice> allInvoices = loadFromFile();
        for (Invoice inv : allInvoices) {
            Long id = inv.getId();
            Appointment appointment = inv.getAppointment();
            Double amount = inv.getAmount();
            InvoiceStatus paymentStatus = inv.getPaymentStatus();

            Invoice invoice = new Invoice(id, appointment, amount, paymentStatus);
            cache.add(invoice);
        }
        cacheLoaded = true;
    }

    @SuppressWarnings("unchecked")
    private List<Invoice> loadFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Invoice>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveToFile(List<Invoice> invoices) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(invoices);
        } catch (IOException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void updateInCache(Invoice invoice) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(invoice.getId())) {
                cache.set(i, invoice);
                return;
            }
        }
    }
}
