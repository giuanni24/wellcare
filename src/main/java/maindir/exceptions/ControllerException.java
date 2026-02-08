package maindir.exceptions;

public class ControllerException extends ApplicationException {

    public ControllerException() {
        super();
    }

    public ControllerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ControllerException(String message) {
        super(message);
    }
}
