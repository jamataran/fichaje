package org.fichaje.provider.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sede")
public class Sede {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;

	@Column(nullable = false)
	private String nombre;

	@Column(length = 255, nullable = false)
	private String email;

	@Column(length = 20)
	private String telefono;

	@Column(length = 255, nullable = false)
	private String direccion;

	@Column(name = "codigo_postal", length = 10, nullable = false)
	private String codigoPostal;

	@Column(length = 100, nullable = false)
	private String localidad;

	@Column(length = 100, nullable = false)
	private String provincia;

	@Column(length = 100, nullable = false, columnDefinition = "VARCHAR(100) DEFAULT 'España'")
	private String pais;

	@Column(columnDefinition = "POINT")
	private Point ubicacion;

	@Column(columnDefinition = "boolean default true")
	private boolean activa;

	@JsonIgnore
	@OneToMany(mappedBy = "sede", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<SedeParametro> parametros;

	@JsonIgnore
	@OneToMany(mappedBy = "sede", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Calendario> calendarios;
}