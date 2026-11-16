package cr.ac.ucr.edumentorhub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.dto.materia.MateriaRequest;
import cr.ac.ucr.edumentorhub.dto.materia.MateriaResponse;
import cr.ac.ucr.edumentorhub.entity.Materia;
import cr.ac.ucr.edumentorhub.exception.ConflictoException;
import cr.ac.ucr.edumentorhub.exception.RecursoNoEncontradoException;
import cr.ac.ucr.edumentorhub.mapper.MateriaMapper;
import cr.ac.ucr.edumentorhub.repository.MateriaRepository;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepository;

    public MateriaService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    @Transactional(readOnly = true)
    public List<MateriaResponse> listar() {
        return materiaRepository.findAllByOrderByNombreMateriaAsc().stream()
                .map(MateriaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MateriaResponse obtener(Long id) {
        return MateriaMapper.toResponse(buscar(id));
    }

    @Transactional
    public MateriaResponse crear(MateriaRequest request) {
        if (materiaRepository.existsByCodigoMateria(request.codigoMateria())) {
            throw new ConflictoException("Ya existe una materia con el código " + request.codigoMateria());
        }
        Materia materia = materiaRepository.save(MateriaMapper.toEntity(request));
        return MateriaMapper.toResponse(materia);
    }

    @Transactional
    public MateriaResponse actualizar(Long id, MateriaRequest request) {
        Materia materia = buscar(id);
        if (materiaRepository.existsByCodigoMateriaAndIdNot(request.codigoMateria(), id)) {
            throw new ConflictoException("Ya existe otra materia con el código " + request.codigoMateria());
        }
        MateriaMapper.actualizar(materia, request);
        return MateriaMapper.toResponse(materiaRepository.save(materia));
    }

    /** Si la materia ya tiene sesiones, la base de datos lo impide y el handler responde 409. */
    @Transactional
    public void eliminar(Long id) {
        Materia materia = buscar(id);
        materiaRepository.delete(materia);
        materiaRepository.flush();
    }

    private Materia buscar(Long id) {
        return materiaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la materia con id " + id));
    }
}
