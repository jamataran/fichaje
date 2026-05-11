package org.fichaje.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.converter.DiaDtoConverter;
import org.fichaje.dto.entity.DiaDto;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.provider.db.repository.CalendarioRepository;
import org.fichaje.provider.db.repository.DiaLaborableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio para la gestión de días laborables.
 */
@Slf4j
@Service
@Transactional
public class DiaLaborableService extends CommonServiceImpl<DiaLaborable, DiaLaborableRepository> {

    private final DiaDtoConverter dtoConverter;
    private final CalendarioRepository calendarioRepository;

    public DiaLaborableService(DiaLaborableRepository repository, 
                               DiaDtoConverter dtoConverter, 
                               CalendarioRepository calendarioRepository) {
        super(repository);
        this.dtoConverter = dtoConverter;
        this.calendarioRepository = calendarioRepository;
    }

    @Transactional(readOnly = true)
    public boolean existsByDia(LocalDate dia) {
        return repository.existsByDia(dia);
    }

    @Transactional(readOnly = true)
    public Optional<DiaLaborable> getByDiaInActiveCalendar(LocalDate dia, Calendario calendario) {
        return repository.getByDiaActive(dia, calendario);
    }

    public List<DiaLaborable> saveAll(List<DiaLaborable> lista) {
        return repository.saveAll(lista);
    }

    public Optional<DiaDto> createDia(DiaDto diaDTO, Long idCalendario) {
        log.info("Creando día laborable para calendario ID: {}", idCalendario);
        return calendarioRepository.findById(idCalendario).map(calendario -> {
            DiaLaborable dia = dtoConverter.transformWithCalendario(diaDTO, calendario);
            DiaLaborable saved = repository.save(dia);
            return dtoConverter.inverseTransform(saved);
        });
    }

    public List<DiaDto> createDias(List<DiaDto> listaDias) {
        log.info("Creando múltiple días laborables ({} días)", listaDias.size());
        List<DiaLaborable> dias = listaDias.stream().map(d -> {
            Calendario cal = null;
            if (d.calendarioNombre() != null) {
                cal = calendarioRepository.findByNombre(d.calendarioNombre()).orElse(null);
            }
            return dtoConverter.transformWithCalendario(d, cal);
        }).toList();

        return repository.saveAll(dias).stream()
                .map(dtoConverter::inverseTransform)
                .toList();
    }

    public Optional<DiaDto> updateDia(Long id, DiaDto editar) {
        log.info("Actualizando día laborable ID: {}", id);
        return repository.findById(id).map(d -> {
            d.setDia(editar.dia());
            d.setHoraInicio(editar.horaInicio());
            d.setHoraFin(editar.horaFin());
            DiaLaborable saved = repository.save(d);
            return dtoConverter.inverseTransform(saved);
        });
    }
}
