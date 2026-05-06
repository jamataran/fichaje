package org.fichaje.provider.db.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "empresa_id", nullable = false)
	private Empresa empresa;

	@Column(nullable = false, length = 100)
	private String clave;

	@Column(nullable = false, length = 255)
	private String valor;
}