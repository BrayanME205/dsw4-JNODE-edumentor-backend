package cr.ac.ucr.edumentorhub.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void recursoNoEncontradoDevuelve404ConFormatoRfc7807() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/materias/99");

        ProblemDetail problema = handler.manejarNoEncontrado(
                new RecursoNoEncontradoException("No existe la materia 99"), request);

        assertEquals(404, problema.getStatus());
        assertEquals("Recurso no encontrado", problema.getTitle());
        assertEquals("No existe la materia 99", problema.getDetail());
        assertEquals(URI.create("/api/materias/99"), problema.getInstance());
    }

    @Test
    void reglaDeNegocioDevuelve422() {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/sesiones/1/cancelar");

        ProblemDetail problema = handler.manejarReglaNegocio(
                new ReglaNegocioException("La cancelación requiere al menos 2 horas de anticipación"), request);

        assertEquals(422, problema.getStatus());
        assertEquals(URI.create("/api/sesiones/1/cancelar"), problema.getInstance());
    }

    @Test
    void errorInesperadoNoExponeElMensajeInterno() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/tutores");

        ProblemDetail problema = handler.manejarInesperado(new IllegalStateException("NullPointer en X"), request);

        assertEquals(500, problema.getStatus());
        assertEquals("Ocurrió un error inesperado. Intente de nuevo más tarde.", problema.getDetail());
    }
}
