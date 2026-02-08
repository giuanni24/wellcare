package maindir.exceptions;

public class ApplicationException extends Exception {
    public ApplicationException() {
        super();
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message) {
        super(message);
    }
}