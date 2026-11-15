package cr.ac.ucr.edumentorhub.dto.auth;

import cr.ac.ucr.edumentorhub.entity.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Datos para crear una cuenta. Solo se aceptan los roles ESTUDIANTE y TUTOR (lo valida AuthService). */
public record RegistroRequest(
        @NotBlank(message = "El carné es obligatorio")
        @Size(max = 20, message = "El carné admite como máximo 20 caracteres")
        String carne,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 150, message = "El correo admite como máximo 150 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre admite como máximo 100 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100, message = "El apellido admite como máximo 100 caracteres")
        String apellido,

        @NotNull(message = "El rol es obligatorio")
        Rol rol) {
}
