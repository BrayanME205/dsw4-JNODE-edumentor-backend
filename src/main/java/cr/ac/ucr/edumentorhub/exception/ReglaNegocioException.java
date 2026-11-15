package cr.ac.ucr.edumentorhub.exception;

/**
 * 422 Unprocessable Entity: la solicitud es válida pero viola una regla de negocio.
 * La traduce a RFC 7807 el {@link GlobalExceptionHandler}.
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
