package org.fichaje.provider.db.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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

	@Column(unique = true, nullable = false)
	private String cif;

	@Column(columnDefinition = "boolean default false")
	private boolean activa;

	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Calendario> calendarios = new ArrayList<>();

	@ManyToMany(mappedBy = "empresas")
	private List<Usuario> usuarios = new ArrayList<>();
}