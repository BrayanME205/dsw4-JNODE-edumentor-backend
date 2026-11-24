package cr.ac.ucr.edumentorhub.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import cr.ac.ucr.edumentorhub.entity.EvaluacionTutoria;

public interface EvaluacionTutoriaRepository extends JpaRepository<EvaluacionTutoria, Long> {

    boolean existsBySesionId(Long sesionId);

    Optional<EvaluacionTutoria> findBySesionId(Long sesionId);

    /** Reseñas de un tutor (sesion.tutor.id), de la más reciente a la más antigua. */
    List<EvaluacionTutoria> findBySesionTutorIdOrderByFechaEvaluacionDesc(Long tutorId);
}
