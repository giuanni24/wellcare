package maindir.persistance.jdbc;

import maindir.exceptions.DAOException;
import maindir.model.Service;
import maindir.persistance.dao.ServiceDAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCServiceDAO implements ServiceDAO {

    private static JDBCServiceDAO instance;
    private List<Service> cache;
    private boolean cacheLoaded;

    private JDBCServiceDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized JDBCServiceDAO getInstance() {
        if (instance == null) {
            instance = new JDBCServiceDAO();
        }
        return instance;
    }

    @Override
    public List<Service> getAll() throws DAOException {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Service service) throws DAOException {
        String query = "INSERT INTO services (name, base_price) VALUES (?, ?)";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, service.getName());
            pstmt.setDouble(2, service.getBasePrice());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    service.setId(generatedKeys.getLong(1));
                }
            }

            cache.add(service);

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @Override
    public void update(Service service) throws DAOException {
        String query = "UPDATE services SET name = ?, base_price = ? WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, service.getName());
            pstmt.setDouble(2, service.getBasePrice());
            pstmt.setLong(3, service.getId());

            pstmt.executeUpdate();

            updateInCache(service);

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void loadCache() throws DAOException {
        cache.clear();

        String query = "SELECT id, name, base_price FROM services ORDER BY name";
        Connection conn = ConnectionFactory.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                double basePrice = rs.getDouble("base_price");

                Service service = new Service(id, name, basePrice);

                cache.add(service);
            }

            cacheLoaded = true;

        } catch (SQLException e) {
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

    public Service findServiceById(Long serviceId) throws DAOException {
        if (serviceId == null) return null;

        return JDBCServiceDAO.getInstance().getAll().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElse(null);
    }
}
