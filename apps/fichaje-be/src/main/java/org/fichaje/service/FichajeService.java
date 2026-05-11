package org.fichaje.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.fichaje.dto.entity.FichajeDto;
import org.fichaje.dto.entity.FichajeDtoReqRes;
import org.fichaje.provider.db.entity.Fichaje;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.enums.TipoFichaje;
import org.fichaje.provider.db.repository.FichajeRepository;
import org.fichaje.provider.db.repository.EmpresaRepository;
import org.fichaje.provider.db.specifications.FichajeSpecifications;
import org.fichaje.util.SecurityUtils;

@Service
@Transactional
public class FichajeService
		extends CommonServiceImpl<Fichaje, FichajeRepository> {

	private final UsuarioService usuarioService;
	private final EmpresaRepository empresaRepository;
	private final FichajeSpecifications specifications;

	public FichajeService(FichajeRepository repository, UsuarioService usuarioService, EmpresaRepository empresaRepository, FichajeSpecifications specifications) {
		super(repository);
		this.usuarioService = usuarioService;
		this.empresaRepository = empresaRepository;
		this.specifications = specifications;
	}

	public List<Fichaje> findByUsuarioAndDia(Usuario usuario, LocalDate dia) {
		return repository.findByUsuarioAndDiaOrderByHora(usuario, dia);
	}

	public FichajeDtoReqRes fichar(FichajeDtoReqRes fichajeDto) {
		fichajeDto.setHora(LocalTime.now());
		fichajeDto.setDia(LocalDate.now());

		Usuario usuario = usuarioService.findByNumero(fichajeDto.getNumeroUsuario()).orElse(null);
		if (usuario == null) {
			return null;
		}

		Fichaje fichaje = new Fichaje();
		fichaje.setHora(fichajeDto.getHora());
		fichaje.setDia(fichajeDto.getDia());
		fichaje.setUsuario(usuario);
		fichaje.setOrigen(fichajeDto.getOrigen());

		// Aislamiento Multi-empresa
		Long empresaId = SecurityUtils.getCurrentEmpresaId();
		if (empresaId != null) {
			empresaRepository.findById(empresaId).ifPresent(fichaje::setEmpresa);
		} else if (usuario.getEmpresas() != null && !usuario.getEmpresas().isEmpty()) {
			fichaje.setEmpresa(usuario.getEmpresas().iterator().next());
		}

		boolean workingState = Optional.ofNullable(usuario.getWorking()).orElse(false);
		fichaje.setTipo(workingState ? TipoFichaje.SALIDA.toString() : TipoFichaje.ENTRADA.toString());

		usuario.setWorking(!workingState);
		String formattedTime = fichaje.getHora().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
		usuario.setUltimoFichaje(fichaje.getDia() + " " + formattedTime + " - " + fichaje.getTipo());

		usuarioService.save(usuario);
		repository.save(fichaje);

		fichajeDto.setNombreUsuario(usuario.getNombreEmpleado());
		fichajeDto.setTipo(usuario.getWorking() ? "entrada" : "salida");

		return fichajeDto;
	}

	public Page<Fichaje> getFilteredPages(FichajeDto dto, int page, int size, String order, boolean asc) {
		Specification<Fichaje> spec = buildSpecification(dto);
		return pagesAndSpec(spec, PageRequest.of(page, size, asc ? Sort.by(order).ascending() : Sort.by(order).descending()));
	}

	public List<Fichaje> getFilteredList(FichajeDto dto) {
		Specification<Fichaje> spec = buildSpecification(dto);
		return filterAndList(spec);
	}

	private Specification<Fichaje> buildSpecification(FichajeDto dto) {
		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		String currentUserNumber = SecurityUtils.getCurrentUserNumber();
		boolean isRrhh = SecurityUtils.isRRHH();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		// Si NO es RRHH ni SuperAdmin, solo puede ver sus propios fichajes
		if (!isRrhh && !isSuperAdmin) {
			dto.setNumeroUsuario(currentUserNumber);
		}

		Specification<Fichaje> spec = (root, query, cb) -> cb.conjunction();

		if (dto.getNombreUsuario() != null) {
			spec = spec.and(specifications.nombreUsuarioContains(dto.getNombreUsuario()));
		}
		if (dto.getNumeroUsuario() != null) {
			spec = spec.and(specifications.numeroUsuarioContains(dto.getNumeroUsuario()));
		}
		if (dto.getTipo() != null) {
			spec = spec.and(specifications.tipoContains(dto.getTipo()));
		}
		if (dto.getHoraDesde() != null) {
			spec = spec.and(specifications.horaMayorQue(dto.getHoraDesde()));
		}
		if (dto.getHoraHasta() != null) {
			spec = spec.and(specifications.horaMenorQue(dto.getHoraHasta()));
		}
		if (dto.getDiaDesde() != null) {
			spec = spec.and(specifications.diaMayorQue(dto.getDiaDesde()));
		}
		if (dto.getDiaHasta() != null) {
			spec = spec.and(specifications.diaMenorQue(dto.getDiaHasta()));
		}

		// Aislamiento Multi-empresa
		if (currentEmpresaId != null) {
			spec = spec.and(specifications.hasEmpresa(currentEmpresaId));
		} else if (!isSuperAdmin) {
			// Return a specification that matches nothing if not superadmin and no empresa
			return (root, query, cb) -> cb.disjunction();
		}

		return spec;
	}
}
