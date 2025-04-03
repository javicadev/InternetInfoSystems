package es.uma.informatica.sii.practica.jpa;

import java.util.Objects;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "CAMPO")
public class Campo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "nombre_campo")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO")
    private Tipo tipo;

    @Column(name = "DESCRIPCION")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "formulario", foreignKey = @ForeignKey(name = "FK_CAMPO_formulario"))
    private Formulario formulario;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campo campo = (Campo) o;
        return Objects.equals(id, campo.id) && Objects.equals(nombre, campo.nombre) && tipo == campo.tipo && Objects.equals(descripcion, campo.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, tipo, descripcion);
    }

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

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Formulario getFormulario() {
        return formulario;
    }

    public void setFormulario(Formulario formulario) {
        this.formulario = formulario;
    }
}
