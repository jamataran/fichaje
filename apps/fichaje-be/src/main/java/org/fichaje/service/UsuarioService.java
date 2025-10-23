package org.fichaje.service;

import java.util.*;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.fichaje.dto.entity.UsuarioDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.fichaje.config.security.enums.RolNombre;
import org.fichaje.provider.db.entity.Rol;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.repository.UsuarioRepository;
import org.fichaje.provider.mail.EmailService;

@Service
@Transactional
public class UsuarioService extends CommonServiceImpl<Usuario, UsuarioRepository> {

    private final PasswordEncoder passwordEncoder;
    private final RolService rolService;
    private final EmailService emailService;

    public UsuarioService(PasswordEncoder passwordEncoder, RolService rolService, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.rolService = rolService;
        this.emailService = emailService;
    }

    public Optional<Usuario> findByNumero(String numero) {
        return repository.findByNumero(numero);
    }

    public List<Usuario> findByRoles(Set<Rol> roles) {
        return repository.findByRolesIn(roles);
    }

    public boolean existsByNumero(String numero) {
        return repository.existsByNumero(numero);
    }

    public boolean existsByDni(String dni) {
        return repository.existsByDni(dni);
    }

    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    /**
     * Crea un nuevo usuario con todas sus validaciones, asignación de roles y envío de credenciales.
     *
     * @param usuarioDto DTO con los datos del nuevo usuario
     * @return Usuario creado y guardado
     */
    public Usuario createNewUser(UsuarioDTO usuarioDto) {
        // Construir el usuario con datos básicos
        final String documentoEmpleado = usuarioDto.getDni().toUpperCase(Locale.ROOT);

        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setNumero(documentoEmpleado);
        usuario.setNombreEmpleado(usuarioDto.getNombreEmpleado());
        usuario.setDni(documentoEmpleado);

        // Inicializar valores por defecto
        usuario.setDiasVacaciones(0);
        usuario.setHorasGeneradas(0.0);
        usuario.setEnVacaciones(false);
        usuario.setDeBaja(false);
        usuario.setWorking(false);
        usuario.setAdmin(false);

        // Generar y establecer contraseña
        // FIXME
        final String password = documentoEmpleado;
        usuario.setPassword(passwordEncoder.encode(password));

        // Asignar roles
        List<Rol> roles = assignRoles(usuarioDto.getRoles());
        usuario.setRoles(roles);

        // Guardar usuario
        usuario = save(usuario);

        // Enviar credenciales por email
        sendCredentialsEmail(usuario, password);

        return usuario;
    }

    /**
     * Crea un nuevo usuario administrador con contraseña proporcionada.
     *
     * @param usuarioDto DTO con los datos del nuevo usuario administrador
     * @return Usuario administrador creado y guardado
     */
    public Usuario createNewAdminUser(UsuarioDTO usuarioDto) {
        // Construir el usuario con datos básicos
        Usuario usuario = new Usuario();
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setNumero(usuarioDto.getNumero());
        usuario.setNombreEmpleado(usuarioDto.getNombreEmpleado());
        usuario.setDni(usuarioDto.getDni());

        // Inicializar valores por defecto
        usuario.setDiasVacaciones(0);
        usuario.setHorasGeneradas(0.0);
        usuario.setEnVacaciones(false);
        usuario.setDeBaja(false);
        usuario.setWorking(false);
        usuario.setAdmin(true);

        // Usar contraseña proporcionada
        usuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));

        // Asignar roles (incluyendo ROLE_ADMIN si aplica)
        List<Rol> roles = assignRoles(usuarioDto.getRoles());
        usuario.setRoles(roles);

        return save(usuario);
    }

    /**
     * Asigna los roles correspondientes al usuario.
     *
     * @param rolesFromDto Lista de nombres de roles desde el DTO
     * @return Lista de entidades Rol
     */
    private List<Rol> assignRoles(List<String> rolesFromDto) {
        List<Rol> roles = new ArrayList<>();

        // Rol base: ROLE_USER
        roles.add(rolService.findByRolNombre(RolNombre.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Rol ROLE_USER no encontrado")));

        // Roles adicionales basados en el DTO
        if (rolesFromDto != null) {
            if (rolesFromDto.contains("rrhh")) {
                roles.add(rolService.findByRolNombre(RolNombre.ROLE_RRHH)
                        .orElseThrow(() -> new RuntimeException("Rol ROLE_RRHH no encontrado")));
            }
        }

        return roles;
    }

    /**
     * Genera una contraseña aleatoria de 10 caracteres alfanuméricos.
     *
     * @return Contraseña generada
     */
    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    /**
     * Envía las credenciales del usuario por email.
     *
     * @param usuario  Usuario creado
     * @param password Contraseña sin cifrar
     */
    private void sendCredentialsEmail(Usuario usuario, String password) {
        StringBuilder body = new StringBuilder("Sus credenciales de acceso a Fichaje son:\n\n");
        body.append(String.format("Número de empleado: %s \n", usuario.getNumero()));
        body.append(String.format("Contraseña: %s \n\n", password));
        body.append("Puede cambiar su contraseña desde la aplicación.");

        emailService.sendEmail(
                usuario.getEmail(),
                "Usuario creado en Fichaje",
                body.toString());
    }

}
