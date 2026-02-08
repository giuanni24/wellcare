package maindir.controller;

import maindir.bean.UserBean;
import maindir.exceptions.ControllerException;
import maindir.persistance.DAOFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import maindir.exceptions.DuplicateUserException;
import static org.junit.jupiter.api.Assertions.*;
class DuplicateUserTest {

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
    void testRegister_DuplicateEmail_ThrowsException() {
        UserBean newUser = new UserBean();
        newUser.setEmail("paziente@email.com");
        newUser.setPassword("password123");
        newUser.setFiscalCode("RSSMRA90A01H501Z");
        newUser.setName("Mario");
        newUser.setSurname("Rossi");

        boolean correctExceptionThrown = false;
        try {
            controller.register(newUser);
        } catch (DuplicateUserException e) {
            correctExceptionThrown = true;
        } catch (ControllerException e) {
            return;
        }

        assertTrue(correctExceptionThrown);
    }
}