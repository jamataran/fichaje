package org.fichaje.provider.db.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.fichaje.provider.db.entity.enums.EstadosPeticion;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "permisos")
public class Permiso {

	@Id
	@GeneratedValue
	private Long id;

//	@NotNull
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date inicio;
	@NotNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime horaInicio;

//	@NotNull
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date fin;
	@NotNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime horaFin;

	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dia;

	@NotNull
	private String descripcion;

//	@NotNull
//	@Enumerated(EnumType.STRING)
//	private EstadosPeticion estado = EstadosPeticion.PENDIENTE;

	@NotNull
	private String estado = EstadosPeticion.PENDIENTE.toString();

	@Column(columnDefinition = "boolean default false")
	private boolean aprobado;

	@JsonIgnoreProperties(value = { "password", "dni", "diasVacaciones", "horasGeneradas",
			"working", "enVacaciones", "deBaja",
			"admin", "roles", "fichajes", "incidencias", "permisos", "vacaciones" })
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;
}
