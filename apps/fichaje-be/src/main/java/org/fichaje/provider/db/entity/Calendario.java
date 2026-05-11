package org.fichaje.provider.db.entity;

import java.util.List;

import jakarta.persistence.*;

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
public class Calendario implements TenantEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
	@JoinColumn(name = "sede_id")
	private Sede sede;

	@Override
	public Empresa getEmpresa() {
		return (sede != null) ? sede.getEmpresa() : null;
	}

	@Override
	public void setEmpresa(Empresa empresa) {
		// La empresa se establece a través de la sede.
		// Este metodo se deja vacío o se podría implementar si fuera necesario vincular a una sede por defecto.
	}
}
