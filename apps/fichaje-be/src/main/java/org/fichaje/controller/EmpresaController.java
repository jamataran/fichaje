package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.dto.entity.EmpresaParametroDTO;
import org.fichaje.service.EmpresaParametroService;
import org.fichaje.service.EmpresaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaService service;
    private final EmpresaParametroService parametroService;

    public EmpresaController(EmpresaService service, EmpresaParametroService parametroService) {
        this.parametroService = parametroService;
        this.service = service;
    }

    @Operation(summary = "Crea una nueva empresa")
    @PostMapping
    public ResponseEntity<EmpresaDTO> newEmpresa(@Valid @RequestBody EmpresaDTO empresaDto) {
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
}