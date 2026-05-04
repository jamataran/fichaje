package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.fichaje.config.security.jwt.JwtProvider;
import org.fichaje.dto.entity.SedeDTO;
import org.fichaje.dto.entity.SedeParametroDTO;
import org.fichaje.service.SedeParametroService;
import org.fichaje.service.SedeService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import org.fichaje.util.SecurityUtils;
import org.fichaje.provider.db.entity.Sede;
import org.fichaje.provider.db.repository.SedeRepository;
import org.fichaje.exception.SedeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/sedes")
public class SedeController {

    private final SedeService sedeService;
    private final SedeParametroService parametroService;
    private final JwtProvider jwtProvider;
    
    @Autowired
    private SedeRepository sedeRepository;

    public SedeController(SedeService sedeService, SedeParametroService parametroService, JwtProvider jwtProvider) {
        this.sedeService = sedeService;
        this.parametroService = parametroService;
        this.jwtProvider = jwtProvider;
    }

    private void validateSedeAccess(Long sedeId) {
        Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

        if (!isSuperAdmin && currentEmpresaId != null) {
            Sede sede = sedeRepository.findById(sedeId)
                    .orElseThrow(() -> new SedeNotFoundException(sedeId));
            if (!sede.getEmpresa().getId().equals(currentEmpresaId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a esta sede");
            }
        }
    }

    @Operation(summary = "Obtiene una sede por su ID")
    @GetMapping("/{sedeId}")
    public ResponseEntity<SedeDTO> getSede(@PathVariable Long sedeId) {
        validateSedeAccess(sedeId);
        return ResponseEntity.ok(sedeService.findById(sedeId));
    }

    @Operation(summary = "Edita una sede")
    @PutMapping("/{sedeId}")
    public ResponseEntity<SedeDTO> updateSede(@PathVariable Long sedeId, @Valid @RequestBody SedeDTO dto) {
        validateSedeAccess(sedeId);
        return ResponseEntity.ok(sedeService.update(sedeId, dto));
    }

    @Operation(summary = "Activa una sede")
    @PatchMapping("/{sedeId}/activar")
    public ResponseEntity<?> activarSede(@PathVariable Long sedeId) {
        validateSedeAccess(sedeId);
        sedeService.activar(sedeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Desactiva una sede")
    @PatchMapping("/{sedeId}/desactivar")
    public ResponseEntity<?> desactivarSede(@PathVariable Long sedeId) {
        validateSedeAccess(sedeId);
        sedeService.desactivar(sedeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lista los parámetros de una sede")
    @GetMapping("/{sedeId}/parametros")
    public ResponseEntity<List<SedeParametroDTO>> listParametros(@PathVariable Long sedeId) {
        validateSedeAccess(sedeId);
        return ResponseEntity.ok(parametroService.findBySedeId(sedeId));
    }

    @Operation(summary = "Añade un parámetro a una sede")
    @PostMapping("/{sedeId}/parametros")
    public ResponseEntity<SedeParametroDTO> addParametro(@PathVariable Long sedeId, @Valid @RequestBody SedeParametroDTO dto) {
        validateSedeAccess(sedeId);
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
        validateSedeAccess(sedeId);
        return ResponseEntity.ok(parametroService.update(sedeId, parametroId, dto));
    }

    @Operation(summary = "Elimina un parámetro de una sede")
    @DeleteMapping("/{sedeId}/parametros/{parametroId}")
    public ResponseEntity<?> deleteParametro(@PathVariable Long sedeId, @PathVariable Long parametroId) {
        validateSedeAccess(sedeId);
        parametroService.delete(sedeId, parametroId);
        return ResponseEntity.noContent().build();
    }
}