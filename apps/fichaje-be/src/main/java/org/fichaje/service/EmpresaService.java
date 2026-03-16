package org.fichaje.service;

import org.fichaje.converter.EmpresaDtoConverter;
import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.provider.db.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.Optional;

@Service
@Transactional
public class EmpresaService
        extends CommonServiceImpl<Empresa, EmpresaRepository> {

    @Autowired
    EmpresaDtoConverter dtoConverter;

    @Autowired
    EmpresaRepository repository;

    public Optional<Empresa> findByCif(String cif) {
        return repository.findByCif(cif);
    }

    public Optional<Empresa> findByActiva(Boolean activa) {
        return repository.findByActiva(activa);
    }

    public Page<EmpresaDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(dtoConverter::inverseTransform);
    }

    public EmpresaDTO save(EmpresaDTO empresaDTO) {
        Empresa empresa = dtoConverter.transform(empresaDTO);
        Empresa savedEmpresa = repository.save(empresa);
        return dtoConverter.inverseTransform(savedEmpresa);
    }

    public EmpresaDTO update(EmpresaDTO empresaDTO, Long id) {
        return repository.findById(id).map(
                c -> {
                    c.setId(empresaDTO.getId());
                    c.setNombre(empresaDTO.getNombre());
                    c.setCif(empresaDTO.getCif());
                    c.setActiva(empresaDTO.isActiva());

                    Empresa updatedEmpresa = repository.save(c);
                    return dtoConverter.inverseTransform(updatedEmpresa);
                }).orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + id));
    }

    public void delete(Long id) {
        repository.findById(id).orElseThrow(() -> new RuntimeException("Empresa no encontrada con id: " + id));
        repository.deleteById(id);
    }
}