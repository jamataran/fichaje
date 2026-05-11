package org.fichaje.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.fichaje.converter.DiaDtoConverter;
import org.fichaje.dto.entity.DiaDto;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.provider.db.repository.CalendarioRepository;
import org.fichaje.provider.db.repository.DiaLaborableRepository;

@Service
@Transactional
public class DiaLaborableService
		extends CommonServiceImpl<DiaLaborable, DiaLaborableRepository> {

	private final DiaDtoConverter dtoConverter;
	private final CalendarioRepository calendarioRepository;

	public DiaLaborableService(DiaLaborableRepository repository, DiaDtoConverter dtoConverter, CalendarioRepository calendarioRepository) {
		super(repository);
		this.dtoConverter = dtoConverter;
		this.calendarioRepository = calendarioRepository;
	}

	public boolean existsByDia(LocalDate dia) {
		return repository.existsByDia(dia);
	}

	public Optional<DiaLaborable> getByDiaInActiveCalendar(LocalDate dia,
			Calendario calendario) {
		return repository.getByDiaActive(dia, calendario);
	}

	public List<DiaLaborable> saveAll(List<DiaLaborable> lista) {
		return repository.saveAll(lista);
	}

	public Optional<DiaLaborable> createDia(DiaDto diaDTO, Long idCalendario) {
		return calendarioRepository.findById(idCalendario).map(calendario -> {
			DiaLaborable dia = dtoConverter.transformWithCalendario(diaDTO, calendario);
			return repository.save(dia);
		});
	}

	public List<DiaLaborable> createDias(List<DiaDto> listaDias) {
		List<DiaLaborable> dias = listaDias.stream().map(d -> {
			Calendario cal = null;
			if (d.getCalendarioNombre() != null) {
				cal = calendarioRepository.findByNombre(d.getCalendarioNombre()).orElse(null);
			}
			return dtoConverter.transformWithCalendario(d, cal);
		}).collect(Collectors.toList());

		return repository.saveAll(dias);
	}

	public Optional<DiaLaborable> updateDia(Long id, DiaDto editar) {
		return repository.findById(id).map(d -> {
			d.setDia(editar.getDia());
			d.setHoraInicio(editar.getHoraInicio());
			d.setHoraFin(editar.getHoraFin());
			return repository.save(d);
		});
	}

}
