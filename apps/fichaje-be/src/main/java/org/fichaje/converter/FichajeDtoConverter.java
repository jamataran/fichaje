package org.fichaje.converter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.FichajeDto;
import org.fichaje.dto.entity.FichajeDtoReqRes;
import org.fichaje.provider.db.entity.Fichaje;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.enums.TipoFichaje;
import org.fichaje.service.FichajeService;
import org.fichaje.service.UsuarioService;

import org.fichaje.provider.db.repository.EmpresaRepository;
import org.fichaje.util.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FichajeDtoConverter {

	@Autowired
	private UsuarioService service;
	@Autowired
	private FichajeService fichajeService;
	@Autowired
	private EmpresaRepository empresaRepository;

	public FichajeDto inverseTransform(Fichaje f) {

		return null;

	}

	public Fichaje transform(FichajeDtoReqRes dto) {
		Fichaje f = new Fichaje();
		f.setHora(dto.getHora());
		f.setDia(dto.getDia());
		Usuario usuario = service
				.findByNumero(dto.getNumeroUsuario())
				.orElse(null);
		f.setUsuario(usuario);
		f.setTipo(dto.getTipo());
		f.setOrigen(dto.getOrigen());

		return f;
	}

	@Transactional
	public FichajeDtoReqRes fichar(FichajeDtoReqRes fichajeDto) {

		fichajeDto.setHora(LocalTime.now());
		fichajeDto.setDia(LocalDate.now());

		Fichaje fichaje = transform(fichajeDto);

		Usuario usuario = fichaje.getUsuario();
		if (usuario != null) {
			// Aislamiento Multi-empresa
			Long empresaId = SecurityUtils.getCurrentEmpresaId();
			if (empresaId != null) {
				empresaRepository.findById(empresaId).ifPresent(fichaje::setEmpresa);
			} else if (usuario.getEmpresas() != null && !usuario.getEmpresas().isEmpty()) {
				fichaje.setEmpresa(usuario.getEmpresas().iterator().next());
			}

			boolean workingState = usuario.getWorking() != null && usuario.getWorking();
			if (workingState)
				fichaje.setTipo(TipoFichaje.SALIDA.toString());
			else
				fichaje.setTipo(TipoFichaje.ENTRADA.toString());

			usuario.setWorking(!workingState);
			StringBuilder sb = new StringBuilder();
			sb.append(fichaje.getDia());
			sb.append(" ");
			DateTimeFormatter myTime = DateTimeFormatter.ofPattern("HH:mm:ss");
			sb.append(myTime.format(fichaje.getHora()));
			sb.append(" - ");
			sb.append(fichaje.getTipo());
			usuario.setUltimoFichaje(sb.toString());

			service.save(usuario);
			fichaje.setUsuario(usuario);
			fichajeService.save(fichaje);

			fichajeDto.setNombreUsuario(usuario.getNombreEmpleado());
			fichajeDto.setHora(fichaje.getHora());
			fichajeDto.setTipo(usuario.getWorking() ? "entrada" : "salida");

			return fichajeDto;
		}
		return null;

	}

}
