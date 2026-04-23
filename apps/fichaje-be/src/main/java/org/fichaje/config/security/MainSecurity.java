package org.fichaje.config.security;

import org.fichaje.config.security.apikey.ApiKeyAuthenticationFilter;
import org.fichaje.config.security.jwt.JwtEntryPoint;
import org.fichaje.config.security.jwt.JwtTokenFilter;
import org.fichaje.config.security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class MainSecurity {

	private final String RRHH = "RRHH";
	private final String USER = "USER";
	private final String SUPER_ADMIN = "SUPER_ADMIN";

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	JwtEntryPoint jwtEntryPoint;

	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter();
	}

	@Bean
	public ApiKeyAuthenticationFilter apiKeyAuthenticationFilter() {
		return new ApiKeyAuthenticationFilter();
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		AuthenticationManagerBuilder builder =
				http.getSharedObject(AuthenticationManagerBuilder.class);
		builder.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder);
		return builder.build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(cors -> cors.configure(http))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/calendario/**").hasRole(RRHH)
						.requestMatchers("/dia/**").hasRole(RRHH)
						.requestMatchers("/fichaje/now").permitAll()
						.requestMatchers("/fichaje/pagesFiltered").hasRole(USER)
						.requestMatchers("/fichaje/listFiltered").hasRole(USER)
						.requestMatchers("/fichaje/**").hasRole(RRHH)
						.requestMatchers("/permiso/create").hasRole(USER)
						.requestMatchers("/permiso/pagesFiltered").hasRole(USER)
						.requestMatchers("/permiso/listFiltered").hasRole(USER)
						.requestMatchers("/permiso/**").hasRole(RRHH)
						.requestMatchers("/usuario/password/**").hasRole(USER)
						.requestMatchers("/usuario/miusuario").hasRole(USER)
						.requestMatchers("/usuario/**").hasAnyRole(RRHH, SUPER_ADMIN)
						.requestMatchers("/vacaciones/create").hasRole(USER)
						.requestMatchers("/vacaciones/pagesFiltered").hasRole(USER)
						.requestMatchers("/vacaciones/listFiltered").hasRole(USER)
						.requestMatchers("/vacaciones/**").hasRole(RRHH)
						.requestMatchers("/empresas/mi-empresa").hasRole(RRHH)
						.requestMatchers("/empresas/list").hasRole(SUPER_ADMIN)
						.requestMatchers("/empresas/**").hasAnyRole(SUPER_ADMIN, RRHH)
						.requestMatchers("/sedes/mis-sedes").hasRole(RRHH)
						.requestMatchers("/sedes/**").hasAnyRole(SUPER_ADMIN, RRHH)
						.requestMatchers("/auth/nuevo").hasRole(RRHH)
						.requestMatchers("/auth/login").permitAll()
						.requestMatchers("/apikey/**").hasRole(RRHH)
						.requestMatchers("/test/**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.anyRequest().authenticated()
				)
				.exceptionHandling(ex -> ex.authenticationEntryPoint(jwtEntryPoint))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		http.addFilterBefore(apiKeyAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
		http.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}