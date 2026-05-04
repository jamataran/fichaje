package org.fichaje.provider.db.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fichajes")
public class Fichaje implements TenantEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

//	@NotNull
//	@Temporal(TemporalType.TIMESTAMP)
//	private Date tiempoFichaje;
	@NotNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime hora;

	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dia;

	@JsonIgnoreProperties(value = { "password", "dni", "diasVacaciones", "horasGeneradas", "working", "enVacaciones",
			"deBaja", "admin", "roles", "fichajes", "incidencias", "permisos", "vacaciones" })
	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

//	@NotNull
//	@Enumerated(EnumType.STRING)
//	private TipoFichaje tipo;

	@NotNull
	private String tipo;

	@NotNull
	private String origen;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;
}
