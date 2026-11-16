package exceptions;

/** Excepción checked para errores de reglas de negocio. */
public class CustomBusinessException extends Exception {

    public CustomBusinessException(String message) {
        super(message);
    }

    public CustomBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
