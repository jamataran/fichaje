package org.fichaje.provider.db.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dias_laborables")
public class DiaLaborable {

	@Id
	@GeneratedValue()
	private Long id;

//	@NotNull
//	@Temporal(TemporalType.DATE)
//	private Date dia;
	@NotNull
	@Column(unique = true)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dia;

//	@NotNull
//	@Temporal(TemporalType.TIME)
//	private Date horaInicio;
	@NotNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime horaInicio;

//	@NotNull
//	@Temporal(TemporalType.TIME)
//	private Date horaFin;
	@NotNull
	@JsonFormat(pattern = "HH:mm")
	private LocalTime horaFin;

	@JsonIgnoreProperties(value = { "dias", "handler", "hibernateLazyInitializer" })
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendarioId")
	private Calendario calendario;

}
