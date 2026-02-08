package maindir.persistance.dao;

import maindir.exceptions.DAOException;
import maindir.model.DoctorUnavailability;
import java.util.List;

public interface DoctorUnavailabilityDAO {
    List<DoctorUnavailability> getAll() throws DAOException;
    void save(DoctorUnavailability unavailability) throws DAOException;
    void update(DoctorUnavailability unavailability) throws DAOException;
    void delete(DoctorUnavailability unavailability) throws DAOException;
}
