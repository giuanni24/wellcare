package maindir.persistance.jdbc;

import maindir.exceptions.DAOException;
import maindir.exceptions.DuplicateUserException;
import maindir.model.*;
import maindir.model.enums.Role;
import maindir.persistance.dao.UserDAO;
import java.sql.*;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class JDBCUserDAO implements UserDAO {

    private static JDBCUserDAO instance;
    private List<User> cache;
    private boolean cacheLoaded;

    private JDBCUserDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized JDBCUserDAO getInstance() {
        if (instance == null) {
            instance = new JDBCUserDAO();
        }
        return instance;
    }

    @Override
    public List<User> getAll() throws DAOException {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(User user) throws DAOException {
        String query = "INSERT INTO users (password, role, email, fiscal_code, name, surname) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();
        try(PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getRole().name());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getFiscalCode());
            pstmt.setString(5, user.getName());
            pstmt.setString(6, user.getSurname());


            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                }
            }
            user.setPassword("");
            cache.add(user);

        } catch (SQLException e) {
            throw new DuplicateUserException(e.getMessage());
        }
    }

    @Override
    public void update(User user) throws DAOException {
        String query = "UPDATE users SET email = ?, fiscal_code = ?, name = ?, surname = ? WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFiscalCode());
            pstmt.setString(3, user.getName());
            pstmt.setString(4, user.getSurname());
            pstmt.setLong(5, user.getId());

            pstmt.executeUpdate();

            updateInCache(user);

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    public User authenticate(User user) throws DAOException {
        String query = "SELECT id, role, email, fiscal_code, name, surname FROM users WHERE email = ? AND password = ?";
        Connection conn = ConnectionFactory.getConnection();
        try(PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPassword());

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapUser(rs);  // ← Usa metodo estratto
            }

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }

        return null;
    }

    private void loadCache() throws DAOException {
        cache.clear();

        String query = "SELECT id, role, email, fiscal_code, name, surname FROM users";
        Connection conn = ConnectionFactory.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                User user = mapUser(rs);
                cache.add(user);
            }

            cacheLoaded = true;

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    // Metodo estratto che mappa ResultSet → User
    private User mapUser(ResultSet rs) throws SQLException, DAOException {
        Long id = rs.getLong("id");
        String roleString = rs.getString("role");
        Role role = Role.valueOf(roleString);
        String email = rs.getString("email");
        String fiscalCode = rs.getString("fiscal_code");
        String name = rs.getString("name");
        String surname = rs.getString("surname");

        if (Role.DOCTOR.equals(role)) {
            List<Service> services = loadDoctorServices(id);
            List<DayOfWeek> availableDays = loadDoctorDays(id);
            Doctor doctor =  new Doctor(id, role, email, fiscalCode, name, surname);
            doctor.setServices(services);
            doctor.setAvailableDays(availableDays);
            return doctor;
        } else {
            return new User(id, role, email, fiscalCode, name, surname);
        }
    }

    private List<Service> loadDoctorServices(Long doctorId) throws DAOException {
        List<Service> services = new ArrayList<>();

        String query = "SELECT service_id FROM doctor_services WHERE doctor_id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Long serviceId = rs.getLong("service_id");
                Service service = findServiceById(serviceId);
                if (service != null) {
                    services.add(service);
                }
            }

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }

        return services;
    }

    private List<DayOfWeek> loadDoctorDays(Long doctorId) throws DAOException {
        List<DayOfWeek> availableDays = new ArrayList<>();

        String query = "SELECT day_of_week FROM doctor_available_days WHERE doctor_id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String dayString = rs.getString("day_of_week");
                DayOfWeek day = DayOfWeek.valueOf(dayString);
                availableDays.add(day);
            }

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }

        return availableDays;
    }

    private Service findServiceById(Long serviceId) throws DAOException {
        if (serviceId == null) return null;

        return JDBCServiceDAO.getInstance().getAll().stream()
                .filter(s -> s.getId().equals(serviceId))
                .findFirst()
                .orElse(null);
    }

    private void updateInCache(User user) {
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getId().equals(user.getId())) {
                cache.set(i, user);
                return;
            }
        }
    }

    public User findUserById(Long userId) throws DAOException {
        if (userId == null) return null;

        return JDBCUserDAO.getInstance().getAll().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }
}
