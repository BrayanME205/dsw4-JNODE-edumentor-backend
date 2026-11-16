package cr.ac.ucr.edumentorhub.mapper;

import cr.ac.ucr.edumentorhub.dto.tutor.TutorResponse;
import cr.ac.ucr.edumentorhub.entity.Usuario;

public final class TutorMapper {

    private TutorMapper() {
    }

    public static TutorResponse toResponse(Usuario tutor) {
        return new TutorResponse(tutor.getId(), tutor.getNombre(), tutor.getApellido(),
                tutor.getPromedioCalificacion(), tutor.getTotalEvaluaciones());
    }
}
