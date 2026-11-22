package cr.ac.ucr.edumentorhub.exception;

/** 409: el tutor o el estudiante ya tienen una sesión confirmada que se cruza con este horario. */
public class TraslapeHorarioException extends ConflictoException {

    public TraslapeHorarioException(String mensaje) {
        super(mensaje);
    }
}
