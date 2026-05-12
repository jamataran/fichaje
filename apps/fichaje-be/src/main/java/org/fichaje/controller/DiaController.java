package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.dto.entity.DiaDto;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.service.DiaLaborableService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de días laborables.
 */
@Slf4j
@RestController
@RequestMapping("/dia")
@Tag(name = "Día Laborable", description = "Endpoints para la gestión de días laborables dentro de los calendarios")
public class DiaController extends CommonController<DiaLaborable, DiaLaborableService> {

    public DiaController(DiaLaborableService service) {
        super(service);
    }

    @PostMapping("/create/{idCalendario}")
    @Operation(summary = "Crea un nuevo día laborable asociado a un calendario")
    public ResponseEntity<DiaDto> newDia(@RequestBody DiaDto diaDTO, @PathVariable Long idCalendario) {
        log.info("Recibida petición para crear día en calendario ID: {}", idCalendario);
        return service.createDia(diaDTO, idCalendario)
                .map(dia -> ResponseEntity.status(HttpStatus.CREATED).body(dia))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create_dias")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea múltiples días laborables a partir de una lista")
    public List<DiaDto> newListaDias(@RequestBody List<DiaDto> listaDias) {
        log.info("Recibida petición para crear {} días laborables", listaDias.size());
        return service.createDias(listaDias);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita un día laborable existente")
    public ResponseEntity<DiaDto> editDia(@PathVariable Long id, @RequestBody DiaDto editar) {
        log.info("Recibida petición para editar día ID: {}", id);
        return service.updateDia(id, editar)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
