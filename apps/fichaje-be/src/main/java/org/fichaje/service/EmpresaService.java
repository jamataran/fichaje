package org.fichaje.service;

import org.fichaje.converter.EmpresaDtoConverter;
import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.exception.BusinessException;
import org.fichaje.exception.EmpresaNotFoundException;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.provider.db.repository.EmpresaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class EmpresaService {

    private static final Logger log = LoggerFactory.getLogger(EmpresaService.class);

    private final EmpresaDtoConverter dtoConverter;
    private final EmpresaRepository repository;
    private final CIFValidator cifValidator;

    public EmpresaService(EmpresaDtoConverter dtoConverter, EmpresaRepository repository, CIFValidator cifValidator) {
        this.dtoConverter = dtoConverter;
        this.repository = repository;
        this.cifValidator = cifValidator;
    }

    @Transactional(readOnly = true)
    public Page<EmpresaDTO> findAll(Pageable pageable) {
        log.info("Consultando listado paginado de empresas");
        return repository.findAll(pageable).map(dtoConverter::todtoConverter);
    }

    @Transactional
    public EmpresaDTO save(EmpresaDTO empresaDTO) {
        log.info("Iniciando proceso de creación para la empresa con CIF: {}", empresaDTO.getCif());

        validarCif(empresaDTO.getCif());

        Empresa empresa = dtoConverter.transform(empresaDTO);
        Empresa savedEmpresa = repository.save(empresa);

        log.info("Empresa guardada con éxito con ID: {}", savedEmpresa.getId());
        return dtoConverter.todtoConverter(savedEmpresa);
    }

    @Transactional
    public Optional<EmpresaDTO> update(EmpresaDTO empresaDTO, Long id) {
        Empresa empresaExistente = repository.findById(id).orElseThrow(() -> new EmpresaNotFoundException(id));

        log.info("Actualizando datos de la empresa con ID: {}", id);

        validarCif(empresaDTO.getCif());

        return repository.findById(id).map(empresa -> {
            empresaExistente.setNombre(empresaDTO.getNombre());
            empresaExistente.setCif(empresaDTO.getCif());
            empresaExistente.setActiva(empresaDTO.isActiva());

            Empresa updatedEmpresa = repository.save(empresaExistente);
            log.info("Empresa con ID: {} actualizada correctamente", id);
            return dtoConverter.todtoConverter(updatedEmpresa);
        });
    }

    @Transactional
    public void delete(Long id) {
        log.info("Solicitud para desactivar la empresa con ID: {}", id);

        Empresa empresaExistente = repository.findById(id).orElseThrow(() -> new EmpresaNotFoundException(id));

        if (!empresaExistente.isActiva()) {
            throw new BusinessException("La empresa con id " + id + " ya está desactivada");
        }

        empresaExistente.setActiva(false);
        repository.save(empresaExistente);
        log.info("Empresa con ID: {} desactivada correctamente", id);
    }

    @Transactional
    public void activar(Long id) {
        log.info("Solicitud para activar la empresa con ID: {}", id);

        Empresa empresaExistente = repository.findById(id).orElseThrow(() -> new EmpresaNotFoundException(id));

        if (empresaExistente.isActiva()) {
            throw new BusinessException("La empresa con id " + id + " ya está activa");
        }

        empresaExistente.setActiva(true);
        repository.save(empresaExistente);
        log.info("Empresa con ID: {} activada correctamente", id);
    }

    private void validarCif(String cif) {
        if (!cifValidator.isValid(cif)) {
            log.warn("Validación fallida: Se intentó registrar un CIF con formato inválido ({})", cif);
            throw new IllegalArgumentException("El CIF proporcionado tiene un formato inválido.");
        }
    }
}