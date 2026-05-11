package org.fichaje.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.fichaje.dto.entity.ChartDataDto;
import org.fichaje.dto.entity.IncidenciaDtoEdit;
import org.fichaje.dto.entity.IncidenciaDtoFilter;
import org.fichaje.dto.interfaces.ITopIncidencias;
import org.fichaje.dto.interfaces.IUsuarioDtoEstadistica;
import org.fichaje.provider.db.entity.Incidencia;
import org.fichaje.provider.db.repository.IncidenciaRepository;
import org.fichaje.provider.db.specifications.IncidenciaSpecifications;
import org.fichaje.util.SecurityUtils;
import org.fichaje.converter.IncidenciaDtoConverter;

@Service
@Transactional
public class IncidenciaService extends CommonServiceImpl<Incidencia, IncidenciaRepository> {

	@Autowired
	private IncidenciaSpecifications specifications;

	@Autowired
	private IncidenciaDtoConverter dtoConverter;

	public List<Incidencia> findByUser(Long id) {
		return repository.findByUsuarioId(id);
	}

	public Page<Incidencia> getFilteredPages(IncidenciaDtoFilter dto, int page, int size, String order, boolean asc) {
		Specification<Incidencia> spec = buildCompleteSpecification(dto);
		return pagesAndSpec(spec, PageRequest.of(page, size, asc ? Sort.by(order).ascending() : Sort.by(order).descending()));
	}

	public List<Incidencia> getFilteredList(IncidenciaDtoFilter dto) {
		Specification<Incidencia> spec = buildCompleteSpecification(dto);
		return filterAndList(spec);
	}

	private Specification<Incidencia> buildCompleteSpecification(IncidenciaDtoFilter dto) {
		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		String currentUserNumber = SecurityUtils.getCurrentUserNumber();
		boolean isRrhh = SecurityUtils.isRRHH();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		// Si NO es RRHH ni SuperAdmin, filtrar por su número de usuario
		if (!isRrhh && !isSuperAdmin) {
			dto.setUsuarioNumero(currentUserNumber);
		}

		return specifications.buildSpecification(dto, currentEmpresaId, isSuperAdmin);
	}

	public Optional<Incidencia> updateIncidencia(Long id, IncidenciaDtoEdit editar) {
		return repository.findById(id).map(d -> {
			Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
			boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

			if (!isSuperAdmin && currentEmpresaId != null && d.getEmpresa() != null &&
					!d.getEmpresa().getId().equals(currentEmpresaId)) {
				return null;
			}

			dtoConverter.transformEdit(d, editar);
			return repository.save(d);
		});
	}

	public ChartDataDto getStatsLast12Months() {
		Long currentEmpresaId = getValidatedEmpresaId();
		ChartDataDto result = new ChartDataDto();
		List<LocalDate> datesList = new ArrayList<>();
		List<Integer> countList = new ArrayList<>();
		LocalDate dateNow = LocalDate.now();

		for (int i = 12; i >= 1; i--) {
			LocalDate dateRequest = dateNow.minusMonths(i);
			Integer count = repository.countNumberOfIncidenciasOfMonth(
					dateRequest.getMonthValue(),
					dateRequest.getYear(),
					currentEmpresaId);
			datesList.add(dateRequest);
			countList.add(count);
		}
		result.setCantidades(countList);
		result.setFechas(datesList);
		return result;
	}

	public List<IUsuarioDtoEstadistica> getStatsPerUserLast12Months() {
		return repository.numberOfIncidenciasPerUserLast12Months(getValidatedEmpresaId());
	}

	public List<ITopIncidencias> getTopStatsLast12Months() {
		return repository.topIncidenciasLast12Months(getValidatedEmpresaId());
	}

	private Long getValidatedEmpresaId() {
		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
		if (currentEmpresaId == null && !isSuperAdmin) {
			throw new SecurityException("No se ha podido validar la empresa del usuario");
		}
		return currentEmpresaId;
	}

}
