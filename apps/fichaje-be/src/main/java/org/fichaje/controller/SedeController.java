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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/sedes")
public class SedeController {

    private final SedeService sedeService;
    private final SedeParametroService parametroService;
    private final JwtProvider jwtProvider;

    public SedeController(SedeService sedeService, SedeParametroService parametroService, JwtProvider jwtProvider) {
        this.sedeService = sedeService;
        this.parametroService = parametroService;
        this.jwtProvider = jwtProvider;
    }

    @Operation(summary = "Lista las sedes de la empresa asociada al token autenticado")
    @GetMapping("/mis-sedes")
    public ResponseEntity<List<SedeDTO>> listMisSedes(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token de autorización no válido");
        }

        String token = authHeader.replace("Bearer ", "").trim();
        if (!jwtProvider.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no válido o expirado");
        }

        Long empresaId = jwtProvider.getEmpresaIdFromToken(token);
        if (empresaId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El token no contiene empresaId");
        }

        return ResponseEntity.ok(sedeService.findByEmpresaId(empresaId));
    }

    @Operation(summary = "Lista las sedes de una empresa")
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<SedeDTO>> listSedes(@PathVariable Long empresaId) {
        return ResponseEntity.ok(sedeService.findByEmpresaId(empresaId));
    }

    @Operation(summary = "Añade una sede a una empresa")
    @PostMapping("/empresa/{empresaId}")
    public ResponseEntity<SedeDTO> addSede(@PathVariable Long empresaId, @Valid @RequestBody SedeDTO dto) {
        SedeDTO saved = sedeService.save(empresaId, dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{sedeId}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @Operation(summary = "Edita una sede")
    @PutMapping("/{sedeId}")
    public ResponseEntity<SedeDTO> updateSede(@PathVariable Long sedeId, @Valid @RequestBody SedeDTO dto) {
        return ResponseEntity.ok(sedeService.update(sedeId, dto));
    }

    @Operation(summary = "Activa una sede")
    @PatchMapping("/{sedeId}/activar")
    public ResponseEntity<?> activarSede(@PathVariable Long sedeId) {
        sedeService.activar(sedeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Desactiva una sede")
    @PatchMapping("/{sedeId}/desactivar")
    public ResponseEntity<?> desactivarSede(@PathVariable Long sedeId) {
        sedeService.desactivar(sedeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lista los parámetros de una sede")
    @GetMapping("/{sedeId}/parametros")
    public ResponseEntity<List<SedeParametroDTO>> listParametros(@PathVariable Long sedeId) {
        return ResponseEntity.ok(parametroService.findBySedeId(sedeId));
    }

    @Operation(summary = "Añade un parámetro a una sede")
    @PostMapping("/{sedeId}/parametros")
    public ResponseEntity<SedeParametroDTO> addParametro(@PathVariable Long sedeId, @Valid @RequestBody SedeParametroDTO dto) {
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
        return ResponseEntity.ok(parametroService.update(sedeId, parametroId, dto));
    }

    @Operation(summary = "Elimina un parámetro de una sede")
    @DeleteMapping("/{sedeId}/parametros/{parametroId}")
    public ResponseEntity<?> deleteParametro(@PathVariable Long sedeId, @PathVariable Long parametroId) {
        parametroService.delete(sedeId, parametroId);
        return ResponseEntity.noContent().build();
    }
}