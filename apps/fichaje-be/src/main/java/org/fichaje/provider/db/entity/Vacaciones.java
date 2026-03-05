package org.fichaje.provider.db.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
public class Vacaciones {

	@Id
	@GeneratedValue
	private Long id;

//	@NotNull
//	@Temporal(TemporalType.DATE)
//	private Date inicio;
	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate inicio;

//	@NotNull
//	@Temporal(TemporalType.DATE)
//	private Date fin;
	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate fin;

//	@Column(columnDefinition = "boolean default false")
	private Boolean consumidas;

	@NotNull
//	@Enumerated(EnumType.STRING)
//	private EstadosPeticion estado = EstadosPeticion.PENDIENTE;
	private String estado = EstadosPeticion.PENDIENTE.toString();

	@Column(columnDefinition = "boolean default false")
	private Boolean aprobado;

	@JsonIgnoreProperties(value = { "password", "dni", "diasVacaciones", "horasGeneradas",
			"working", "enVacaciones", "deBaja",
			"admin", "roles", "fichajes", "incidencias", "permisos", "vacaciones" })
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;
}
