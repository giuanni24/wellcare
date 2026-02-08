package maindir.persistance.dao;

import maindir.exceptions.DAOException;
import maindir.model.Notification;
import java.util.List;

public interface NotificationDAO {

    List<Notification> getAll() throws DAOException;

    void save(Notification notification) throws DAOException;

    void update(Notification notification) throws DAOException;
}
