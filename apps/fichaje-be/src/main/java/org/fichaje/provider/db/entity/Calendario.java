package org.fichaje.provider.db.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "calendarios")
public class Calendario {

	@Id
	@GeneratedValue()
	private Long id;

	@NotNull
	@Column(unique = true)
	private String nombre;

	@Column(columnDefinition = "boolean default false")
	private boolean active;

	@NotNull
	@Column(unique = true)
	private int year;

	@NotNull
	private int minutosMasEntrada;

	@NotNull
	private int minutosMenosEntrada;

	@JsonIgnoreProperties(value = { "calendario" })
	@OneToMany(mappedBy = "calendario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<DiaLaborable> dias;

	@JsonIgnoreProperties(value = { "calendarios" })
	@ManyToOne
	@JoinColumn(name = "empresa_id")
	private Empresa empresa;
}
