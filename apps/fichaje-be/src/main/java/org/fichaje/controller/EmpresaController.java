package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.service.EmpresaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/empresas")
public class EmpresaController
        extends CommonController<Empresa, EmpresaService> {

    @Operation(summary = "Crea una nueva empresa")
    @PostMapping
    public ResponseEntity<EmpresaDTO> newEmpresa(
            @RequestBody EmpresaDTO empresaDto) {
        EmpresaDTO empresaGuardada = service.save(empresaDto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(empresaGuardada.getId())
                .toUri();

        return ResponseEntity.created(location).body(empresaGuardada);
    }

    @Operation(summary = "Devuelve una lista de DTO de empresas")
    @GetMapping
    public ResponseEntity<Page<EmpresaDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @Operation(summary = "Edita una empresa")
    @PutMapping("/{id}")
    public ResponseEntity<?> editEmpresa(@RequestBody EmpresaDTO empresa, @PathVariable Long id) {

        return ResponseEntity.ok(service.update(empresa, id));
    }

    @Operation(summary = "Elimina una empresa")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}