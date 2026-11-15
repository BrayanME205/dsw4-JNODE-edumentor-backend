package cr.ac.ucr.edumentorhub.exception;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Escribe un error RFC 7807 directamente en la respuesta HTTP.
 *
 * Se usa en los filtros de Spring Security (401 y 403), porque esos errores ocurren
 * antes de llegar a los controladores y por eso @RestControllerAdvice no los atrapa.
 */
public final class ProblemDetailWriter {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ProblemDetailWriter() {
    }

    public static void escribir(HttpServletRequest request, HttpServletResponse response,
            int estado, String titulo, String detalle) throws IOException {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("type", "about:blank");
        cuerpo.put("title", titulo);
        cuerpo.put("status", estado);
        cuerpo.put("detail", detalle);
        cuerpo.put("instance", request.getRequestURI());

        response.setStatus(estado);
        response.setContentType("application/problem+json");
        response.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(response.getWriter(), cuerpo);
    }
}
