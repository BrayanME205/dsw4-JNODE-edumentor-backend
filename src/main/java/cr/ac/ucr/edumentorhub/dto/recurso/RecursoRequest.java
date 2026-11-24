package cr.ac.ucr.edumentorhub.dto.recurso;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Ejemplo: { "titulo": "Guía de JPA", "urlRecurso": "https://spring.io/guides", "tipoArchivo": "Enlace" } */
public record RecursoRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título admite como máximo 150 caracteres")
        String titulo,

        @NotBlank(message = "La URL es obligatoria")
        @URL(message = "La URL no es válida")
        @Size(max = 500, message = "La URL admite como máximo 500 caracteres")
        String urlRecurso,

        @Size(max = 50, message = "El tipo admite como máximo 50 caracteres")
        String tipoArchivo) {
}
