package es.uma.informatica.sii.practica.jpa;

import jakarta.persistence.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "CALIFICACION")
public class Calificacion {

    public static class CalificacionId {
        private Long estudiante;
        private Long asignatura;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            CalificacionId that = (CalificacionId) o;
            return Objects.equals(estudiante, that.estudiante) && Objects.equals(asignatura, that.asignatura);
        }

        @Override
        public int hashCode() {
            return Objects.hash(estudiante, asignatura);
        }
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "ESTUDIANTE", foreignKey = @ForeignKey(name = "FK_CALIFICACION_ESTUDIANTE"))
    private Estudiante estudiante;

    @Id
    @ManyToOne
    @JoinColumn(name = "ASIGNATURA", foreignKey = @ForeignKey(name = "FK_CALIFICACION_ASIGNATURA"))
    private Asignatura asignatura;

    @Column(name = "NOTA")
    private Double nota; // Nota obtenida en la asignatura

    @Temporal(TemporalType.DATE)
    @Column(name = "FECHA_ULTIMO_EXAMEN")
    private Date fechaExamen;

    // Getters y Setters

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Date getFechaExamen() {
        return fechaExamen;
    }

    public void setFechaExamen(Date fechaExamen) {
        this.fechaExamen = fechaExamen;
    }
}