package org.fichaje.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.fichaje.dto.entity.ChartDataDto;
import org.fichaje.dto.interfaces.IUsuarioDtoEstadistica;
import org.fichaje.provider.db.entity.Permiso;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.repository.PermisoRepository;

@Service
@Transactional
public class PermisoService extends CommonServiceImpl<Permiso, PermisoRepository> {

	public List<Permiso> findByUsuarioAprobado(Usuario usuario) {
		return repository.findByUsuarioAndAprobadoTrue(usuario);
	}

	public ChartDataDto numberOfPermisosLast12Months() {

		ChartDataDto result = new ChartDataDto();

		List<LocalDate> datesList = new ArrayList<>();
		List<Integer> countList = new ArrayList<>();

		LocalDate dateNow = LocalDate.now();

		for (int i = 12; i >= 1; i--) {

			LocalDate dateRequest = dateNow.minusMonths(i);

			Integer count = repository.countNumberOfPermisosOfMonth(
					dateRequest.getMonthValue(),
					dateRequest.getYear());

			datesList.add(dateRequest);
			countList.add(count);

		}
		result.setCantidades(countList);
		result.setFechas(datesList);

		return result;
	}

	public List<IUsuarioDtoEstadistica> numberOfPermisosPerUserLast12Months() {

		return repository.numberOfPermisosPerUserLast12Months(LocalDate.now());

	}

}
