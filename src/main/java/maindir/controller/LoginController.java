package maindir.controller;

import maindir.bean.UserBean;
import maindir.exceptions.ControllerException;
import maindir.exceptions.DAOException;
import maindir.exceptions.DuplicateUserException;
import maindir.model.enums.Role;
import maindir.persistance.dao.UserDAO;
import maindir.persistance.DAOFactory;
import maindir.model.User;

public class LoginController {

    public UserBean authenticate(UserBean logUser) throws ControllerException {
        UserBean bean = new UserBean();

        try

    {
        UserDAO userDAO = DAOFactory.getUserDAO();
        assert userDAO != null;

        User logU = new User();
        logU.setEmail(logUser.getEmail());
        logU.setPassword(logUser.getPassword());


        logU = userDAO.authenticate(logU);

        if (logU == null) {
            return null;
        }


        bean.setId(logU.getId());
        bean.setRole(logU.getRole());
        bean.setEmail(logU.getEmail());
        bean.setName(logU.getName());
        bean.setSurname(logU.getSurname());
        }catch (DAOException e){
            throw new ControllerException(e.getMessage(), e);
        }
        return bean;
    }

    public UserBean register(UserBean logUser) throws DuplicateUserException, ControllerException{
        try {
            UserDAO userDAO = DAOFactory.getUserDAO();
            assert userDAO != null;

            User newUser = new User(logUser.getEmail(), logUser.getPassword(), logUser.getFiscalCode(), logUser.getName(), logUser.getSurname(), Role.PATIENT);
            userDAO.save(newUser);
        }catch(DAOException e){
            throw new ControllerException(e.getMessage(), e);
        }catch(DuplicateUserException e){
            throw new DuplicateUserException(e.getMessage());
        }

        logUser.setRole(Role.PATIENT);
        return logUser;
    }

}

