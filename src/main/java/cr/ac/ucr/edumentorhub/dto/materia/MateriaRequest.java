package cr.ac.ucr.edumentorhub.dto.materia;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record MateriaRequest(
        @NotBlank(message = "El código es obligatorio")
        @Pattern(regexp = "^[A-Z]{2,4}[0-9]{3,4}$", message = "El código debe tener el formato IF0009")
        String codigoMateria,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre admite como máximo 150 caracteres")
        String nombreMateria,

        @NotBlank(message = "La carrera es obligatoria")
        @Size(max = 100, message = "La carrera admite como máximo 100 caracteres")
        String carrera,

        @Min(value = 1, message = "Los créditos deben estar entre 1 y 10")
        @Max(value = 10, message = "Los créditos deben estar entre 1 y 10")
        int creditos) {
}
