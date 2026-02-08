package maindir.persistance.jdbc;

import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.model.enums.AppointmentStatus;
import maindir.observer.NotificationObserver;
import maindir.persistance.dao.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class JDBCAppointmentDAO implements AppointmentDAO {

    private static JDBCAppointmentDAO instance;
    private List<Appointment> cache;
    private boolean cacheLoaded;

    private JDBCAppointmentDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized JDBCAppointmentDAO getInstance() {
        if (instance == null) {
            instance = new JDBCAppointmentDAO();
        }
        return instance;
    }

    @Override
    public List<Appointment> getAll() throws DAOException {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public Appointment save(Appointment appointment) throws DAOException {
        String query = "INSERT INTO appointments (patient_id, doctor_id, service_id, requested_date, status) VALUES (?, ?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, appointment.getPatient().getId());
            pstmt.setLong(2, appointment.getDoctor().getId());
            pstmt.setLong(3, appointment.getService().getId());
            pstmt.setDate(4, Date.valueOf(appointment.getRequestedDate()));
            pstmt.setString(5, appointment.getStatus().toString());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    appointment.setId(generatedKeys.getLong(1));
                }
            }

            cache.add(appointment);

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
        return appointment;
    }

    @Override
    public void update(Appointment appointment) throws DAOException {
        String query = "UPDATE appointments SET status = ? WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, appointment.getStatus().toString());
            pstmt.setLong(2, appointment.getId());

            pstmt.executeUpdate();

            updateInCache(appointment);

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void loadCache() throws DAOException {
        cache.clear();

        String query = "SELECT id, patient_id, doctor_id, service_id, status, requested_date, confirmed_time FROM appointments";
        Connection conn = ConnectionFactory.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                User patient = JDBCUserDAO.getInstance().findUserById(rs.getLong("patient_id"));
                Doctor doctor = (Doctor) JDBCUserDAO.getInstance().findUserById(rs.getLong("doctor_id"));
                Service service = JDBCServiceDAO.getInstance().findServiceById(rs.getLong("service_id"));
                AppointmentStatus status = AppointmentStatus.valueOf(rs.getString("status"));
                LocalDate requestDate = rs.getDate("requested_date").toLocalDate();
                Time confirmedTimeSQL = rs.getTime("confirmed_time");
                LocalTime confirmedTime = (confirmedTimeSQL != null) ? confirmedTimeSQL.toLocalTime() : null;


                Appointment apt = new Appointment(id, patient, doctor, service,  requestDate, confirmedTime, status);

                if (apt.isPending() || apt.isConfirmed() || apt.isRescheduled()) {
                    apt.attach(NotificationObserver.getInstance());
                }

                cache.add(apt);
            }

            cacheLoaded = true;

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    public Appointment findAppointmentById(Long appointmentId) throws DAOException {
        if (appointmentId == null) return null;

        return JDBCAppointmentDAO.getInstance().getAll().stream()
                .filter(apt -> apt.getId().equals(appointmentId))
                .findFirst()
                .orElse(null);
    }

    private void updateInCache(Appointment appointment) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(appointment.getId())) {
                cache.set(i, appointment);
                return;
            }
        }
    }


}
