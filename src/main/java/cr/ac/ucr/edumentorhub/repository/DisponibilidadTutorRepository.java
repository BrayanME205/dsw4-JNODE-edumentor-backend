package cr.ac.ucr.edumentorhub.repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import cr.ac.ucr.edumentorhub.entity.DisponibilidadTutor;

public interface DisponibilidadTutorRepository extends JpaRepository<DisponibilidadTutor, Long> {

    List<DisponibilidadTutor> findByTutorIdAndEstaActivoTrue(Long tutorId);

    List<DisponibilidadTutor> findByTutorIdAndDiaSemanaAndEstaActivoTrue(Long tutorId, DayOfWeek diaSemana);

    /**
     * ¿Algún bloque activo del tutor cubre completo el rango [inicio, fin] de ese día?
     * Lo usa REQ-10 para validar que una solicitud de sesión cae dentro de la disponibilidad.
     */
    @Query("""
            select case when count(d) > 0 then true else false end
            from DisponibilidadTutor d
            where d.tutor.id = :tutorId
              and d.diaSemana = :dia
              and d.estaActivo = true
              and d.horaInicio <= :inicio
              and d.horaFin >= :fin
            """)
    boolean existeBloqueQueCubre(@Param("tutorId") Long tutorId, @Param("dia") DayOfWeek dia,
            @Param("inicio") LocalTime inicio, @Param("fin") LocalTime fin);
}
