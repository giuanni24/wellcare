package maindir.persistance.dao;

import maindir.exceptions.DAOException;
import maindir.model.Appointment;
import java.util.List;

public interface AppointmentDAO {

    List<Appointment> getAll() throws DAOException;

    Appointment save(Appointment appointment) throws DAOException;

    void update(Appointment appointment) throws DAOException;
}
