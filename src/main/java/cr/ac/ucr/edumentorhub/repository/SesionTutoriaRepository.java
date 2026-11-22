package cr.ac.ucr.edumentorhub.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import cr.ac.ucr.edumentorhub.entity.EstadoTutoria;
import cr.ac.ucr.edumentorhub.entity.SesionTutoria;

public interface SesionTutoriaRepository extends JpaRepository<SesionTutoria, Long> {

    // ---- Listados: @EntityGraph trae estudiante, tutor y materia en la MISMA consulta (evita N+1) ----

    @EntityGraph(attributePaths = { "estudiante", "tutor", "materia" })
    List<SesionTutoria> findByEstudianteIdOrderByFechaHoraDesc(Long estudianteId);

    @EntityGraph(attributePaths = { "estudiante", "tutor", "materia" })
    List<SesionTutoria> findByTutorIdOrderByFechaHoraDesc(Long tutorId);

    @EntityGraph(attributePaths = { "estudiante", "tutor", "materia" })
    List<SesionTutoria> findAllByOrderByFechaHoraDesc();

    @EntityGraph(attributePaths = { "estudiante", "tutor", "materia" })
    Optional<SesionTutoria> findConDetalleById(Long id);

    // ---- Traslapes: dos rangos se cruzan si inicioExistente < finNuevo y finExistente > inicioNuevo ----

    boolean existsByTutorIdAndEstadoInAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
            Long tutorId, Collection<EstadoTutoria> estados, LocalDateTime finNuevo, LocalDateTime inicioNuevo);

    boolean existsByEstudianteIdAndEstadoInAndFechaHoraLessThanAndFechaHoraFinGreaterThan(
            Long estudianteId, Collection<EstadoTutoria> estados, LocalDateTime finNuevo, LocalDateTime inicioNuevo);
}
