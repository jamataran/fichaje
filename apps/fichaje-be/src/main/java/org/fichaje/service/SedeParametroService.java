package org.fichaje.service;

import org.fichaje.converter.SedeParametroDtoConverter;
import org.fichaje.dto.entity.SedeParametroDTO;
import org.fichaje.exception.SedeNotFoundException;
import org.fichaje.exception.SedeParametroNotFoundException;
import org.fichaje.provider.db.entity.Sede;
import org.fichaje.provider.db.entity.SedeParametro;
import org.fichaje.provider.db.repository.SedeParametroRepository;
import org.fichaje.provider.db.repository.SedeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SedeParametroService {

    private static final Logger log = LoggerFactory.getLogger(SedeParametroService.class);

    private final SedeParametroRepository repository;
    private final SedeRepository sedeRepository;
    private final SedeParametroDtoConverter dtoConverter;

    public SedeParametroService(SedeParametroRepository repository, SedeRepository sedeRepository, SedeParametroDtoConverter dtoConverter) {
        this.repository = repository;
        this.sedeRepository = sedeRepository;
        this.dtoConverter = dtoConverter;
    }

    @Transactional(readOnly = true)
    public List<SedeParametroDTO> findBySedeId(Long sedeId) {
        log.info("Consultando parámetros de la sede con ID: {}", sedeId);
        sedeRepository.findById(sedeId).orElseThrow(() -> new SedeNotFoundException(sedeId));
        return repository.findBySedeId(sedeId).stream()
                .map(dtoConverter::todtoConverter)
                .toList();
    }

    @Transactional
    public SedeParametroDTO save(Long sedeId, SedeParametroDTO dto) {
        log.info("Creando parámetro '{}' para la sede con ID: {}", dto.getClave(), sedeId);
        Sede sede = sedeRepository.findById(sedeId)
                .orElseThrow(() -> new SedeNotFoundException(sedeId));

        SedeParametro parametro = dtoConverter.transform(dto);
        parametro.setSede(sede);

        SedeParametro saved = repository.save(parametro);
        log.info("Parámetro '{}' creado con éxito para la sede con ID: {}", dto.getClave(), sedeId);
        return dtoConverter.todtoConverter(saved);
    }

    @Transactional
    public SedeParametroDTO update(Long sedeId, Long parametroId, SedeParametroDTO dto) {
        log.info("Actualizando parámetro con ID: {} de la sede con ID: {}", parametroId, sedeId);
        sedeRepository.findById(sedeId).orElseThrow(() -> new SedeNotFoundException(sedeId));

        SedeParametro parametro = repository.findById(parametroId)
                .orElseThrow(() -> new SedeParametroNotFoundException(parametroId));

        parametro.setClave(dto.getClave());
        parametro.setValor(dto.getValor());

        SedeParametro updated = repository.save(parametro);
        log.info("Parámetro con ID: {} actualizado correctamente", parametroId);
        return dtoConverter.todtoConverter(updated);
    }

    @Transactional
    public void delete(Long sedeId, Long parametroId) {
        log.info("Eliminando parámetro con ID: {} de la sede con ID: {}", parametroId, sedeId);
        sedeRepository.findById(sedeId).orElseThrow(() -> new SedeNotFoundException(sedeId));

        SedeParametro parametro = repository.findById(parametroId)
                .orElseThrow(() -> new SedeParametroNotFoundException(parametroId));

        repository.delete(parametro);
        log.info("Parámetro con ID: {} eliminado correctamente", parametroId);
    }
}