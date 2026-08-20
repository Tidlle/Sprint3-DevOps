package com.clyvo.vitalpet.service;

import com.clyvo.vitalpet.dto.ClinicaRequest;
import com.clyvo.vitalpet.dto.ClinicaResponse;
import com.clyvo.vitalpet.exception.RecursoNaoEncontradoException;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.mapper.ClinicaMapper;
import com.clyvo.vitalpet.model.Clinica;
import com.clyvo.vitalpet.repository.ClinicaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClinicaService {

    private final ClinicaRepository clinicaRepository;

    public ClinicaService(ClinicaRepository clinicaRepository) {
        this.clinicaRepository = clinicaRepository;
    }

    @Transactional
    @CacheEvict(value = {"clinicas", "dashboard"}, allEntries = true)
    public ClinicaResponse criar(ClinicaRequest request) {
        if (clinicaRepository.existsByCnpj(request.cnpj())) {
            throw new RegraNegocioException("Já existe clínica cadastrada com esse CNPJ.");
        }
        Clinica clinica = ClinicaMapper.toEntity(request);
        return ClinicaMapper.toResponse(clinicaRepository.save(clinica));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "clinicas", key = "#id")
    public ClinicaResponse buscarPorId(Long id) {
        return ClinicaMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<ClinicaResponse> listar(String nome, String cidade, String estado, Boolean ativa, Pageable pageable) {
        return clinicaRepository.buscarComFiltros(limpar(nome), limpar(cidade), limpar(estado), ativa, pageable)
                .map(ClinicaMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = {"clinicas", "dashboard"}, allEntries = true)
    public ClinicaResponse atualizar(Long id, ClinicaRequest request) {
        Clinica clinica = buscarEntidadePorId(id);
        if (clinicaRepository.existsByCnpjAndIdNot(request.cnpj(), id)) {
            throw new RegraNegocioException("Já existe outra clínica cadastrada com esse CNPJ.");
        }
        ClinicaMapper.copiarDados(request, clinica);
        return ClinicaMapper.toResponse(clinicaRepository.save(clinica));
    }

    @Transactional
    @CacheEvict(value = {"clinicas", "dashboard"}, allEntries = true)
    public void desativar(Long id) {
        Clinica clinica = buscarEntidadePorId(id);
        clinica.setAtiva(false);
        clinicaRepository.save(clinica);
    }

    @Transactional
    @CacheEvict(value = {"clinicas", "dashboard"}, allEntries = true)
    public ClinicaResponse ativar(Long id) {
        Clinica clinica = buscarEntidadePorId(id);
        clinica.setAtiva(true);
        return ClinicaMapper.toResponse(clinicaRepository.save(clinica));
    }

    @Transactional(readOnly = true)
    public Clinica buscarEntidadePorId(Long id) {
        return clinicaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Clínica não encontrada com id " + id + "."));
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
