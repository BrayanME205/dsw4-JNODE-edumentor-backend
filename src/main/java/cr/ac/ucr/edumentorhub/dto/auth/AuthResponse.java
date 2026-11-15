package cr.ac.ucr.edumentorhub.dto.auth;

import cr.ac.ucr.edumentorhub.entity.Rol;

/** Respuesta del login: el token JWT y los datos mínimos del usuario para el frontend. */
public record AuthResponse(String token, String tipo, Long usuarioId, String nombre, Rol rol) {
}
