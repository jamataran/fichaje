package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.fichaje.dto.entity.*;
import org.fichaje.config.security.jwt.JwtProvider;
import org.fichaje.provider.db.entity.UsuarioPrincipal;
import org.fichaje.service.EmpresaParametroService;
import org.fichaje.service.EmpresaService;
import org.fichaje.service.SedeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService service;
    private final EmpresaParametroService parametroService;
    private final JwtProvider jwtProvider;
    private final SedeService sedeService;

    public EmpresaController(EmpresaService service, EmpresaParametroService parametroService, JwtProvider jwtProvider, SedeService sedeService) {
        this.parametroService = parametroService;
        this.service = service;
        this.jwtProvider = jwtProvider;
        this.sedeService = sedeService;
    }

    @Operation(summary = "Crea una nueva empresa")
    @PostMapping
    public ResponseEntity<EmpresaDTO> newEmpresa(@Valid @RequestBody EmpresaCreateDTO empresaDto) {
        EmpresaDTO empresaGuardada = service.save(empresaDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(empresaGuardada.getId())
                .toUri();

        return ResponseEntity.created(location).body(empresaGuardada);
    }

    @Operation(summary = "Devuelve una lista paginada de empresas")
    @GetMapping
    public ResponseEntity<Page<EmpresaDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Devuelve una lista de todas las empresas")
    @GetMapping("/list")
    public ResponseEntity<List<EmpresaDTO>> listAll() {
        return ResponseEntity.ok(service.findAllList());
    }

    @Operation(summary = "Edita una empresa existente")
    @PutMapping("/{id}")
    public ResponseEntity<EmpresaDTO> editEmpresa(@PathVariable Long id, @Valid @RequestBody EmpresaDTO empresa) {
        return ResponseEntity.of(service.update(empresa, id));
    }

    @Operation(summary = "Elimina una empresa")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Activa una empresa")
    @PatchMapping("/{id}/activar")
    public ResponseEntity<?> activarEmpresa(@PathVariable Long id) {
        service.activar(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lista los parámetros de una empresa")
    @GetMapping("/{id}/parametros")
    public ResponseEntity<List<EmpresaParametroDTO>> listParametros(@PathVariable Long id) {
        return ResponseEntity.ok(parametroService.findByEmpresaId(id));
    }

    @Operation(summary = "Añade un parámetro a una empresa")
    @PostMapping("/{id}/parametros")
    public ResponseEntity<EmpresaParametroDTO> addParametro(@PathVariable Long id, @Valid @RequestBody EmpresaParametroDTO dto) {
        EmpresaParametroDTO saved = parametroService.save(id, dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{parametroId}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @Operation(summary = "Edita un parámetro de una empresa")
    @PutMapping("/{id}/parametros/{parametroId}")
    public ResponseEntity<EmpresaParametroDTO> updateParametro(@PathVariable Long id, @PathVariable Long parametroId, @Valid @RequestBody EmpresaParametroDTO dto) {
        return ResponseEntity.ok(parametroService.update(id, parametroId, dto));
    }

    @Operation(summary = "Elimina un parámetro de una empresa")
    @DeleteMapping("/{id}/parametros/{parametroId}")
    public ResponseEntity<?> deleteParametro(@PathVariable Long id, @PathVariable Long parametroId) {
        parametroService.delete(id, parametroId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Lista las sedes de una empresa")
    @GetMapping("/{empresaId}/sedes")
    @PreAuthorize("hasRole('SUPER_ADMIN') or (hasRole('RRHH') and #empresaId == authentication.principal.empresaId)")
    public ResponseEntity<List<SedeDTO>> listSedes(@PathVariable Long empresaId) {
        return ResponseEntity.ok(sedeService.findByEmpresaId(empresaId));
    }

    @Operation(summary = "Añade una sede a una empresa")
    @PostMapping("/{empresaId}/sedes")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<SedeDTO> addSede(@PathVariable Long empresaId, @Valid @RequestBody SedeDTO dto) {
        SedeDTO saved = sedeService.save(empresaId, dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{sedeId}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }
}