package org.fichaje.provider.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empresa")
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String nombre;

	@Column(name = "razon_social")
	private String razonSocial;

	@Column(unique = true, nullable = false, length = 20)
	private String cif;

	@Column(columnDefinition = "boolean default false")
	private boolean activa;

	@Column(length = 255)
	private String email;

	@Column(length = 20)
	private String telefono;

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

	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Calendario> calendarios;

	@ManyToMany(mappedBy = "empresas")
	private List<Usuario> usuarios;

	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EmpresaParametro> parametros;

	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Sede> sedes;
}