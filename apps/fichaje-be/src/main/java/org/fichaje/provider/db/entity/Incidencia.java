package org.fichaje.provider.db.entity;

import java.time.LocalDate;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "incidencias")
public class Incidencia {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@NotNull
	private String descripcion;

	@NotNull
	private String resumen;

//	@NotNull
//	@Temporal(TemporalType.DATE)
//	private Date dia;
	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dia;

	@Column(columnDefinition = "boolean default false")
	private Boolean resuelta;

	private String explicacion;

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
