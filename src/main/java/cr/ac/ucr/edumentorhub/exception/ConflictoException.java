package cr.ac.ucr.edumentorhub.exception;

/**
 * 409 Conflict: el dato ya existe o choca con otro (por ejemplo, un correo duplicado).
 * La traduce a RFC 7807 el {@link GlobalExceptionHandler}.
 */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
