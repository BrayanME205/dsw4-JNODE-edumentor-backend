package cr.ac.ucr.edumentorhub.dto.evaluacion;

import java.time.LocalDateTime;

public record EvaluacionResponse(Long id, Long sesionId, int calificacionEstrellas, String comentario,
        LocalDateTime fechaEvaluacion) {
}
