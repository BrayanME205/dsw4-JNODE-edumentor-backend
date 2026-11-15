package cr.ac.ucr.edumentorhub.dto.usuario;

import cr.ac.ucr.edumentorhub.entity.Rol;

/** Usuario expuesto por la API. Nunca incluye passwordHash. */
public record UsuarioResponse(Long id, String carne, String email, String nombre, String apellido, Rol rol) {
}
