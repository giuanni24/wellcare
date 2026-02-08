package maindir.persistance.memory;

import maindir.model.*;
import maindir.model.enums.InvoiceStatus;
import maindir.persistance.dao.InvoiceDAO;

import java.util.ArrayList;
import java.util.List;

public class MemoryInvoiceDAO implements InvoiceDAO {
    private static MemoryInvoiceDAO instance;
    private List<Invoice> cache;
    private boolean cacheLoaded;

    private MemoryInvoiceDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized MemoryInvoiceDAO getInstance() {
        if (instance == null) {
            instance = new MemoryInvoiceDAO();
        }
        return instance;
    }

    @Override
    public List<Invoice> getAll() {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Invoice invoice) {
        if (!cacheLoaded) {
            loadCache();
        }

        Long newId = cache.stream()
                .mapToLong(Invoice::getId)
                .max()
                .orElse(0L) + 1;
        invoice.setId(newId);

        cache.add(invoice);
    }

    @Override
    public void update(Invoice invoice) {
        if (!cacheLoaded) {
            loadCache();
        }
        updateInCache(invoice);
    }

    private void loadCache() {
        cache.clear();
        loadInitialInvoices();
        cacheLoaded = true;
    }

    private void loadInitialInvoices() {
        MemoryAppointmentDAO appointmentDAO = MemoryAppointmentDAO.getInstance();
        List<Appointment> appointments = appointmentDAO.getAll();

        if (appointments.isEmpty()) {
            return;
        }

        // Fattura 1: PAID - appuntamento completato
        if (appointments.size() > 2) {
            Appointment apt = appointments.get(2);
            Invoice inv1 = new Invoice(
                    1L,
                    apt,
                    apt.getService().getBasePrice(),
                    InvoiceStatus.PAID
            );
            cache.add(inv1);
        }

        // Fattura 2: UNPAID - appuntamento confermato
        if (appointments.size() > 1) {
            Appointment apt = appointments.get(1);
            Invoice inv2 = new Invoice(
                    2L,
                    apt,
                    apt.getService().getBasePrice(),
                    InvoiceStatus.UNPAID
            );
            cache.add(inv2);
        }

        // Fattura 3: PENDING - appuntamento arrived
        if (appointments.size() > 3) {
            Appointment apt = appointments.get(3);
            Invoice inv3 = new Invoice(
                    3L,
                    apt,
                    apt.getService().getBasePrice(),
                    InvoiceStatus.UNPAID
            );
            cache.add(inv3);
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
