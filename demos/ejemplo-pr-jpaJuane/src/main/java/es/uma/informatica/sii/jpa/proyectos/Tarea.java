package es.uma.informatica.sii.jpa.proyectos;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class Tarea {

	@Embeddable
	public static class TareaId implements Serializable {
		private static final long serialVersionUID = -7636200172590268843L;

		@Column(name = "PROYECTO_ID")
		private Long proyectoId;

		@Column(name = "ID")
		private Long id;

		public Long getProyectoId() {
			return proyectoId;
		}

		public void setProyectoId(Long proyectoId) {
			this.proyectoId = proyectoId;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((proyectoId == null) ? 0 : proyectoId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null || getClass() != obj.getClass()) return false;
			TareaId other = (TareaId) obj;
			return id.equals(other.id) && proyectoId.equals(other.proyectoId);
		}
	}

	@EmbeddedId
	private TareaId id;

	@ManyToOne
	@JoinColumn(name = "PROYECTO_ID", nullable = false, insertable = false, updatable = false)
	private Proyecto proyecto;

	@Temporal(TemporalType.DATE)
	private Date fechaInicio;

	@Temporal(TemporalType.DATE)
	private Date fechaFin;

	private Double esfuerzo;
	private String nombre;
	private String descripcion;

	public TareaId getId() {
		return id;
	}

	public void setId(TareaId id) {
		this.id = id;
	}

	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Double getEsfuerzo() {
		return esfuerzo;
	}

	public void setEsfuerzo(Double esfuerzo) {
		this.esfuerzo = esfuerzo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
}
