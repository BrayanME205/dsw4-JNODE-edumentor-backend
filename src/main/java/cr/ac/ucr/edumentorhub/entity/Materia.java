package cr.ac.ucr.edumentorhub.entity;

import org.hibernate.annotations.Check;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Asignatura del catálogo. La administra el COORDINADOR. */
@Entity
@Table(name = "materia")
@Check(constraints = "creditos BETWEEN 1 AND 10")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_materia", nullable = false, unique = true, length = 10)
    private String codigoMateria;

    @Column(name = "nombre_materia", nullable = false, length = 150)
    private String nombreMateria;

    @Column(name = "carrera", nullable = false, length = 100)
    private String carrera;

    @Column(name = "creditos", nullable = false)
    private int creditos;

    // Requerido por JPA
    protected Materia() {
    }

    public Materia(String codigoMateria, String nombreMateria, String carrera, int creditos) {
        this.codigoMateria = codigoMateria;
        this.nombreMateria = nombreMateria;
        this.carrera = carrera;
        this.creditos = creditos;
    }

    public Long getId() {
        return id;
    }

    public String getCodigoMateria() {
        return codigoMateria;
    }

    public void setCodigoMateria(String codigoMateria) {
        this.codigoMateria = codigoMateria;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public void setNombreMateria(String nombreMateria) {
        this.nombreMateria = nombreMateria;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }
}
