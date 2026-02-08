package maindir.persistance.dao;

import maindir.exceptions.DAOException;
import maindir.model.Invoice;
import java.util.List;

public interface InvoiceDAO {

    List<Invoice> getAll() throws DAOException;

    void save(Invoice invoice) throws DAOException;

    void update(Invoice invoice) throws DAOException;
}
