package org.fichaje.service;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.fichaje.converter.EmpresaDtoConverter;
import org.fichaje.converter.SedeDtoConverter;
import org.fichaje.converter.UsuarioDtoConverter;
import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.dto.entity.EmpresaDTOWithoutSedes;
import org.fichaje.dto.entity.EmpresaParametroDTO;
import org.fichaje.dto.entity.SedeDTO;
import org.fichaje.dto.entity.UsuarioDTO;
import org.fichaje.exception.SedeNotFoundException;
import org.fichaje.exception.UsuarioNotFoundException;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.provider.db.entity.Sede;
import org.fichaje.provider.db.repository.SedeRepository;
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
    private final SedeRepository sedeRepository;
    private final UsuarioDtoConverter usuarioDtoConverter;
    private final SedeDtoConverter sedeDtoConverter;
    private final EmpresaDtoConverter empresaDtoConverter;

    public UsuarioService(PasswordEncoder passwordEncoder, RolService rolService, EmailService emailService, SedeRepository sedeRepository, UsuarioDtoConverter usuarioDtoConverter, SedeDtoConverter sedeDtoConverter, EmpresaDtoConverter empresaDtoConverter) {
        this.passwordEncoder = passwordEncoder;
        this.rolService = rolService;
        this.emailService = emailService;
        this.sedeRepository = sedeRepository;
        this.usuarioDtoConverter = usuarioDtoConverter;
        this.sedeDtoConverter = sedeDtoConverter;
        this.empresaDtoConverter = empresaDtoConverter;
    }

    public Optional<Usuario> findByNumero(String numero) {
        return repository.findByNumero(numero);
    }

    public Optional<Usuario> findSimpleByNumero(String numero) {
        return repository.findSimpleByNumero(numero);
    }

    @Transactional(readOnly = true)
    public boolean belongsToEmpresa(String numeroUsuario, Long empresaId) {
        if (numeroUsuario == null || empresaId == null) {
            return false;
        }

        Usuario usuario = repository.findByNumero(numeroUsuario).orElse(null);
        if (usuario == null || usuario.getEmpresas() == null) {
            return false;
        }

        return usuario.getEmpresas().stream()
                .anyMatch(empresa -> empresaId.equals(empresa.getId()));
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
        usuario.setSedes(new HashSet<>());
        usuario.setEmpresas(new HashSet<>());

        // Generar y establecer contraseña
        // FIXME
        final String password = documentoEmpleado;
        usuario.setPassword(passwordEncoder.encode(password));

        // Asignar roles
        Set<Rol> roles = assignRoles(usuarioDto.getRoles());
        usuario.setRoles(roles);

        // Guardar usuario
        usuario = save(usuario);

        // Asignar sede si se proporciona
        if (usuarioDto.getSedeId() != null) {
            addSede(usuario.getId(), usuarioDto.getSedeId());
        }

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
        usuario.setSedes(new HashSet<>());
        usuario.setEmpresas(new HashSet<>());

        // Usar contraseña proporcionada
        usuario.setPassword(passwordEncoder.encode(usuarioDto.getPassword()));

        // Asignar roles (incluyendo ROLE_ADMIN si aplica)
        Set<Rol> roles = assignRoles(usuarioDto.getRoles());
        usuario.setRoles(roles);

        return save(usuario);
    }

    /**
     * Asigna los roles correspondientes al usuario.
     *
     * @param rolesFromDto Lista de nombres de roles desde el DTO
     * @return Set de entidades Rol
     */
    private Set<Rol> assignRoles(List<String> rolesFromDto) {
        Set<Rol> roles = new HashSet<>();

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

    public UsuarioDTO addSede(Long usuarioId, Long sedeId) {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new SedeNotFoundException(sedeId));

        if (!usuario.getSedes().contains(sede)) {
            usuario.getSedes().add(sede);
        }

        Empresa empresa = sede.getEmpresa();
        if (!usuario.getEmpresas().contains(empresa)) {
            usuario.getEmpresas().add(empresa);
        }

        Usuario saved = save(usuario);
        return usuarioDtoConverter.inverseTransform(saved);
    }

    public void removeSede(Long usuarioId, Long sedeId) {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));

        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new SedeNotFoundException(sedeId));

        usuario.getSedes().remove(sede);

        boolean tieneOtraSedeEnEmpresa = usuario.getSedes().stream()
                .anyMatch(s -> s.getEmpresa().getId().equals(sede.getEmpresa().getId()));

        if (!tieneOtraSedeEnEmpresa) {
            usuario.getEmpresas().remove(sede.getEmpresa());
        }

        save(usuario);
    }

    public List<SedeDTO> listSedes(Long usuarioId) {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        return usuario.getSedes().stream()
                .map(sedeDtoConverter::todtoConverter)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmpresaDTO> findEmpresasByUsuarioId(Long usuarioId) {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        return usuario.getEmpresas().stream()
                .map(empresaDtoConverter::todtoConverter)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EmpresaDTOWithoutSedes> findEmpresasByUsuarioIdWithoutSedes(Long usuarioId) {
        Usuario usuario = repository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        return usuario.getEmpresas().stream()
                .map(empresa -> EmpresaDTOWithoutSedes.builder()
                        .id(empresa.getId())
                        .nombre(empresa.getNombre())
                        .razonSocial(empresa.getRazonSocial())
                        .cif(empresa.getCif())
                        .activa(empresa.isActiva())
                        .parametros(empresa.getParametros().stream()
                                .map(p -> EmpresaParametroDTO.builder()
                                        .clave(p.getClave())
                                        .valor(p.getValor())
                                        .build())
                                .collect(java.util.stream.Collectors.toSet()))
                        .build())
                .collect(java.util.stream.Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getMiUsuario(String numeroUsuario, Long empresaId) {
        Usuario usuario = repository.findByNumero(numeroUsuario)
                .orElseThrow(() -> new UsuarioNotFoundException(numeroUsuario));
        return usuarioDtoConverter.inverseTransformForSession(usuario, empresaId);
    }

}
