package com.clyvo.vitalpet.service;

import com.clyvo.vitalpet.dto.VeterinarioRequest;
import com.clyvo.vitalpet.dto.VeterinarioResponse;
import com.clyvo.vitalpet.exception.RecursoNaoEncontradoException;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.mapper.VeterinarioMapper;
import com.clyvo.vitalpet.model.Clinica;
import com.clyvo.vitalpet.model.Veterinario;
import com.clyvo.vitalpet.repository.VeterinarioRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VeterinarioService {

    private final VeterinarioRepository veterinarioRepository;
    private final ClinicaService clinicaService;

    public VeterinarioService(VeterinarioRepository veterinarioRepository, ClinicaService clinicaService) {
        this.veterinarioRepository = veterinarioRepository;
        this.clinicaService = clinicaService;
    }

    @Transactional
    @CacheEvict(value = {"veterinarios", "clinicas", "dashboard"}, allEntries = true)
    public VeterinarioResponse criar(VeterinarioRequest request) {
        validarUnicidade(request, null);
        Clinica clinica = clinicaService.buscarEntidadePorId(request.clinicaId());
        Veterinario veterinario = VeterinarioMapper.toEntity(request, clinica);
        return VeterinarioMapper.toResponse(veterinarioRepository.save(veterinario));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "veterinarios", key = "#id")
    public VeterinarioResponse buscarPorId(Long id) {
        return VeterinarioMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<VeterinarioResponse> listar(String nome, String especialidade, Long clinicaId, Boolean ativo, Pageable pageable) {
        return veterinarioRepository.buscarComFiltros(limpar(nome), limpar(especialidade), clinicaId, ativo, pageable)
                .map(VeterinarioMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = {"veterinarios", "clinicas", "dashboard"}, allEntries = true)
    public VeterinarioResponse atualizar(Long id, VeterinarioRequest request) {
        Veterinario veterinario = buscarEntidadePorId(id);
        validarUnicidade(request, id);
        Clinica clinica = clinicaService.buscarEntidadePorId(request.clinicaId());
        VeterinarioMapper.copiarDados(request, veterinario, clinica);
        return VeterinarioMapper.toResponse(veterinarioRepository.save(veterinario));
    }

    @Transactional
    @CacheEvict(value = {"veterinarios", "dashboard"}, allEntries = true)
    public void desativar(Long id) {
        Veterinario veterinario = buscarEntidadePorId(id);
        veterinario.setAtivo(false);
        veterinarioRepository.save(veterinario);
    }

    @Transactional
    @CacheEvict(value = {"veterinarios", "dashboard"}, allEntries = true)
    public VeterinarioResponse ativar(Long id) {
        Veterinario veterinario = buscarEntidadePorId(id);
        veterinario.setAtivo(true);
        return VeterinarioMapper.toResponse(veterinarioRepository.save(veterinario));
    }

    @Transactional(readOnly = true)
    public Veterinario buscarEntidadePorId(Long id) {
        return veterinarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Veterinário não encontrado com id " + id + "."));
    }

    private void validarUnicidade(VeterinarioRequest request, Long idAtual) {
        boolean crmvExiste = idAtual == null ? veterinarioRepository.existsByCrmv(request.crmv().toUpperCase()) : veterinarioRepository.existsByCrmvAndIdNot(request.crmv().toUpperCase(), idAtual);
        boolean emailExiste = idAtual == null ? veterinarioRepository.existsByEmail(request.email()) : veterinarioRepository.existsByEmailAndIdNot(request.email(), idAtual);
        if (crmvExiste) {
            throw new RegraNegocioException("Já existe veterinário cadastrado com esse CRMV.");
        }
        if (emailExiste) {
            throw new RegraNegocioException("Já existe veterinário cadastrado com esse e-mail.");
        }
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
