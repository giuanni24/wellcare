package maindir.controller;

import maindir.bean.UserBean;
import maindir.exceptions.ControllerException;
import maindir.persistance.DAOFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValidCredentialsTest {
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
    void testAuthenticate_ValidCredentials() throws ControllerException {
        UserBean loginUser = new UserBean();
        loginUser.setEmail("paziente@email.com");
        loginUser.setPassword("password123"); // Password corretta del DEMO

        UserBean result = controller.authenticate(loginUser);

        assertNotNull(result);
    }
}
