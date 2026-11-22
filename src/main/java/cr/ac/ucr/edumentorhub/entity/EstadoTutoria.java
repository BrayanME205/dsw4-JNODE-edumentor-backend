package cr.ac.ucr.edumentorhub.entity;

/** Estados del ciclo de vida de una sesión de tutoría (máquina de estados del enunciado). */
public enum EstadoTutoria {
    REQUESTED,
    CONFIRMED,
    REJECTED,
    CANCELLED,
    IN_SESSION,
    COMPLETED,
    REVIEWED
}
