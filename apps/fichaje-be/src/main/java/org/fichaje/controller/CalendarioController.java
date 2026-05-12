package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.dto.entity.CalendarioDto;
import org.fichaje.dto.entity.Mensaje;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.service.CalendarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de calendarios laborales.
 */
@Slf4j
@RestController
@RequestMapping("/calendario")
@Tag(name = "Calendario", description = "Endpoints para la gestión de calendarios laborales")
public class CalendarioController extends CommonController<Calendario, CalendarioService> {

    public CalendarioController(CalendarioService service) {
        super(service);
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un nuevo calendario")
    public Mensaje newCalendario(@RequestBody CalendarioDto calendarioDto) {
        log.info("Recibida petición para crear calendario: {}", calendarioDto.nombre());
        service.createCalendario(calendarioDto);
        return new Mensaje("Calendario creado correctamente");
    }

    @GetMapping("/list/dto")
    @Operation(summary = "Lista todos los calendarios en formato DTO")
    public List<CalendarioDto> listDto() {
        return service.getCalendariosDto();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edita un calendario existente")
    public ResponseEntity<CalendarioDto> editCalendario(@PathVariable Long id, @RequestBody CalendarioDto editar) {
        log.info("Recibida petición para editar calendario ID: {}", id);
        return service.updateCalendario(id, editar)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
