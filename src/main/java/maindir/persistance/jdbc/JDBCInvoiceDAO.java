package maindir.persistance.jdbc;

import maindir.exceptions.DAOException;
import maindir.model.*;
import maindir.model.enums.InvoiceStatus;
import maindir.persistance.dao.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCInvoiceDAO implements InvoiceDAO {

    private static JDBCInvoiceDAO instance;
    private List<Invoice> cache;
    private boolean cacheLoaded;

    private JDBCInvoiceDAO() {
        this.cache = new ArrayList<>();
        this.cacheLoaded = false;
    }

    public static synchronized JDBCInvoiceDAO getInstance() {
        if (instance == null) {
            instance = new JDBCInvoiceDAO();
        }
        return instance;
    }

    @Override
    public List<Invoice> getAll() throws DAOException {
        if (!cacheLoaded) {
            loadCache();
        }
        return new ArrayList<>(cache);
    }

    @Override
    public void save(Invoice invoice) throws DAOException {
        String query = "INSERT INTO invoices (appointment_id, amount, payment_status) VALUES (?, ?, ?)";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, invoice.getAppointment().getId());
            pstmt.setDouble(2, invoice.getAmount());
            pstmt.setString(3, invoice.getPaymentStatus().name());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    invoice.setId(generatedKeys.getLong(1));
                }
            }

            cache.add(invoice);

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    @Override
    public void update(Invoice invoice) throws DAOException {
        String query = "UPDATE invoices SET payment_status = ? WHERE id = ?";
        Connection conn = ConnectionFactory.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, invoice.getPaymentStatus().name());
            pstmt.setLong(2, invoice.getId());

            pstmt.executeUpdate();

            updateInCache(invoice);

        } catch (SQLException e) {
            throw new DAOException(e.getMessage(), e);
        }
    }

    private void loadCache() throws DAOException {
        cache.clear();

        String query = "SELECT id, appointment_id, amount, payment_status FROM invoices";
        Connection conn = ConnectionFactory.getConnection();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Long id = rs.getLong("id");
                Appointment appointment = JDBCAppointmentDAO.getInstance().findAppointmentById(rs.getLong("appointment_id"));
                Double amount = rs.getDouble("amount");
                String statusString = rs.getString("payment_status");
                InvoiceStatus paymentStatus = InvoiceStatus.valueOf(statusString);

                Invoice invoice = new Invoice(id, appointment, amount, paymentStatus);

                cache.add(invoice);
            }

            cacheLoaded = true;

        } catch (SQLException | DAOException e) {
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
