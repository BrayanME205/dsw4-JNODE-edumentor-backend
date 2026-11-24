package cr.ac.ucr.edumentorhub.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/** Calificación (1 a 5 estrellas) que el estudiante da a una sesión completada. Una por sesión. */
@Entity
@Table(name = "evaluacion_tutoria")
@Check(constraints = "calificacion_estrellas BETWEEN 1 AND 5")
public class EvaluacionTutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sesion_id", nullable = false, unique = true)
    private SesionTutoria sesion;

    @Column(name = "calificacion_estrellas", nullable = false)
    private int calificacionEstrellas;

    @Column(name = "comentario", length = 500)
    private String comentario;

    @Column(name = "fecha_evaluacion", nullable = false)
    private LocalDateTime fechaEvaluacion;

    // Requerido por JPA
    protected EvaluacionTutoria() {
    }

    public EvaluacionTutoria(SesionTutoria sesion, int calificacionEstrellas, String comentario,
            LocalDateTime fechaEvaluacion) {
        this.sesion = sesion;
        this.calificacionEstrellas = calificacionEstrellas;
        this.comentario = comentario;
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public Long getId() {
        return id;
    }

    public SesionTutoria getSesion() {
        return sesion;
    }

    public int getCalificacionEstrellas() {
        return calificacionEstrellas;
    }

    public String getComentario() {
        return comentario;
    }

    public LocalDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }
}
