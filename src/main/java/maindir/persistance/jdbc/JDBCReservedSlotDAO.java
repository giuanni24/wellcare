package maindir.persistance.jdbc;

import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.persistance.dao.ReservedSlotDAO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class JDBCReservedSlotDAO implements ReservedSlotDAO {
    private static JDBCReservedSlotDAO instance;
    private List<ReservedSlot> cache;
    private boolean cacheLoaded;

    private JDBCReservedSlotDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized JDBCReservedSlotDAO getInstance() {
        if (instance == null) {
            instance = new JDBCReservedSlotDAO();
        }
        return instance;
    }

    @Override
    public List<ReservedSlot> getAll() throws DAOException {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(ReservedSlot slot) throws DAOException {
        String query = "INSERT INTO reserved_slots (doctor_id, date, time, unavailability_id, is_booked) VALUES (?, ?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, slot.getDoctor().getId());
            pstmt.setDate(2, Date.valueOf(slot.getDate()));
            pstmt.setTime(3, Time.valueOf(slot.getTime()));
            pstmt.setLong(4, slot.getUnavailabilityPeriod().getId());
            pstmt.setBoolean(5, slot.isBooked());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    slot.setId(generatedKeys.getLong(1));
                }
            }
            cache.add(slot);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @Override
    public void update(ReservedSlot slot) throws DAOException {
        String query = "UPDATE reserved_slots SET is_booked = ? WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setBoolean(1, slot.isBooked());
            pstmt.setLong(2, slot.getId());
            pstmt.executeUpdate();
            updateInCache(slot);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(ReservedSlot slot) throws DAOException {
        String query = "DELETE FROM reserved_slots WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, slot.getId());
            pstmt.executeUpdate();
            cache.removeIf(s -> s.getId().equals(slot.getId()));
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void loadCache() throws DAOException {
        cache.clear();
        String query = "SELECT id, doctor_id, date, time, unavailability_id, is_booked FROM reserved_slots";
        Connection conn = ConnectionFactory.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                Doctor doctor = (Doctor) JDBCUserDAO.getInstance().findUserById(rs.getLong("doctor_id"));
                LocalDate date = rs.getDate("date").toLocalDate();
                LocalTime time = rs.getTime("time").toLocalTime();
                DoctorUnavailability period = JDBCDoctorUnavailabilityDAO.getInstance().findUnavailabilityById(rs.getLong("unavailability_id"));
                boolean isBooked = rs.getBoolean("is_booked");

                ReservedSlot slot = new ReservedSlot(id, doctor, date, time, period, isBooked);
                cache.add(slot);
            }
            cacheLoaded = true;
        } catch (SQLException | DAOException e) {
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

    public ReservedSlot findSlotById(Long slotId) throws DAOException {
        if (slotId == null) return null;
        return JDBCReservedSlotDAO.getInstance().getAll().stream()
                .filter(s -> s.getId().equals(slotId))
                .findFirst()
                .orElse(null);
    }


}
