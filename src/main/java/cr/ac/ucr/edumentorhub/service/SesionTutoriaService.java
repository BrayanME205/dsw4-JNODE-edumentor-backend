package cr.ac.ucr.edumentorhub.service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.edumentorhub.dto.sesion.FinalizarSesionRequest;
import cr.ac.ucr.edumentorhub.dto.sesion.SesionResponse;
import cr.ac.ucr.edumentorhub.dto.sesion.SolicitudSesionRequest;
import cr.ac.ucr.edumentorhub.entity.EstadoTutoria;
import cr.ac.ucr.edumentorhub.entity.Materia;
import cr.ac.ucr.edumentorhub.entity.Rol;
import cr.ac.ucr.edumentorhub.entity.SesionTutoria;
import cr.ac.ucr.edumentorhub.entity.Usuario;
import cr.ac.ucr.edumentorhub.exception.AccesoDenegadoNegocioException;
import cr.ac.ucr.edumentorhub.exception.RecursoNoEncontradoException;
import cr.ac.ucr.edumentorhub.exception.ReglaNegocioException;
import cr.ac.ucr.edumentorhub.exception.TraslapeHorarioException;
import cr.ac.ucr.edumentorhub.mapper.SesionMapper;
import cr.ac.ucr.edumentorhub.repository.DisponibilidadTutorRepository;
import cr.ac.ucr.edumentorhub.repository.MateriaRepository;
import cr.ac.ucr.edumentorhub.repository.SesionTutoriaRepository;
import cr.ac.ucr.edumentorhub.repository.UsuarioRepository;

/** Reglas de negocio del ciclo de vida de una sesión de tutoría. */
@Service
public class SesionTutoriaService {

    /** Máquina de estados: desde cada estado, a cuáles se puede pasar. */
    private static final Map<EstadoTutoria, Set<EstadoTutoria>> TRANSICIONES = Map.of(
            EstadoTutoria.REQUESTED, Set.of(EstadoTutoria.CONFIRMED, EstadoTutoria.REJECTED, EstadoTutoria.CANCELLED),
            EstadoTutoria.CONFIRMED, Set.of(EstadoTutoria.IN_SESSION, EstadoTutoria.CANCELLED),
            EstadoTutoria.IN_SESSION, Set.of(EstadoTutoria.COMPLETED),
            EstadoTutoria.COMPLETED, Set.of(EstadoTutoria.REVIEWED));

    /** Estados que ocupan la agenda y por lo tanto cuentan para detectar traslapes. */
    private static final Set<EstadoTutoria> OCUPAN_AGENDA = Set.of(EstadoTutoria.CONFIRMED, EstadoTutoria.IN_SESSION);

    /** Anticipación mínima para cancelar una sesión confirmada. */
    private static final int HORAS_MINIMAS_CANCELACION = 2;

    private final SesionTutoriaRepository sesionRepository;
    private final UsuarioRepository usuarioRepository;
    private final MateriaRepository materiaRepository;
    private final DisponibilidadTutorRepository disponibilidadRepository;
    private final UsuarioActualService usuarioActualService;
    private final Clock clock;

    public SesionTutoriaService(SesionTutoriaRepository sesionRepository, UsuarioRepository usuarioRepository,
            MateriaRepository materiaRepository, DisponibilidadTutorRepository disponibilidadRepository,
            UsuarioActualService usuarioActualService, Clock clock) {
        this.sesionRepository = sesionRepository;
        this.usuarioRepository = usuarioRepository;
        this.materiaRepository = materiaRepository;
        this.disponibilidadRepository = disponibilidadRepository;
        this.usuarioActualService = usuarioActualService;
        this.clock = clock;
    }

    // ===================== Solicitar =====================

    @Transactional
    public SesionResponse solicitar(String emailEstudiante, SolicitudSesionRequest request) {
        Usuario estudiante = usuarioActualService.obtener(emailEstudiante);
        Usuario tutor = usuarioRepository.findById(request.tutorId())
                .filter(usuario -> usuario.getRol() == Rol.TUTOR)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un tutor con id " + request.tutorId()));
        Materia materia = materiaRepository.findById(request.materiaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la materia con id " + request.materiaId()));

        LocalDateTime inicio = request.fechaHora();
        LocalDateTime fin = inicio.plusMinutes(request.duracionMinutos());

        if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
            throw new ReglaNegocioException("La sesión debe iniciar y terminar el mismo día");
        }
        boolean dentroDeDisponibilidad = disponibilidadRepository.existeBloqueQueCubre(
                tutor.getId(), inicio.getDayOfWeek(), inicio.toLocalTime(), fin.toLocalTime());
        if (!dentroDeDisponibilidad) {
            throw new ReglaNegocioException("El horario solicitado está fuera de la disponibilidad del tutor");
        }

        SesionTutoria sesion = new SesionTutoria(estudiante, tutor, materia, inicio, request.duracionMinutos(), fin,
                request.modalidad(), EstadoTutoria.REQUESTED);
        return SesionMapper.toResponse(sesionRepository.save(sesion));
    }

    // ===================== Aceptar y rechazar (tutor) =====================

    /**
     * Si hay traslape, la sesión queda REJECTED y se responde 409.
     * noRollbackFor evita que la excepción deshaga el cambio a REJECTED.
     */
    @Transactional(noRollbackFor = TraslapeHorarioException.class)
    public SesionResponse aceptar(Long id, String emailTutor) {
        SesionTutoria sesion = buscar(id);
        validarEsTutor(sesion, emailTutor);
        validarTransicion(sesion.getEstado(), EstadoTutoria.CONFIRMED);

        if (hayTraslape(sesion)) {
            sesion.setEstado(EstadoTutoria.REJECTED);
            sesionRepository.save(sesion);
            throw new TraslapeHorarioException(
                    "El horario se cruza con otra sesión confirmada del tutor o del estudiante; la solicitud fue rechazada");
        }

        sesion.setEstado(EstadoTutoria.CONFIRMED);
        return SesionMapper.toResponse(sesionRepository.save(sesion));
    }

    @Transactional
    public SesionResponse rechazar(Long id, String emailTutor) {
        SesionTutoria sesion = buscar(id);
        validarEsTutor(sesion, emailTutor);
        validarTransicion(sesion.getEstado(), EstadoTutoria.REJECTED);

        sesion.setEstado(EstadoTutoria.REJECTED);
        return SesionMapper.toResponse(sesionRepository.save(sesion));
    }

    // ===================== Cancelar, iniciar y finalizar =====================

    /**
     * REQUESTED: solo el estudiante puede desistir (el tutor la rechaza).
     * CONFIRMED: cualquiera de los dos, con al menos 2 horas de anticipación.
     */
    @Transactional
    public SesionResponse cancelar(Long id, String email) {
        SesionTutoria sesion = buscar(id);
        if (!esParticipante(sesion, email)) {
            throw new AccesoDenegadoNegocioException("Solo los participantes pueden cancelar esta sesión");
        }
        validarTransicion(sesion.getEstado(), EstadoTutoria.CANCELLED);

        if (sesion.getEstado() == EstadoTutoria.REQUESTED && !sesion.getEstudiante().getEmail().equals(email)) {
            throw new AccesoDenegadoNegocioException("El tutor no cancela una solicitud: debe rechazarla");
        }
        if (sesion.getEstado() == EstadoTutoria.CONFIRMED) {
            LocalDateTime limite = sesion.getFechaHora().minusHours(HORAS_MINIMAS_CANCELACION);
            if (LocalDateTime.now(clock).isAfter(limite)) {
                throw new ReglaNegocioException("La cancelación requiere al menos 2 horas de anticipación");
            }
        }

        sesion.setEstado(EstadoTutoria.CANCELLED);
        return SesionMapper.toResponse(sesionRepository.save(sesion));
    }

    /** CONFIRMED -> IN_SESSION, solo cuando ya llegó la hora pactada. */
    @Transactional
    public SesionResponse iniciar(Long id, String emailTutor) {
        SesionTutoria sesion = buscar(id);
        validarEsTutor(sesion, emailTutor);
        validarTransicion(sesion.getEstado(), EstadoTutoria.IN_SESSION);

        if (LocalDateTime.now(clock).isBefore(sesion.getFechaHora())) {
            throw new ReglaNegocioException("La sesión solo puede iniciarse a partir de la hora pactada");
        }

        sesion.setEstado(EstadoTutoria.IN_SESSION);
        return SesionMapper.toResponse(sesionRepository.save(sesion));
    }

    /** IN_SESSION -> COMPLETED, guardando el resumen del trabajo realizado. */
    @Transactional
    public SesionResponse finalizar(Long id, String emailTutor, FinalizarSesionRequest request) {
        SesionTutoria sesion = buscar(id);
        validarEsTutor(sesion, emailTutor);
        validarTransicion(sesion.getEstado(), EstadoTutoria.COMPLETED);

        sesion.setResumenTrabajo(request.resumen().trim());
        sesion.setEstado(EstadoTutoria.COMPLETED);
        return SesionMapper.toResponse(sesionRepository.save(sesion));
    }

    /**
     * COMPLETED -> REVIEWED. Lo llama EvaluacionTutoriaService (REQ-14) dentro de su misma transacción,
     * después de guardar la evaluación del estudiante.
     */
    @Transactional
    public void marcarRevisada(Long sesionId) {
        SesionTutoria sesion = buscar(sesionId);
        validarTransicion(sesion.getEstado(), EstadoTutoria.REVIEWED);
        sesion.setEstado(EstadoTutoria.REVIEWED);
        sesionRepository.save(sesion);
    }

    // ===================== Consultas =====================

    /** Estudiante: sus solicitudes. Tutor: las que le hicieron. Coordinador: todas (supervisión). */
    @Transactional(readOnly = true)
    public List<SesionResponse> listarMias(String email) {
        Usuario usuario = usuarioActualService.obtener(email);
        List<SesionTutoria> sesiones = switch (usuario.getRol()) {
            case ESTUDIANTE -> sesionRepository.findByEstudianteIdOrderByFechaHoraDesc(usuario.getId());
            case TUTOR -> sesionRepository.findByTutorIdOrderByFechaHoraDesc(usuario.getId());
            case COORDINADOR -> sesionRepository.findAllByOrderByFechaHoraDesc();
        };
        return sesiones.stream().map(SesionMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public SesionResponse obtener(Long id, String email) {
        SesionTutoria sesion = buscar(id);
        Usuario usuario = usuarioActualService.obtener(email);
        if (usuario.getRol() != Rol.COORDINADOR && !esParticipante(sesion, email)) {
            throw new AccesoDenegadoNegocioException("Solo los participantes pueden ver esta sesión");
        }
        return SesionMapper.toResponse(sesion);
    }

    // ===================== Apoyo =====================

    private SesionTutoria buscar(Long id) {
        return sesionRepository.findConDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la sesión con id " + id));
    }

    private boolean hayTraslape(SesionTutoria sesion) {
        boolean tutorOcupado = sesionRepository.existsByTutorIdAndEstadoInAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
                sesion.getTutor().getId(), OCUPAN_AGENDA, sesion.getFechaHoraFin(), sesion.getFechaHora());
        boolean estudianteOcupado = sesionRepository
                .existsByEstudianteIdAndEstadoInAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
                        sesion.getEstudiante().getId(), OCUPAN_AGENDA, sesion.getFechaHoraFin(), sesion.getFechaHora());
        return tutorOcupado || estudianteOcupado;
    }

    private void validarTransicion(EstadoTutoria actual, EstadoTutoria destino) {
        if (!TRANSICIONES.getOrDefault(actual, Set.of()).contains(destino)) {
            throw new ReglaNegocioException("No se puede pasar una sesión de " + actual + " a " + destino);
        }
    }

    private void validarEsTutor(SesionTutoria sesion, String email) {
        if (!sesion.getTutor().getEmail().equals(email)) {
            throw new AccesoDenegadoNegocioException("Solo el tutor de la sesión puede realizar esta acción");
        }
    }

    private boolean esParticipante(SesionTutoria sesion, String email) {
        return sesion.getEstudiante().getEmail().equals(email) || sesion.getTutor().getEmail().equals(email);
    }
}
