package org.fichaje.provider.db.entity;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UsuarioPrincipal implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String numero;
	private String nombre;
	private String password;
	private Long id;
	private Long empresaId;
	private Collection<? extends GrantedAuthority> authorities;

	public static UsuarioPrincipal build(Usuario usuario) {

		List<GrantedAuthority> authorities = usuario.getRoles().stream()
				.map(rol -> (GrantedAuthority) new SimpleGrantedAuthority(rol
						.getRolNombre().name()))
				.toList();

		return UsuarioPrincipal.builder()
				.numero(usuario.getNumero())
				.nombre(usuario.getNombreEmpleado())
				.password(usuario.getPassword())
				.id(usuario.getId())
				.authorities(authorities)
				.build();
	}

	@Override
	public String getUsername() {
		return numero;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
