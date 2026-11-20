package cr.ac.ucr.edumentorhub.dto.tutor;

import java.math.BigDecimal;

/** Datos públicos de un tutor. Sin correo ni carné: es un catálogo consultable sin sesión. */
public record TutorResponse(Long id, String nombre, String apellido, BigDecimal promedioCalificacion,
        int totalEvaluaciones) {
}
