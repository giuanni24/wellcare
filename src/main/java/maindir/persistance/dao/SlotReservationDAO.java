package maindir.persistance.dao;

import maindir.exceptions.DAOException;
import maindir.model.SlotReservation;
import java.util.List;

public interface SlotReservationDAO {
    List<SlotReservation> getAll() throws DAOException;
    void save(SlotReservation reservation) throws DAOException;
    void delete(SlotReservation reservation) throws DAOException;
}
