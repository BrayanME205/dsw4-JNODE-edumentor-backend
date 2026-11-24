package cr.ac.ucr.edumentorhub.dto.evaluacion;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EvaluacionRequest(
        @NotNull(message = "La calificación es obligatoria")
        @Min(value = 1, message = "La calificación debe estar entre 1 y 5")
        @Max(value = 5, message = "La calificación debe estar entre 1 y 5")
        Integer calificacionEstrellas,

        @Size(max = 500, message = "El comentario admite como máximo 500 caracteres")
        String comentario) {
}
