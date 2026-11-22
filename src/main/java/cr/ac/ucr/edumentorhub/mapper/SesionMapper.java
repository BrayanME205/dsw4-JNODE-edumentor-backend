package cr.ac.ucr.edumentorhub.mapper;

import cr.ac.ucr.edumentorhub.dto.sesion.SesionResponse;
import cr.ac.ucr.edumentorhub.entity.SesionTutoria;
import cr.ac.ucr.edumentorhub.entity.Usuario;

public final class SesionMapper {

    private SesionMapper() {
    }

    public static SesionResponse toResponse(SesionTutoria sesion) {
        return new SesionResponse(
                sesion.getId(),
                sesion.getEstudiante().getId(),
                nombreCompleto(sesion.getEstudiante()),
                sesion.getTutor().getId(),
                nombreCompleto(sesion.getTutor()),
                sesion.getMateria().getId(),
                sesion.getMateria().getCodigoMateria(),
                sesion.getMateria().getNombreMateria(),
                sesion.getFechaHora(),
                sesion.getFechaHoraFin(),
                sesion.getDuracionMinutos(),
                sesion.getEstado(),
                sesion.getModalidad(),
                sesion.getResumenTrabajo());
    }

    private static String nombreCompleto(Usuario usuario) {
        return usuario.getNombre() + " " + usuario.getApellido();
    }
}
