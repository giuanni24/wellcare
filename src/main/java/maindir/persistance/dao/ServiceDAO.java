package maindir.persistance.dao;

import maindir.exceptions.DAOException;
import maindir.model.Service;
import java.util.List;

public interface ServiceDAO {

    List<Service> getAll() throws DAOException;

    void save(Service service) throws DAOException;

    void update(Service service) throws DAOException;
}
