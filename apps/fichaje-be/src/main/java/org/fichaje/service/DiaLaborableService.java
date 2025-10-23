package org.fichaje.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.provider.db.repository.DiaLaborableRepository;
import org.fichaje.service.common.CommonServiceImpl;

@Service
@Transactional
public class DiaLaborableService
		extends CommonServiceImpl<DiaLaborable, DiaLaborableRepository> {

	public boolean existsByDia(Date dia) {
		return repository.existsByDia(dia);
	}

//	public Optional<DiaLaborable> getByDiaInActiveCalendar(Date dia, Calendario calendario) {
	public Optional<DiaLaborable> getByDiaInActiveCalendar(LocalDate dia,
			Calendario calendario) {
		return repository.getByDiaActive(dia, calendario);
	}

	public List<DiaLaborable> saveAll(List<DiaLaborable> lista) {
		return repository.saveAll(lista);
	}

}
