package cr.ac.ucr.edumentorhub.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.dto.evaluacion.EvaluacionRequest;
import cr.ac.ucr.edumentorhub.dto.evaluacion.EvaluacionResponse;
import cr.ac.ucr.edumentorhub.entity.EstadoTutoria;
import cr.ac.ucr.edumentorhub.entity.EvaluacionTutoria;
import cr.ac.ucr.edumentorhub.entity.SesionTutoria;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.exception.AccesoDenegadoNegocioException;
import cr.ac.ucr.edumentorhub.exception.ConflictoException;
import cr.ac.ucr.edumentorhub.exception.RecursoNoEncontradoException;
import cr.ac.ucr.edumentorhub.exception.ReglaNegocioException;
import cr.ac.ucr.edumentorhub.mapper.EvaluacionMapper;
import cr.ac.ucr.edumentorhub.repository.EvaluacionTutoriaRepository;
import cr.ac.ucr.edumentorhub.repository.SesionTutoriaRepository;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;

@Service
public class EvaluacionTutoriaService {

    private final EvaluacionTutoriaRepository evaluacionRepository;
    private final SesionTutoriaRepository sesionRepository;
    private final UsuarioRepository usuarioRepository;
    private final SesionTutoriaService sesionTutoriaService;
    private final Clock clock;

    public EvaluacionTutoriaService(EvaluacionTutoriaRepository evaluacionRepository,
            SesionTutoriaRepository sesionRepository, UsuarioRepository usuarioRepository,
            SesionTutoriaService sesionTutoriaService, Clock clock) {
        this.evaluacionRepository = evaluacionRepository;
        this.sesionRepository = sesionRepository;
        this.usuarioRepository = usuarioRepository;
        this.sesionTutoriaService = sesionTutoriaService;
        this.clock = clock;
    }

    /**
     * Guarda la evaluación, recalcula el promedio del tutor y pasa la sesión a REVIEWED.
     * Todo en una sola transacción: si algo falla, no queda nada a medias.
     */
    @Transactional
    public EvaluacionResponse registrar(Long sesionId, String emailEstudiante, EvaluacionRequest request) {
        SesionTutoria sesion = buscarSesion(sesionId);
        if (!sesion.getEstudiante().getEmail().equals(emailEstudiante)) {
            throw new AccesoDenegadoNegocioException("Solo el estudiante de la sesión puede evaluarla");
        }
        if (sesion.getEstado() != EstadoTutoria.COMPLETED) {
            throw new ReglaNegocioException("Solo se pueden evaluar sesiones completadas");
        }
        if (evaluacionRepository.existsBySesionId(sesionId)) {
            throw new ConflictoException("Esta sesión ya fue evaluada");
        }

        String comentario = request.comentario() == null || request.comentario().isBlank() ? null
                : request.comentario().trim();
        EvaluacionTutoria evaluacion = evaluacionRepository.save(new EvaluacionTutoria(sesion,
                request.calificacionEstrellas(), comentario, LocalDateTime.now(clock)));

        actualizarPromedio(sesion.getTutor(), request.calificacionEstrellas());
        sesionTutoriaService.marcarRevisada(sesionId);

        return EvaluacionMapper.toResponse(evaluacion);
    }

    @Transactional(readOnly = true)
    public EvaluacionResponse obtener(Long sesionId, String email) {
        SesionTutoria sesion = buscarSesion(sesionId);
        boolean participa = sesion.getEstudiante().getEmail().equals(email)
                || sesion.getTutor().getEmail().equals(email);
        if (!participa) {
            throw new AccesoDenegadoNegocioException("Solo los participantes pueden ver la evaluación");
        }
        return evaluacionRepository.findBySesionId(sesionId)
                .map(EvaluacionMapper::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("La sesión " + sesionId + " aún no tiene evaluación"));
    }

    /** Reseñas públicas de un tutor (para su perfil). */
    @Transactional(readOnly = true)
    public List<EvaluacionResponse> listarPorTutor(Long tutorId) {
        return evaluacionRepository.findBySesionTutorIdOrderByFechaEvaluacionDesc(tutorId).stream()
                .map(EvaluacionMapper::toResponse)
                .toList();
    }

    /**
     * Promedio incremental, sin releer todas las evaluaciones:
     * nuevoPromedio = (promedio * total + estrellas) / (total + 1), redondeado a 2 decimales.
     */
    private void actualizarPromedio(Usuario tutor, int estrellas) {
        int total = tutor.getTotalEvaluaciones();
        BigDecimal suma = tutor.getPromedioCalificacion()
                .multiply(BigDecimal.valueOf(total))
                .add(BigDecimal.valueOf(estrellas));
        BigDecimal nuevoPromedio = suma.divide(BigDecimal.valueOf(total + 1L), 2, RoundingMode.HALF_UP);

        tutor.setPromedioCalificacion(nuevoPromedio);
        tutor.setTotalEvaluaciones(total + 1);
        usuarioRepository.save(tutor);
    }

    private SesionTutoria buscarSesion(Long sesionId) {
        return sesionRepository.findConDetalleById(sesionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la sesión con id " + sesionId));
    }
}
