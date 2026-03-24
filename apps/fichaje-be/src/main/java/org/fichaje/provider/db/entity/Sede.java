package org.fichaje.provider.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.List;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;

	@Column(nullable = false)
	private String nombre;

	@Column(length = 255)
	private String direccion;

	@Column(name = "codigo_postal", length = 10)
	private String codigoPostal;

	@Column(length = 100)
	private String localidad;

	@Column(length = 100)
	private String provincia;

	@Column(length = 100, columnDefinition = "VARCHAR(100) DEFAULT 'España'")
	private String pais;

	@Column(columnDefinition = "POINT")
	private Point ubicacion;

	@Column(columnDefinition = "boolean default true")
	private boolean activa;

	@OneToMany(mappedBy = "sede", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<SedeParametro> parametros;
}