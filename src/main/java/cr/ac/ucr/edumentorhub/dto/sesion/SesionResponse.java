package cr.ac.ucr.edumentorhub.dto.sesion;

import java.time.LocalDateTime;

import cr.ac.ucr.edumentorhub.entity.EstadoTutoria;
import cr.ac.ucr.edumentorhub.entity.Modalidad;

public record SesionResponse(
        Long id,
        Long estudianteId,
        String estudianteNombre,
        Long tutorId,
        String tutorNombre,
        Long materiaId,
        String materiaCodigo,
        String materiaNombre,
        LocalDateTime fechaHora,
        LocalDateTime fechaHoraFin,
        int duracionMinutos,
        EstadoTutoria estado,
        Modalidad modalidad,
        String resumenTrabajo) {
}
