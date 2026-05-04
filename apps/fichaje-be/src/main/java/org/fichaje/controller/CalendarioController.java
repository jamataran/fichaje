package org.fichaje.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.converter.CalendarioDtoConverter;
import org.fichaje.dto.entity.CalendarioDto;
import org.fichaje.dto.entity.Mensaje;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.service.CalendarioService;
import org.fichaje.service.DiaLaborableService;

import io.swagger.v3.oas.annotations.Operation;

import org.fichaje.util.SecurityUtils;
import org.fichaje.provider.db.specifications.CalendarioSpecifications;
import org.springframework.data.jpa.domain.Specification;

@RestController
@RequestMapping("/calendario")
//@CrossOrigin(origins = "http://localhost:4200")
public class CalendarioController
		extends CommonController<Calendario, CalendarioService> {

	@Autowired
	CalendarioDtoConverter dtoConverter;
	@Autowired
	DiaController diaController;
	@Autowired
	DiaLaborableService diaService;
	@Autowired
	CalendarioSpecifications specifications;

	@Operation(summary = "Crea un nuevo calendario")
	@PostMapping("/create")
	public ResponseEntity<?> newCalendario(
			@RequestBody CalendarioDto calendarioDto) {
//		Calendario calendario = new Calendario();
//		calendario.setNombre(calendarioDto.getNombre());

		service.save(dtoConverter.transform(calendarioDto));
		diaController.newListaDias(calendarioDto.getDias());

//		return ResponseEntity.created(null).build();
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new Mensaje("Calendario creado"));

//		return ResponseEntity.status(HttpStatus.CREATED)
//				.body(service.save(dtoConverter.transform(calendarioDto)));
	}

	@Operation(summary = "Devuelve una lista de DTO de calendarios")
	@GetMapping("/list/dto")
	public ResponseEntity<List<CalendarioDto>> listDto() {
		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		Specification<Calendario> spec = Specification.where(null);
		if (currentEmpresaId != null) {
			spec = spec.and(specifications.hasEmpresa(currentEmpresaId));
		} else if (!isSuperAdmin) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(service.filterAndList(spec).stream()
						.map(c -> dtoConverter.inverseTransform(c))
						.collect(Collectors.toList()));

	}

	@Override
	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return service.findById(id).map(c -> {
			Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
			boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

			if (!isSuperAdmin && currentEmpresaId != null && c.getSede() != null &&
					!c.getSede().getEmpresa().getId().equals(currentEmpresaId)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			return ResponseEntity.ok(c);
		}).orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Edita un calendario")
	@PutMapping("/{id}")
	public ResponseEntity<?> editCalendario(@RequestBody Calendario editar,
			@PathVariable Long id) {

		return service.findById(id).map(c -> {
			Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
			boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

			if (!isSuperAdmin && currentEmpresaId != null && c.getSede() != null &&
					!c.getSede().getEmpresa().getId().equals(currentEmpresaId)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			c.setId(editar.getId());
			c.setYear(editar.getYear());
			c.setNombre(editar.getNombre());
			c.setMinutosMasEntrada(editar.getMinutosMasEntrada());
			c.setMinutosMenosEntrada(editar.getMinutosMenosEntrada());
			c.setActive(editar.isActive());
			editar.getDias().forEach(d -> {
				DiaLaborable dia = diaService.findById(d.getId()).get();
				dia.setDia(d.getDia());
				dia.setHoraInicio(d.getHoraInicio());
				dia.setHoraFin(d.getHoraFin());
				diaService.save(dia);
			});

			return ResponseEntity.ok(service.save(c));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

}
