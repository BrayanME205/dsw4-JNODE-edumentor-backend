package cr.ac.ucr.edumentorhub.service;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.dto.recurso.RecursoRequest;
import cr.ac.ucr.edumentorhub.dto.recurso.RecursoResponse;
import cr.ac.ucr.edumentorhub.entity.EstadoTutoria;
import cr.ac.ucr.edumentorhub.entity.RecursoEstudio;
import cr.ac.ucr.edumentorhub.entity.SesionTutoria;
import cr.ac.ucr.edumentorhub.exception.AccesoDenegadoNegocioException;
import cr.ac.ucr.edumentorhub.exception.RecursoNoEncontradoException;
import cr.ac.ucr.edumentorhub.exception.ReglaNegocioException;
import cr.ac.ucr.edumentorhub.mapper.RecursoMapper;
import cr.ac.ucr.edumentorhub.repository.RecursoEstudioRepository;
import cr.ac.ucr.edumentorhub.repository.SesionTutoriaRepository;

@Service
public class RecursoEstudioService {

    /** Solo se comparten recursos en sesiones aceptadas (no en solicitudes, rechazadas ni canceladas). */
    private static final Set<EstadoTutoria> ESTADOS_CON_RECURSOS = Set.of(EstadoTutoria.CONFIRMED,
            EstadoTutoria.IN_SESSION, EstadoTutoria.COMPLETED, EstadoTutoria.REVIEWED);

    private final RecursoEstudioRepository recursoRepository;
    private final SesionTutoriaRepository sesionRepository;

    public RecursoEstudioService(RecursoEstudioRepository recursoRepository,
            SesionTutoriaRepository sesionRepository) {
        this.recursoRepository = recursoRepository;
        this.sesionRepository = sesionRepository;
    }

    @Transactional
    public RecursoResponse agregar(Long sesionId, String email, RecursoRequest request) {
        SesionTutoria sesion = buscarSesionComoParticipante(sesionId, email);
        if (!ESTADOS_CON_RECURSOS.contains(sesion.getEstado())) {
            throw new ReglaNegocioException("Solo se pueden agregar recursos a sesiones confirmadas o realizadas");
        }
        String tipo = request.tipoArchivo() == null || request.tipoArchivo().isBlank() ? null
                : request.tipoArchivo().trim();
        RecursoEstudio recurso = new RecursoEstudio(sesion, request.titulo().trim(), request.urlRecurso().trim(),
                tipo);
        return RecursoMapper.toResponse(recursoRepository.save(recurso));
    }

    @Transactional(readOnly = true)
    public List<RecursoResponse> listar(Long sesionId, String email) {
        buscarSesionComoParticipante(sesionId, email);
        return recursoRepository.findBySesionIdOrderByIdAsc(sesionId).stream()
                .map(RecursoMapper::toResponse)
                .toList();
    }

    private SesionTutoria buscarSesionComoParticipante(Long sesionId, String email) {
        SesionTutoria sesion = sesionRepository.findConDetalleById(sesionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la sesión con id " + sesionId));
        boolean participa = sesion.getEstudiante().getEmail().equals(email)
                || sesion.getTutor().getEmail().equals(email);
        if (!participa) {
            throw new AccesoDenegadoNegocioException("Solo los participantes de la sesión pueden ver o agregar recursos");
        }
        return sesion;
    }
}
