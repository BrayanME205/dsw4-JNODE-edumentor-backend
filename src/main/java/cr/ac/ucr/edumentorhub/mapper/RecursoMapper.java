package cr.ac.ucr.edumentorhub.mapper;

import cr.ac.ucr.edumentorhub.dto.recurso.RecursoResponse;
import cr.ac.ucr.edumentorhub.entity.RecursoEstudio;

public final class RecursoMapper {

    private RecursoMapper() {
    }

    public static RecursoResponse toResponse(RecursoEstudio recurso) {
        return new RecursoResponse(recurso.getId(), recurso.getSesion().getId(), recurso.getTitulo(),
                recurso.getUrlRecurso(), recurso.getTipoArchivo());
    }
}
