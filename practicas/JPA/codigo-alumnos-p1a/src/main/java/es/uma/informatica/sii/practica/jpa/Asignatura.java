package es.uma.informatica.sii.practica.jpa;

import java.util.Set;
import jakarta.persistence.*;

@Entity
@Table(name = "ASIGNATURA")

public class Asignatura {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "CREDITOS")
    private Integer creditos;

    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Calificacion> calificaciones; // Relación con estudiantes

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCreditos() {
        return creditos;
    }

    public void setCreditos(Integer creditos) {
        this.creditos = creditos;
    }

    public Set<Calificacion> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(Set<Calificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }
}