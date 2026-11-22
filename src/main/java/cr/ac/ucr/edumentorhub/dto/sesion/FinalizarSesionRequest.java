package cr.ac.ucr.edumentorhub.dto.sesion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Resumen del trabajo realizado que el tutor registra al finalizar la sesión. */
public record FinalizarSesionRequest(
        @NotBlank(message = "El resumen es obligatorio")
        @Size(max = 1000, message = "El resumen admite como máximo 1000 caracteres")
        String resumen) {
}
