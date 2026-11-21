package cr.ac.ucr.edumentorhub.dto.disponibilidad;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

/** Ejemplo: { "diaSemana": "MONDAY", "horaInicio": "13:00", "horaFin": "17:00" } */
public record DisponibilidadRequest(
        @NotNull(message = "El día es obligatorio")
        DayOfWeek diaSemana,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin) {
}
