package org.fichaje.provider.db.entity;

import jakarta.persistence.*;
import lombok.*;
import org.fichaje.provider.db.entity.enums.EmpresaParametroClave;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "empresa_parametros")
public class EmpresaParametro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	Empresa empresa;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 100)
	private EmpresaParametroClave clave;

	@Column(nullable = false, length = 255)
	private String valor;
}