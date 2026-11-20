package cr.ac.ucr.edumentorhub.mapper;

import cr.ac.ucr.edumentorhub.dto.materia.MateriaRequest;
import cr.ac.ucr.edumentorhub.dto.materia.MateriaResponse;
import cr.ac.ucr.edumentorhub.entity.Materia;

public final class MateriaMapper {

    private MateriaMapper() {
    }

    public static MateriaResponse toResponse(Materia materia) {
        return new MateriaResponse(materia.getId(), materia.getCodigoMateria(), materia.getNombreMateria(),
                materia.getCarrera(), materia.getCreditos());
    }

    public static Materia toEntity(MateriaRequest request) {
        return new Materia(request.codigoMateria(), request.nombreMateria().trim(), request.carrera().trim(),
                request.creditos());
    }

    /** Copia los datos del request sobre una materia existente (para PUT). */
    public static void actualizar(Materia materia, MateriaRequest request) {
        materia.setCodigoMateria(request.codigoMateria());
        materia.setNombreMateria(request.nombreMateria().trim());
        materia.setCarrera(request.carrera().trim());
        materia.setCreditos(request.creditos());
    }
}
