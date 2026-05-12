package org.fichaje.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.converter.CalendarioDtoConverter;
import org.fichaje.converter.DiaDtoConverter;
import org.fichaje.dto.entity.CalendarioDto;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.provider.db.repository.CalendarioRepository;
import org.fichaje.provider.db.specifications.CalendarioSpecifications;
import org.fichaje.util.SecurityUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de calendarios laborales.
 */
@Slf4j
@Service
@Transactional
public class CalendarioService extends CommonServiceImpl<Calendario, CalendarioRepository> {

    private final CalendarioSpecifications specifications;
    private final CalendarioDtoConverter dtoConverter;
    private final DiaDtoConverter diaDtoConverter;

    public CalendarioService(CalendarioRepository repository, 
                             CalendarioSpecifications specifications,
                             CalendarioDtoConverter dtoConverter, 
                             DiaDtoConverter diaDtoConverter) {
        super(repository);
        this.specifications = specifications;
        this.dtoConverter = dtoConverter;
        this.diaDtoConverter = diaDtoConverter;
    }

    @Transactional(readOnly = true)
    public Optional<Calendario> findByNombre(String nombre) {
        return repository.findByNombre(nombre);
    }

    @Transactional(readOnly = true)
    public List<CalendarioDto> getCalendariosDto() {
        Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
        boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

        Specification<Calendario> spec = specifications.buildSpecification(currentEmpresaId, isSuperAdmin);

        return filterAndList(spec).stream()
                .map(dtoConverter::inverseTransform)
                .toList();
    }

    public void createCalendario(CalendarioDto dto) {
        log.info("Creando nuevo calendario: {}", dto.nombre());
        Calendario calendario = dtoConverter.transform(dto);
        repository.save(calendario);
    }

    public Optional<CalendarioDto> updateCalendario(Long id, CalendarioDto dto) {
        log.info("Actualizando calendario ID: {}", id);
        return repository.findById(id).map(c -> {
            c.setYear(dto.year());
            c.setNombre(dto.nombre());
            c.setMinutosMasEntrada(dto.minutosMasEntrada());
            c.setMinutosMenosEntrada(dto.minutosMenosEntrada());
            c.setActive(dto.active());

            if (dto.dias() != null) {
                List<DiaLaborable> nuevosDias = dto.dias().stream()
                        .map(diaDto -> diaDtoConverter.transformWithCalendario(diaDto, c))
                        .toList();

                c.getDias().clear();
                c.getDias().addAll(nuevosDias);
            }

            Calendario saved = repository.save(c);
            return dtoConverter.inverseTransform(saved);
        });
    }

    @Transactional(readOnly = true)
    public Optional<Calendario> getActive() {
        return repository.findByActiveTrue();
    }

    public void refreshActiveCalendars() {
        int currentYear = LocalDate.now().getYear();
        log.info("Refrescando calendarios activos para el año: {}", currentYear);
        list().forEach(c -> {
            c.setActive(c.getYear() == currentYear);
            repository.save(c);
        });
    }
}
