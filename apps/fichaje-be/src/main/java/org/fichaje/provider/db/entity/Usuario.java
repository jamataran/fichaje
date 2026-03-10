package org.fichaje.provider.db.entity;

import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@NotBlank
	private String email;

	@JsonIgnore
	@NotNull
	@NotBlank
	private String password;

	@NotNull
	@NotBlank
	@Column(unique = true)
	private String numero;

	@NotNull
	@NotBlank
	private String nombreEmpleado;

	@NotNull
	@NotBlank
	@Column(unique = true)
	private String dni;

	private Integer diasVacaciones;

	private Double horasGeneradas;

//	@Enumerated(EnumType.STRING)
//	private EstadosTrabajador estado = EstadosTrabajador.FUERA_TRABAJO;

//	@Column(columnDefinition = "boolean default false")
	private Boolean working;

//	@Column(columnDefinition = "boolean default false")
	private Boolean enVacaciones;

//	@Column(columnDefinition = "boolean default false")
	private Boolean deBaja;

	@JsonIgnore
	private Boolean admin;

	@JsonIgnore
	@NotNull
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_rol", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
//	private Set<Rol> roles = new HashSet<>();
	private List<Rol> roles;

	@JsonIgnore
//	@JsonIgnoreProperties(value = { "usuario" })
	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Fichaje> fichajes;

	private String ultimoFichaje;

	@JsonIgnore
//	@JsonIgnoreProperties(value = { "usuario" })
	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Incidencia> incidencias;

	@JsonIgnore
//	@JsonIgnoreProperties(value = { "usuario" })
	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Permiso> permisos;

	@JsonIgnore
//	@JsonIgnoreProperties(value = { "usuario" })
	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Vacaciones> vacaciones;

	@JsonIgnore
	@NotNull
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "usuario_empresa", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "empresa_id"))
	private List<Empresa> empresas;

//	@ManyToOne
//	@JoinColumn(name = "calendario_id")
//	private Calendario calendario;
}
