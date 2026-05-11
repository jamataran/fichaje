package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.fichaje.dto.entity.SedeDTO;
import org.fichaje.dto.entity.SedeParametroDTO;
import org.fichaje.service.SedeParametroService;
import org.fichaje.service.SedeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import org.fichaje.util.SecurityUtils;
import org.fichaje.provider.db.repository.SedeRepository;
import org.fichaje.exception.SedeNotFoundException;

@RestController
@RequestMapping("/sedes")
public class SedeController {

    private final SedeService sedeService;
    private final SedeParametroService parametroService;
    private final SedeRepository sedeRepository;

    public SedeController(SedeService sedeService, SedeParametroService parametroService, SedeRepository sedeRepository) {
        this.sedeService = sedeService;
        this.parametroService = parametroService;
        this.sedeRepository = sedeRepository;
    }

    @Operation(summary = "Obtiene una sede por su ID")
    @GetMapping("/{sedeId}")
    public ResponseEntity<SedeDTO> getSede(@PathVariable Long sedeId) {
        SecurityUtils.checkTenantAccessById(sedeId, sedeRepository, () -> new SedeNotFoundException(sedeId));
        return ResponseEntity.ok(sedeService.findById(sedeId));
    }

    @Operation(summary = "Edita una sede")
    @PutMapping("/{sedeId}")
    public ResponseEntity<SedeDTO> updateSede(@PathVariable Long sedeId, @Valid @RequestBody SedeDTO dto) {
        SecurityUtils.checkTenantAccessById(sedeId, sedeRepository, () -> new SedeNotFoundException(sedeId));
        return ResponseEntity.ok(sedeService.update(sedeId, dto));
    }

    @Operation(summary = "Activa una sede")
    @PatchMapping("/{sedeId}/activar")
    public ResponseEntity<Void> activarSede(@PathVariable Long sedeId) {
        SecurityUtils.checkTenantAccessById(sedeId, sedeRepository, () -> new SedeNotFoundException(sedeId));
        sedeService.activar(sedeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Desactiva una sede")
    @PatchMapping("/{sedeId}/desactivar")
    public ResponseEntity<Void> desactivarSede(@PathVariable Long sedeId) {
        SecurityUtils.checkTenantAccessById(sedeId, sedeRepository, () -> new SedeNotFoundException(sedeId));
        sedeService.desactivar(sedeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lista los parámetros de una sede")
    @GetMapping("/{sedeId}/parametros")
    public ResponseEntity<List<SedeParametroDTO>> listParametros(@PathVariable Long sedeId) {
        SecurityUtils.checkTenantAccessById(sedeId, sedeRepository, () -> new SedeNotFoundException(sedeId));
        return ResponseEntity.ok(parametroService.findBySedeId(sedeId));
    }

    @Operation(summary = "Añade un parámetro a una sede")
    @PostMapping("/{sedeId}/parametros")
    public ResponseEntity<SedeParametroDTO> addParametro(@PathVariable Long sedeId, @Valid @RequestBody SedeParametroDTO dto) {
        SecurityUtils.checkTenantAccessById(sedeId, sedeRepository, () -> new SedeNotFoundException(sedeId));
        SedeParametroDTO saved = parametroService.save(sedeId, dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{parametroId}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @Operation(summary = "Edita un parámetro de una sede")
    @PutMapping("/{sedeId}/parametros/{parametroId}")
    public ResponseEntity<SedeParametroDTO> updateParametro(@PathVariable Long sedeId, @PathVariable Long parametroId, @Valid @RequestBody SedeParametroDTO dto) {
        SecurityUtils.checkTenantAccessById(sedeId, sedeRepository, () -> new SedeNotFoundException(sedeId));
        return ResponseEntity.ok(parametroService.update(sedeId, parametroId, dto));
    }

    @Operation(summary = "Elimina un parámetro de una sede")
    @DeleteMapping("/{sedeId}/parametros/{parametroId}")
    public ResponseEntity<Void> deleteParametro(@PathVariable Long sedeId, @PathVariable Long parametroId) {
        SecurityUtils.checkTenantAccessById(sedeId, sedeRepository, () -> new SedeNotFoundException(sedeId));
        parametroService.delete(sedeId, parametroId);
        return ResponseEntity.noContent().build();
    }
}
