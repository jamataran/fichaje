package org.fichaje.provider.db.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.fichaje.config.security.enums.RolNombre;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Rol {
	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	@Enumerated(EnumType.STRING)
	private RolNombre rolNombre;
}
