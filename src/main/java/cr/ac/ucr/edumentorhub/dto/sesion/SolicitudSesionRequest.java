package cr.ac.ucr.edumentorhub.dto.sesion;

import java.time.LocalDateTime;

import cr.ac.ucr.edumentorhub.entity.Modalidad;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** Ejemplo: { "tutorId": 2, "materiaId": 3, "fechaHora": "2026-11-25T15:00:00", "duracionMinutos": 60, "modalidad": "VIRTUAL" } */
public record SolicitudSesionRequest(
        @NotNull(message = "El tutor es obligatorio")
        Long tutorId,

        @NotNull(message = "La materia es obligatoria")
        Long materiaId,

        @NotNull(message = "La fecha y hora son obligatorias")
        @Future(message = "La sesión debe ser en una fecha futura")
        LocalDateTime fechaHora,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 30, message = "La duración mínima es de 30 minutos")
        @Max(value = 180, message = "La duración máxima es de 180 minutos")
        Integer duracionMinutos,

        @NotNull(message = "La modalidad es obligatoria")
        Modalidad modalidad) {
}
