package cr.ac.ucr.edumentorhub.mapper;

import cr.ac.ucr.edumentorhub.dto.disponibilidad.DisponibilidadResponse;
import cr.ac.ucr.edumentorhub.entity.DisponibilidadTutor;

public final class DisponibilidadMapper {

    private DisponibilidadMapper() {
    }

    /** tutor.getId() no dispara una consulta extra: Hibernate ya conoce el id del proxy LAZY. */
    public static DisponibilidadResponse toResponse(DisponibilidadTutor bloque) {
        return new DisponibilidadResponse(bloque.getId(), bloque.getTutor().getId(), bloque.getDiaSemana(),
                bloque.getHoraInicio(), bloque.getHoraFin(), bloque.isEstaActivo());
    }
}
