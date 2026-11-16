package cr.ac.ucr.edumentorhub.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.dto.tutor.TutorResponse;
import cr.ac.ucr.edumentorhub.entity.Rol;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.exception.RecursoNoEncontradoException;
import cr.ac.ucr.edumentorhub.mapper.TutorMapper;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;

@Service
public class TutorService {

    private final UsuarioRepository usuarioRepository;

    public TutorService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Tutores ordenados de mejor a peor promedio. */
    @Transactional(readOnly = true)
    public List<TutorResponse> listar() {
        return usuarioRepository.findByRol(Rol.TUTOR).stream()
                .sorted(Comparator.comparing(Usuario::getPromedioCalificacion).reversed())
                .map(TutorMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TutorResponse obtener(Long id) {
        Usuario tutor = usuarioRepository.findById(id)
                .filter(usuario -> usuario.getRol() == Rol.TUTOR)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un tutor con id " + id));
        return TutorMapper.toResponse(tutor);
    }
}
