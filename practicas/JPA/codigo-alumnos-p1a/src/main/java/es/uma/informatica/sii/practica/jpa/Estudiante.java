package es.uma.informatica.sii.practica.jpa;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "ESTUDIANTE")
public class Estudiante {

    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NOMBRE" )
    private String nombre;

    @Column(name = "EMAIL")
    private String email;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Calificacion> calificaciones; // Relación con asignaturas

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Calificacion> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(Set<Calificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }
}