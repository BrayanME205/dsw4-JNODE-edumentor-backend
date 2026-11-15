package cr.ac.ucr.edumentorhub.exception;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Convierte cualquier excepción de la API en una respuesta RFC 7807 (ProblemDetail)
 * con los campos title, status, detail e instance.
 *
 * Spring serializa ProblemDetail con Content-Type application/problem+json.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- Excepciones de negocio (las lanzan los @Service) ----------

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail manejarNoEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        return crear(HttpStatus.NOT_FOUND, "Recurso no encontrado", ex.getMessage(), request);
    }

    @ExceptionHandler(ConflictoException.class)
    public ProblemDetail manejarConflicto(ConflictoException ex, HttpServletRequest request) {
        return crear(HttpStatus.CONFLICT, "Conflicto", ex.getMessage(), request);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail manejarReglaNegocio(ReglaNegocioException ex, HttpServletRequest request) {
        return crear(HttpStatus.UNPROCESSABLE_ENTITY, "Regla de negocio no cumplida", ex.getMessage(), request);
    }

    @ExceptionHandler(AccesoDenegadoNegocioException.class)
    public ProblemDetail manejarAccesoNegocio(AccesoDenegadoNegocioException ex, HttpServletRequest request) {
        return crear(HttpStatus.FORBIDDEN, "Acceso denegado", ex.getMessage(), request);
    }

    // ---------- Validación de datos de entrada (400) ----------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarValidacion(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(error.getField(), error.getDefaultMessage());
        }
        ProblemDetail problema = crear(HttpStatus.BAD_REQUEST, "Datos inválidos",
                "Uno o más campos no cumplen las validaciones.", request);
        problema.setProperty("errors", errores);
        return problema;
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class })
    public ProblemDetail manejarSolicitudMalFormada(Exception ex, HttpServletRequest request) {
        return crear(HttpStatus.BAD_REQUEST, "Solicitud mal formada",
                "El cuerpo o los parámetros de la solicitud no tienen el formato esperado.", request);
    }

    // ---------- Seguridad (401 y 403 que ocurren dentro de los controladores) ----------

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail manejarAutenticacion(AuthenticationException ex, HttpServletRequest request) {
        return crear(HttpStatus.UNAUTHORIZED, "No autenticado", "Correo o contraseña incorrectos.", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail manejarAccesoDenegado(AccessDeniedException ex, HttpServletRequest request) {
        return crear(HttpStatus.FORBIDDEN, "Acceso denegado",
                "Su rol no tiene permiso para realizar esta operación.", request);
    }

    // ---------- Otros casos comunes ----------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail manejarIntegridad(DataIntegrityViolationException ex, HttpServletRequest request) {
        return crear(HttpStatus.CONFLICT, "Conflicto",
                "La operación viola una restricción de la base de datos (dato duplicado o en uso).", request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail manejarRutaInexistente(NoResourceFoundException ex, HttpServletRequest request) {
        return crear(HttpStatus.NOT_FOUND, "Recurso no encontrado", "La ruta solicitada no existe.", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail manejarMetodo(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return crear(HttpStatus.METHOD_NOT_ALLOWED, "Método no permitido",
                "El método " + ex.getMethod() + " no está soportado en esta ruta.", request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ProblemDetail manejarTipoContenido(HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        return crear(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de contenido no soportado",
                "Envíe el cuerpo como JSON con el encabezado Content-Type: application/json.", request);
    }

    // ---------- Cualquier otro error (500): mensaje neutro, detalle solo en el log ----------

    @ExceptionHandler(Exception.class)
    public ProblemDetail manejarInesperado(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {}", request.getRequestURI(), ex);
        return crear(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno",
                "Ocurrió un error inesperado. Intente de nuevo más tarde.", request);
    }

    private ProblemDetail crear(HttpStatus estado, String titulo, String detalle, HttpServletRequest request) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        problema.setTitle(titulo);
        problema.setInstance(URI.create(request.getRequestURI()));
        return problema;
    }
}
