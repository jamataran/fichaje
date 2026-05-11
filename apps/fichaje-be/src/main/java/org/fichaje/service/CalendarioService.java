package org.fichaje.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.fichaje.converter.CalendarioDtoConverter;
import org.fichaje.converter.DiaDtoConverter;
import org.fichaje.dto.entity.CalendarioDto;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.provider.db.repository.CalendarioRepository;
import org.fichaje.provider.db.specifications.CalendarioSpecifications;
import org.fichaje.util.SecurityUtils;

@Service
@Transactional
public class CalendarioService
		extends CommonServiceImpl<Calendario, CalendarioRepository> {

	private final CalendarioSpecifications specifications;
	private final CalendarioDtoConverter dtoConverter;
	private final DiaDtoConverter diaDtoConverter;

	public CalendarioService(CalendarioRepository repository, CalendarioSpecifications specifications,
			CalendarioDtoConverter dtoConverter, DiaDtoConverter diaDtoConverter) {
		super(repository);
		this.specifications = specifications;
		this.dtoConverter = dtoConverter;
		this.diaDtoConverter = diaDtoConverter;
	}

	public Optional<Calendario> findByNombre(String nombre) {
		return repository.findByNombre(nombre);
	}

	public List<CalendarioDto> getCalendariosDto() {
		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		Specification<Calendario> spec = specifications.buildSpecification(currentEmpresaId, isSuperAdmin);

		return filterAndList(spec).stream()
				.map(dtoConverter::inverseTransform)
				.collect(Collectors.toList());
	}

	public Calendario createCalendario(CalendarioDto dto) {
		Calendario calendario = dtoConverter.transform(dto);
		if (calendario.getDias() != null) {
			calendario.getDias().forEach(d -> d.setCalendario(calendario));
		}
		return repository.save(calendario);
	}

	public Optional<Calendario> updateCalendario(Long id, CalendarioDto dto) {
		return repository.findById(id).map(c -> {
			c.setYear(dto.getYear());
			c.setNombre(dto.getNombre());
			c.setMinutosMasEntrada(dto.getMinutosMasEntrada());
			c.setMinutosMenosEntrada(dto.getMinutosMenosEntrada());
			c.setActive(dto.isActive());

			if (dto.getDias() != null) {
				// Limpiar días antiguos y añadir los nuevos desde el DTO
				// Dada la configuración actual de JPA (orphanRemoval = true), podemos simplemente actualizar la lista
				List<DiaLaborable> nuevosDias = dto.getDias().stream()
						.map(diaDto -> diaDtoConverter.transformWithCalendario(diaDto, c))
						.collect(Collectors.toList());

				c.getDias().clear();
				c.getDias().addAll(nuevosDias);
			}

			return repository.save(c);
		});
	}

	public Optional<Calendario> getActive() {
		return repository.findByActiveTrue();
	}

	public void refreshActiveCalendars() {
		int currentYear = LocalDate.now().getYear();
		list().forEach(c -> {
			c.setActive(c.getYear() == currentYear);
			save(c);
		});
	}

}
