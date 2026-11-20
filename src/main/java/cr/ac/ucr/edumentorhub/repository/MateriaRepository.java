package cr.ac.ucr.edumentorhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cr.ac.ucr.edumentorhub.entity.Materia;

public interface MateriaRepository extends JpaRepository<Materia, Long> {

    boolean existsByCodigoMateria(String codigoMateria);

    /** Para actualizar: ¿otra materia (con distinto id) ya usa ese código? */
    boolean existsByCodigoMateriaAndIdNot(String codigoMateria, Long id);

    List<Materia> findAllByOrderByNombreMateriaAsc();
}
