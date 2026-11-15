package cr.ac.ucr.edumentorhub.service;

import org.springframework.stereotype.Service;

import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.exception.RecursoNoEncontradoException;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;

/**
 * Obtiene la entidad Usuario del que hizo la petición.
 * Los controladores reciben el correo desde el token (Authentication#getName) y lo pasan a los servicios.
 */
@Service
public class UsuarioActualService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioActualService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario obtener(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el usuario autenticado"));
    }
}
