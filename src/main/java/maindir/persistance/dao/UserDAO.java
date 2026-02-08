package maindir.persistance.dao;

import maindir.exceptions.DAOException;
import maindir.exceptions.DuplicateUserException;
import maindir.model.User;
import java.util.List;

public interface UserDAO {

    List<User> getAll() throws DAOException;

    void save(User user) throws DAOException, DuplicateUserException;

    void update(User user) throws DAOException;
    User authenticate(User user) throws DAOException;
}
