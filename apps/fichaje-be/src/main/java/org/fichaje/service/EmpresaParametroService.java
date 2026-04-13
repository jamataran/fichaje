package org.fichaje.service;

import org.fichaje.converter.EmpresaParametroDtoConverter;
import org.fichaje.dto.entity.EmpresaParametroDTO;
import org.fichaje.exception.EmpresaNotFoundException;
import org.fichaje.exception.EmpresaParametroNotFoundException;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.provider.db.entity.EmpresaParametro;
import org.fichaje.provider.db.repository.EmpresaParametroRepository;
import org.fichaje.provider.db.repository.EmpresaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaParametroService {

    private static final Logger log = LoggerFactory.getLogger(EmpresaParametroService.class);

    private final EmpresaParametroRepository repository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaParametroDtoConverter dtoConverter;

    public EmpresaParametroService(EmpresaParametroRepository repository, EmpresaRepository empresaRepository, EmpresaParametroDtoConverter dtoConverter) {
        this.repository = repository;
        this.empresaRepository = empresaRepository;
        this.dtoConverter = dtoConverter;
    }

    @Transactional(readOnly = true)
    public List<EmpresaParametroDTO> findByEmpresaId(Long empresaId) {
        log.info("Consultando parámetros de la empresa con ID: {}", empresaId);
        empresaRepository.findById(empresaId).orElseThrow(() -> new EmpresaNotFoundException(empresaId));
        return repository.findByEmpresaId(empresaId).stream()
                .map(dtoConverter::todtoConverter)
                .toList();
    }

    @Transactional
    public EmpresaParametroDTO save(Long empresaId, EmpresaParametroDTO dto) {
        log.info("Creando parámetro '{}' para la empresa con ID: {}", dto.getClave(), empresaId);
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EmpresaNotFoundException(empresaId));

        EmpresaParametro parametro = dtoConverter.transform(dto);
        parametro.setEmpresa(empresa);

        EmpresaParametro saved = repository.save(parametro);
        log.info("Parámetro '{}' creado con éxito para la empresa con ID: {}", dto.getClave(), empresaId);
        return dtoConverter.todtoConverter(saved);
    }

    @Transactional
    public EmpresaParametroDTO update(Long empresaId, Long parametroId, EmpresaParametroDTO dto) {
        log.info("Actualizando parámetro con ID: {} de la empresa con ID: {}", parametroId, empresaId);
        empresaRepository.findById(empresaId).orElseThrow(() -> new EmpresaNotFoundException(empresaId));

        EmpresaParametro parametro = repository.findById(parametroId)
                .orElseThrow(() -> new EmpresaParametroNotFoundException(parametroId));

        parametro.setClave(dto.getClave());
        parametro.setValor(dto.getValor());

        EmpresaParametro updated = repository.save(parametro);
        log.info("Parámetro con ID: {} actualizado correctamente", parametroId);
        return dtoConverter.todtoConverter(updated);
    }

    @Transactional
    public void delete(Long empresaId, Long parametroId) {
        log.info("Eliminando parámetro con ID: {} de la empresa con ID: {}", parametroId, empresaId);
        empresaRepository.findById(empresaId).orElseThrow(() -> new EmpresaNotFoundException(empresaId));

        EmpresaParametro parametro = repository.findById(parametroId)
                .orElseThrow(() -> new EmpresaParametroNotFoundException(parametroId));

        repository.delete(parametro);
        log.info("Parámetro con ID: {} eliminado correctamente", parametroId);
    }
}