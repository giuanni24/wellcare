package maindir.observer;

import maindir.exceptions.DAOException;
import maindir.model.Appointment;

public interface Observer {
    void update(Appointment appointment) throws DAOException;
}
