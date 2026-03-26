package org.fichaje.service;

import org.fichaje.converter.SedeDtoConverter;
import org.fichaje.dto.entity.SedeDTO;
import org.fichaje.exception.BusinessException;
import org.fichaje.exception.EmpresaNotFoundException;
import org.fichaje.exception.SedeNotFoundException;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.provider.db.entity.Sede;
import org.fichaje.provider.db.repository.EmpresaRepository;
import org.fichaje.provider.db.repository.SedeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SedeService {

    private static final Logger log = LoggerFactory.getLogger(SedeService.class);

    private final SedeRepository repository;
    private final EmpresaRepository empresaRepository;
    private final SedeDtoConverter dtoConverter;

    public SedeService(SedeRepository repository, EmpresaRepository empresaRepository, SedeDtoConverter dtoConverter) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.dtoConverter = dtoConverter;
    }

    @Transactional(readOnly = true)
    public List<SedeDTO> findByEmpresaId(Long empresaId) {
        log.info("Consultando sedes de la empresa con ID: {}", empresaId);
        empresaRepository.findById(empresaId).orElseThrow(() -> new EmpresaNotFoundException(empresaId));
        return repository.findByEmpresaId(empresaId).stream()
                .map(dtoConverter::todtoConverter)
                .toList();
    }

    @Transactional
    public SedeDTO save(Long empresaId, SedeDTO dto) {
        log.info("Creando sede '{}' para la empresa con ID: {}", dto.getNombre(), empresaId);
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EmpresaNotFoundException(empresaId));

        Sede sede = dtoConverter.transform(dto);
        sede.setEmpresa(empresa);

        Sede saved = repository.save(sede);
        log.info("Sede '{}' creada con éxito para la empresa con ID: {}", dto.getNombre(), empresaId);
        return dtoConverter.todtoConverter(saved);
    }

    @Transactional
    public SedeDTO update(Long sedeId, SedeDTO dto) {
        log.info("Actualizando sede con ID: {}", sedeId);

        Sede sede = repository.findById(sedeId)
                .orElseThrow(() -> new SedeNotFoundException(sedeId));

        sede.setNombre(dto.getNombre());
        sede.setDireccion(dto.getDireccion());
        sede.setCodigoPostal(dto.getCodigoPostal());
        sede.setLocalidad(dto.getLocalidad());
        sede.setProvincia(dto.getProvincia());
        sede.setPais(dto.getPais());
        sede.setActiva(dto.isActiva());

        Sede updated = repository.save(sede);
        log.info("Sede con ID: {} actualizada correctamente", sedeId);
        return dtoConverter.todtoConverter(updated);
    }

    @Transactional
    public void activar(Long sedeId) {
        log.info("Activando sede con ID: {}", sedeId);
        Sede sede = repository.findById(sedeId)
                .orElseThrow(() -> new SedeNotFoundException(sedeId));

        if (sede.isActiva()) {
            throw new BusinessException("La sede con id " + sedeId + " ya está activa");
        }

        sede.setActiva(true);
        repository.save(sede);
        log.info("Sede con ID: {} activada correctamente", sedeId);
    }

    @Transactional
    public void desactivar(Long sedeId) {
        log.info("Desactivando sede con ID: {}", sedeId);
        Sede sede = repository.findById(sedeId)
                .orElseThrow(() -> new SedeNotFoundException(sedeId));

        if (!sede.isActiva()) {
            throw new BusinessException("La sede con id " + sedeId + " ya está desactivada");
        }

        sede.setActiva(false);
        repository.save(sede);
        log.info("Sede con ID: {} desactivada correctamente", sedeId);
    }
}