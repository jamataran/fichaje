package org.fichaje.provider.db.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empresa")
public class Empresa {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column()
	private String nombre;

	@NotNull
	@Column(unique = true)
	private String cif;

	@Column(columnDefinition = "boolean default false")
	private boolean activa;

	@JsonIgnoreProperties(value = { "empresa" })
	@OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Calendario> calendarios;

	@JsonIgnore
	@ManyToMany(mappedBy = "empresas")
	private List<Usuario> usuarios = new ArrayList<>();
}
