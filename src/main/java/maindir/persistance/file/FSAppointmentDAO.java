package maindir.persistance.file;

import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.model.enums.AppointmentStatus;
import maindir.observer.NotificationObserver;
import maindir.persistance.dao.AppointmentDAO;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FSAppointmentDAO implements AppointmentDAO {
    private static FSAppointmentDAO instance;
    private List<Appointment> cache;
    private boolean cacheLoaded;
    private boolean fileChecked;
    private static final String FILE_PATH = "data/appointments.dat";

    private FSAppointmentDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
        this.fileChecked = false;
    }

    public static synchronized FSAppointmentDAO getInstance() {
        if (instance == null) {
            instance = new FSAppointmentDAO();
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
    public List<Appointment> getAll() throws DAOException {
        ensureFileExists();
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public Appointment save(Appointment appointment) throws DAOException {
        ensureFileExists();
        List<Appointment> allAppointments = loadFromFile();

        Long newId = allAppointments.stream()
                .mapToLong(Appointment::getId)
                .max()
                .orElse(0L) + 1;
        appointment.setId(newId);

        allAppointments.add(appointment);
        saveToFile(allAppointments);
        cache.add(appointment);

        return appointment;
    }

    @Override
    public void update(Appointment appointment) throws DAOException {
        ensureFileExists();
        List<Appointment> allAppointments = loadFromFile();
        for (int i = 0; i < allAppointments.size(); i++) {
            if (allAppointments.get(i).getId().equals(appointment.getId())) {
                allAppointments.set(i, appointment);
                break;
            }
        }
        saveToFile(allAppointments);
        updateInCache(appointment);
    }

    private void loadCache() throws DAOException {
        cache.clear();
        List<Appointment> allAppointments = loadFromFile();
        for (Appointment apt : allAppointments) {
            Long id = apt.getId();
            User patient = apt.getPatient();
            Doctor doctor = apt.getDoctor();
            Service service = apt.getService();
            AppointmentStatus status = apt.getStatus();
            LocalDate requestDate = apt.getRequestedDate();
            LocalTime confirmedTime = apt.getConfirmedTime();

            Appointment appointment = new Appointment(id, patient, doctor, service, requestDate, confirmedTime, status);
            if (apt.isPending() || apt.isConfirmed() || apt.isRescheduled()) {
                apt.attach(NotificationObserver.getInstance());
            }
            cache.add(appointment);
        }
        cacheLoaded = true;
    }

    private List<Appointment> loadFromFile() throws DAOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (List<Appointment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void saveToFile(List<Appointment> appointments) throws DAOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(appointments);
        } catch (IOException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void updateInCache(Appointment appointment) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(appointment.getId())) {
                cache.set(i, appointment);
                if (appointment.isPending() || appointment.isConfirmed() || appointment.isRescheduled()) {
                    appointment.attach(NotificationObserver.getInstance());
                }
                return;
            }
        }
    }
}
