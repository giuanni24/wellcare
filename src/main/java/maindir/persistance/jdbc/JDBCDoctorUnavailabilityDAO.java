package maindir.persistance.jdbc;

import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.persistance.dao.DoctorUnavailabilityDAO;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JDBCDoctorUnavailabilityDAO implements DoctorUnavailabilityDAO {
    private static JDBCDoctorUnavailabilityDAO instance;
    private List<DoctorUnavailability> cache;
    private boolean cacheLoaded;

    private JDBCDoctorUnavailabilityDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized JDBCDoctorUnavailabilityDAO getInstance() {
        if (instance == null) {
            instance = new JDBCDoctorUnavailabilityDAO();
        }
        return instance;
    }

    @Override
    public List<DoctorUnavailability> getAll() throws DAOException {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(DoctorUnavailability unavailability) throws DAOException {
        String query = "INSERT INTO doctor_unavailability (doctor_id, start_date, end_date, created_at) VALUES (?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, unavailability.getDoctor().getId());
            pstmt.setDate(2, Date.valueOf(unavailability.getStartDate()));
            pstmt.setDate(3, Date.valueOf(unavailability.getEndDate()));
            pstmt.setTimestamp(4, Timestamp.valueOf(unavailability.getCreatedAt()));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    unavailability.setId(generatedKeys.getLong(1));
                }
            }
            cache.add(unavailability);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @Override
    public void update(DoctorUnavailability unavailability) throws DAOException {
        String query = "UPDATE doctor_unavailability SET start_date = ?, end_date = ? WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(unavailability.getStartDate()));
            pstmt.setDate(2, Date.valueOf(unavailability.getEndDate()));
            pstmt.setLong(3, unavailability.getId());
            pstmt.executeUpdate();
            updateInCache(unavailability);
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(DoctorUnavailability unavailability) throws DAOException {
        String query = "DELETE FROM doctor_unavailability WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setLong(1, unavailability.getId());
            pstmt.executeUpdate();
            cache.removeIf(u -> u.getId().equals(unavailability.getId()));
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void loadCache() throws DAOException {
        cache.clear();
        String query = "SELECT id, doctor_id, start_date, end_date, created_at FROM doctor_unavailability";
        Connection conn = ConnectionFactory.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Long id = rs.getLong("id");
                Doctor doctor = (Doctor) JDBCUserDAO.getInstance().findUserById(rs.getLong("doctor_id"));
                LocalDate startDate = rs.getDate("start_date").toLocalDate();
                LocalDate endDate = rs.getDate("end_date").toLocalDate();
                LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();

                DoctorUnavailability unavailability = new DoctorUnavailability(id, doctor, startDate, endDate, createdAt);
                cache.add(unavailability);
            }
            cacheLoaded = true;
        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void updateInCache(DoctorUnavailability unavailability) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(unavailability.getId())) {
                cache.set(i, unavailability);
                return;
            }
        }
    }

    public DoctorUnavailability findUnavailabilityById(Long unavailabilityId) throws DAOException {
        if (unavailabilityId == null) return null;
        return JDBCDoctorUnavailabilityDAO.getInstance().getAll().stream()
                .filter(u -> u.getId().equals(unavailabilityId))
                .findFirst()
                .orElse(null);
    }

}
