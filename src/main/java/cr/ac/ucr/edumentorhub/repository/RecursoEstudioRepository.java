package cr.ac.ucr.edumentorhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cr.ac.ucr.edumentorhub.entity.RecursoEstudio;

public interface RecursoEstudioRepository extends JpaRepository<RecursoEstudio, Long> {

    List<RecursoEstudio> findBySesionIdOrderByIdAsc(Long sesionId);
}
