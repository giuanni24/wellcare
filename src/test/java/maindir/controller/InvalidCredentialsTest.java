package maindir.controller;

import maindir.bean.UserBean;
import maindir.exceptions.ControllerException;
import maindir.persistance.DAOFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InvalidCredentialsTest {

    private LoginController controller;

    @BeforeAll
    static void setupDAODemo(){
        DAOFactory.setMode(DAOFactory.PersistenceMode.MEMORY);
    }

    @BeforeEach
    void setUp() {
        controller = new LoginController();
    }


    @Test
    void testAuthenticate_InvalidCredentials() throws ControllerException {
        UserBean loginUser = new UserBean();
        loginUser.setEmail("paziente@email.com");
        loginUser.setPassword("wrongpassword");

        UserBean result = controller.authenticate(loginUser);

        assertNull(result);
    }
}
