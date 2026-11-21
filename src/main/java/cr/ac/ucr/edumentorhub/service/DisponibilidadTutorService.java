package cr.ac.ucr.edumentorhub.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.dto.disponibilidad.DisponibilidadRequest;
import cr.ac.ucr.edumentorhub.dto.disponibilidad.DisponibilidadResponse;
import cr.ac.ucr.edumentorhub.entity.DisponibilidadTutor;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.exception.AccesoDenegadoNegocioException;
import cr.ac.ucr.edumentorhub.exception.RecursoNoEncontradoException;
import cr.ac.ucr.edumentorhub.exception.ReglaNegocioException;
import cr.ac.ucr.edumentorhub.mapper.DisponibilidadMapper;
import cr.ac.ucr.edumentorhub.repository.DisponibilidadTutorRepository;

@Service
public class DisponibilidadTutorService {

    private final DisponibilidadTutorRepository disponibilidadRepository;
    private final UsuarioActualService usuarioActualService;

    public DisponibilidadTutorService(DisponibilidadTutorRepository disponibilidadRepository,
            UsuarioActualService usuarioActualService) {
        this.disponibilidadRepository = disponibilidadRepository;
        this.usuarioActualService = usuarioActualService;
    }

    /** Bloques activos ordenados de lunes a domingo y por hora de inicio. */
    @Transactional(readOnly = true)
    public List<DisponibilidadResponse> listarPorTutor(Long tutorId) {
        return disponibilidadRepository.findByTutorIdAndEstaActivoTrue(tutorId).stream()
                .sorted(Comparator.comparing(DisponibilidadTutor::getDiaSemana)
                        .thenComparing(DisponibilidadTutor::getHoraInicio))
                .map(DisponibilidadMapper::toResponse)
                .toList();
    }

    @Transactional
    public DisponibilidadResponse crear(String emailTutor, DisponibilidadRequest request) {
        Usuario tutor = usuarioActualService.obtener(emailTutor);
        validarBloque(tutor.getId(), request, null);

        DisponibilidadTutor bloque = new DisponibilidadTutor(tutor, request.diaSemana(), request.horaInicio(),
                request.horaFin());
        return DisponibilidadMapper.toResponse(disponibilidadRepository.save(bloque));
    }

    @Transactional
    public DisponibilidadResponse actualizar(Long id, String emailTutor, DisponibilidadRequest request) {
        DisponibilidadTutor bloque = buscarPropio(id, emailTutor);
        validarBloque(bloque.getTutor().getId(), request, id);

        bloque.setDiaSemana(request.diaSemana());
        bloque.setHoraInicio(request.horaInicio());
        bloque.setHoraFin(request.horaFin());
        return DisponibilidadMapper.toResponse(disponibilidadRepository.save(bloque));
    }

    /** Borrado lógico: el bloque queda guardado pero deja de estar disponible. */
    @Transactional
    public void desactivar(Long id, String emailTutor) {
        DisponibilidadTutor bloque = buscarPropio(id, emailTutor);
        bloque.setEstaActivo(false);
        disponibilidadRepository.save(bloque);
    }

    /** Reglas: la hora de fin debe ser posterior al inicio y el bloque no puede solaparse con otro activo. */
    private void validarBloque(Long tutorId, DisponibilidadRequest request, Long idQueSeEdita) {
        if (!request.horaFin().isAfter(request.horaInicio())) {
            throw new ReglaNegocioException("La hora de fin debe ser posterior a la hora de inicio");
        }
        boolean seSolapa = disponibilidadRepository
                .findByTutorIdAndDiaSemanaAndEstaActivoTrue(tutorId, request.diaSemana()).stream()
                .filter(otro -> !otro.getId().equals(idQueSeEdita))
                .anyMatch(otro -> request.horaInicio().isBefore(otro.getHoraFin())
                        && request.horaFin().isAfter(otro.getHoraInicio()));
        if (seSolapa) {
            throw new ReglaNegocioException("El bloque se solapa con otro bloque activo del mismo día");
        }
    }

    private DisponibilidadTutor buscarPropio(Long id, String emailTutor) {
        DisponibilidadTutor bloque = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el bloque de disponibilidad " + id));
        if (!bloque.getTutor().getEmail().equals(emailTutor)) {
            throw new AccesoDenegadoNegocioException("Solo el tutor dueño puede modificar este bloque");
        }
        return bloque;
    }
}
