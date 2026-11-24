package cr.ac.ucr.edumentorhub.mapper;

import cr.ac.ucr.edumentorhub.dto.evaluacion.EvaluacionResponse;
import cr.ac.ucr.edumentorhub.entity.EvaluacionTutoria;

public final class EvaluacionMapper {

    private EvaluacionMapper() {
    }

    public static EvaluacionResponse toResponse(EvaluacionTutoria evaluacion) {
        return new EvaluacionResponse(evaluacion.getId(), evaluacion.getSesion().getId(),
                evaluacion.getCalificacionEstrellas(), evaluacion.getComentario(), evaluacion.getFechaEvaluacion());
    }
}
