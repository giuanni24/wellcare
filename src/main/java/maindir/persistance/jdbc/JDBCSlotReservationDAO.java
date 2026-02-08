package maindir.persistance.jdbc;

import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.persistance.dao.SlotReservationDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCSlotReservationDAO implements SlotReservationDAO {
    private static JDBCSlotReservationDAO instance;
    private List<SlotReservation> cache;
    private boolean cacheLoaded;

    private JDBCSlotReservationDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized JDBCSlotReservationDAO getInstance() {
        if (instance == null) {
            instance = new JDBCSlotReservationDAO();
        }
        return instance;
    }

    @Override
    public List<SlotReservation> getAll() throws DAOException {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(SlotReservation reservation) throws DAOException {
        String query = "INSERT INTO slot_reservations (reserved_slot_id, appointment_id) VALUES (?, ?)";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, reservation.getSlot().getId());
            pstmt.setLong(2, reservation.getAppointment().getId());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getLong(1));
                }
            }
            cache.add(reservation);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(SlotReservation reservation) throws DAOException {
        String query = "DELETE FROM slot_reservations WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, reservation.getId());
            pstmt.executeUpdate();
            cache.removeIf(r -> r.getId().equals(reservation.getId()));
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void loadCache() throws DAOException {
        cache.clear();
        String query = "SELECT id, reserved_slot_id, appointment_id FROM slot_reservations";
        Connection conn = ConnectionFactory.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                ReservedSlot slot = JDBCReservedSlotDAO.getInstance().findSlotById(rs.getLong("reserved_slot_id"));
                Appointment appointment = JDBCAppointmentDAO.getInstance().findAppointmentById(rs.getLong("appointment_id"));

                SlotReservation reservation = new SlotReservation(id, slot, appointment);
                cache.add(reservation);
            }
            cacheLoaded = true;
        } catch (SQLException | DAOException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }



}
