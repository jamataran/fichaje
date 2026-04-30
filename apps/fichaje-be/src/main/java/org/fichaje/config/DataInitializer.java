package org.fichaje.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.dto.entity.EmpresaCreateDTO;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.repository.EmpresaRepository;
import org.fichaje.provider.db.repository.UsuarioRepository;
import org.fichaje.service.EmpresaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final EmpresaService empresaService;
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${fichaje.default-empresa.nombre}")
    private String nombre;

    @Value("${fichaje.default-empresa.razon-social}")
    private String razonSocial;

    @Value("${fichaje.default-empresa.cif}")
    private String cif;

    @Value("${fichaje.default-empresa.email}")
    private String email;

    @Value("${fichaje.default-empresa.telefono}")
    private String telefono;

    @Value("${fichaje.default-empresa.direccion}")
    private String direccion;

    @Value("${fichaje.default-empresa.codigo-postal}")
    private String codigoPostal;

    @Value("${fichaje.default-empresa.localidad}")
    private String localidad;

    @Value("${fichaje.default-empresa.provincia}")
    private String provincia;

    @Value("${fichaje.default-empresa.pais}")
    private String pais;

    @Value("${fichaje.default-empresa.latitud}")
    private Double latitud;

    @Value("${fichaje.default-empresa.longitud}")
    private Double longitud;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Iniciando DataInitializer...");
        initializeDefaultEmpresa();
    }

    private void initializeDefaultEmpresa() {
        Optional<Empresa> empresaOpt = empresaRepository.findByCif(cif);

        if (empresaOpt.isEmpty()) {
            log.info("No se encontró la empresa por defecto con CIF {}. Creándola...", cif);

            EmpresaCreateDTO empresaDTO = EmpresaCreateDTO.builder()
                    .nombre(nombre)
                    .razonSocial(razonSocial)
                    .cif(cif)
                    .email(email)
                    .telefono(telefono)
                    .direccion(direccion)
                    .codigoPostal(codigoPostal)
                    .localidad(localidad)
                    .provincia(provincia)
                    .pais(pais)
                    .latitud(latitud)
                    .longitud(longitud)
                    .activa(true)
                    .build();

            try {
                empresaService.save(empresaDTO);
                log.info("Empresa por defecto creada con éxito.");

            } catch (Exception e) {
                log.error("Error al crear la empresa por defecto: {}", e.getMessage());
            }
        } else {
            log.info("La empresa por defecto con CIF {} ya existe.", cif);
        }
    }
}
