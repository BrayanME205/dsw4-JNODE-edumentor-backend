package cr.ac.ucr.edumentorhub.dto.disponibilidad;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record DisponibilidadResponse(Long id, Long tutorId, DayOfWeek diaSemana, LocalTime horaInicio,
        LocalTime horaFin, boolean estaActivo) {
}
