package org.fichaje.provider.db.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Set;
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

	@JsonIgnore
	@ManyToMany(mappedBy = "empresas")
	private List<Usuario> usuarios;

	@JsonIgnore
	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<EmpresaParametro> parametros;

	@JsonIgnore
	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Sede> sedes;
}