package maindir.persistance.dao;

import maindir.exceptions.DAOException;
import maindir.model.ReservedSlot;
import java.util.List;

public interface ReservedSlotDAO {
    List<ReservedSlot> getAll() throws DAOException;
    void save(ReservedSlot slot) throws DAOException;
    void update(ReservedSlot slot) throws DAOException;
    void delete(ReservedSlot slot) throws DAOException;
}
