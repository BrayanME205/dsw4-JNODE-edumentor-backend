package cr.ac.ucr.edumentorhub.mapper;

import cr.ac.ucr.edumentorhub.dto.usuario.UsuarioResponse;
import cr.ac.ucr.edumentorhub.entity.Usuario;

public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getCarne(), usuario.getEmail(),
                usuario.getNombre(), usuario.getApellido(), usuario.getRol());
    }
}
