package cr.ac.ucr.edumentorhub.exception;

/**
 * 404 Not Found: el recurso solicitado no existe.
 * La traduce a RFC 7807 el {@link GlobalExceptionHandler}.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
