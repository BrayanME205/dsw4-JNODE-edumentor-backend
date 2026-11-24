package cr.ac.ucr.edumentorhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/** Enlace o material compartido dentro de una sesión de tutoría. */
@Entity
@Table(name = "recurso_estudio")
public class RecursoEstudio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionTutoria sesion;

    @Column(name = "titulo", nullable = false, length = 150)
    private String titulo;

    @Column(name = "url_recurso", nullable = false, length = 500)
    private String urlRecurso;

    @Column(name = "tipo_archivo", length = 50)
    private String tipoArchivo;

    // Requerido por JPA
    protected RecursoEstudio() {
    }

    public RecursoEstudio(SesionTutoria sesion, String titulo, String urlRecurso, String tipoArchivo) {
        this.sesion = sesion;
        this.titulo = titulo;
        this.urlRecurso = urlRecurso;
        this.tipoArchivo = tipoArchivo;
    }

    public Long getId() {
        return id;
    }

    public SesionTutoria getSesion() {
        return sesion;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrlRecurso() {
        return urlRecurso;
    }

    public String getTipoArchivo() {
        return tipoArchivo;
    }
}
