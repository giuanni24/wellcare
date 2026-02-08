package maindir.persistance.jdbc;

import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.model.enums.Role;
import maindir.persistance.dao.NotificationDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCNotificationDAO implements NotificationDAO {

    private static JDBCNotificationDAO instance;
    private List<Notification> cache;
    private boolean cacheLoaded;

    private JDBCNotificationDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized JDBCNotificationDAO getInstance() {
        if (instance == null) {
            instance = new JDBCNotificationDAO();
        }
        return instance;
    }

    @Override
    public List<Notification> getAll() throws DAOException {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    public void save(Notification notification) throws DAOException {
        String query = "INSERT INTO notifications (appointment_id, message, target_role, is_read) VALUES (?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, notification.getAppointment().getId());
            pstmt.setString(2, notification.getMessage());
            pstmt.setString(3, notification.getTargetRole().toString());
            pstmt.setBoolean(4, notification.isRead());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notification.setId(generatedKeys.getLong(1));
                }
            }

            cache.add(notification);

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }


    @Override
    public void update(Notification notification) throws DAOException {
        String query = "UPDATE notifications SET is_read = ? WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setBoolean(1, notification.isRead());
            pstmt.setLong(2, notification.getId());

            pstmt.executeUpdate();

            updateInCache(notification);

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void loadCache() throws DAOException {
        cache.clear();

        String query = "SELECT id, appointment_id, message, target_role, is_read FROM notifications";
        Connection conn = ConnectionFactory.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                Long appointmentId = rs.getLong("appointment_id");
                String message = rs.getString("message");
                Role targetRole = Role.valueOf(rs.getString("target_role"));
                boolean isRead = rs.getBoolean("is_read");

                Notification notification = new Notification(id, JDBCAppointmentDAO.getInstance().findAppointmentById(appointmentId), message, targetRole, isRead);

                cache.add(notification);
            }

            cacheLoaded = true;

        } catch (SQLException | DAOException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }


    private void updateInCache(Notification notification) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(notification.getId())) {
                cache.set(i, notification);
                return;
            }
        }
    }
}
