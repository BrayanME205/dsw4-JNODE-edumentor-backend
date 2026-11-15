package cr.ac.ucr.edumentorhub.exception;

/**
 * 403 Forbidden: el usuario está autenticado pero no puede operar sobre este recurso.
 * La traduce a RFC 7807 el {@link GlobalExceptionHandler}.
 */
public class AccesoDenegadoNegocioException extends RuntimeException {

    public AccesoDenegadoNegocioException(String mensaje) {
        super(mensaje);
    }
}
