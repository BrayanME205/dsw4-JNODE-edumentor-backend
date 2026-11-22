package cr.ac.ucr.edumentorhub.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Sesión de tutoría entre un estudiante y un tutor sobre una materia.
 * Solo guarda datos: las reglas (traslapes, 2 horas, transiciones) viven en SesionTutoriaService.
 */
@Entity
@Table(name = "sesion_tutoria")
@Check(constraints = "duracion_minutos > 0 AND estudiante_id <> tutor_id")
public class SesionTutoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Usuario tutor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false)
    private Materia materia;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "duracion_minutos", nullable = false)
    private int duracionMinutos;

    /** fechaHora + duracionMinutos. Se guarda para consultar traslapes con una comparación simple. */
    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoTutoria estado;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad", nullable = false, length = 20)
    private Modalidad modalidad;

    @Column(name = "resumen_trabajo", length = 1000)
    private String resumenTrabajo;

    // Requerido por JPA
    protected SesionTutoria() {
    }

    public SesionTutoria(Usuario estudiante, Usuario tutor, Materia materia, LocalDateTime fechaHora,
            int duracionMinutos, LocalDateTime fechaHoraFin, Modalidad modalidad, EstadoTutoria estado) {
        this.estudiante = estudiante;
        this.tutor = tutor;
        this.materia = materia;
        this.fechaHora = fechaHora;
        this.duracionMinutos = duracionMinutos;
        this.fechaHoraFin = fechaHoraFin;
        this.modalidad = modalidad;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public Usuario getEstudiante() {
        return estudiante;
    }

    public Usuario getTutor() {
        return tutor;
    }

    public Materia getMateria() {
        return materia;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public EstadoTutoria getEstado() {
        return estado;
    }

    public void setEstado(EstadoTutoria estado) {
        this.estado = estado;
    }

    public Modalidad getModalidad() {
        return modalidad;
    }

    public String getResumenTrabajo() {
        return resumenTrabajo;
    }

    public void setResumenTrabajo(String resumenTrabajo) {
        this.resumenTrabajo = resumenTrabajo;
    }
}
