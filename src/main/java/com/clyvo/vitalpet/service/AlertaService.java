package com.clyvo.vitalpet.service;

import com.clyvo.vitalpet.dto.AlertaRequest;
import com.clyvo.vitalpet.dto.AlertaResponse;
import com.clyvo.vitalpet.exception.RecursoNaoEncontradoException;
import com.clyvo.vitalpet.mapper.AlertaMapper;
import com.clyvo.vitalpet.model.Acompanhamento;
import com.clyvo.vitalpet.model.Alerta;
import com.clyvo.vitalpet.model.Pet;
import com.clyvo.vitalpet.repository.AcompanhamentoRepository;
import com.clyvo.vitalpet.repository.AlertaRepository;
import com.clyvo.vitalpet.repository.PetRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AlertaService {

    private final AlertaRepository alertaRepository;
    private final PetRepository petRepository;
    private final AcompanhamentoRepository acompanhamentoRepository;

    public AlertaService(AlertaRepository alertaRepository, PetRepository petRepository, AcompanhamentoRepository acompanhamentoRepository) {
        this.alertaRepository = alertaRepository;
        this.petRepository = petRepository;
        this.acompanhamentoRepository = acompanhamentoRepository;
    }

    @Transactional
    @CacheEvict(value = {"alertas", "pets", "acompanhamentos", "dashboard"}, allEntries = true)
    public AlertaResponse criar(AlertaRequest request) {
        Pet pet = petRepository.findById(request.petId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com id " + request.petId() + "."));
        Acompanhamento acompanhamento = null;
        if (request.acompanhamentoId() != null) {
            acompanhamento = acompanhamentoRepository.findById(request.acompanhamentoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Acompanhamento não encontrado com id " + request.acompanhamentoId() + "."));
        }
        Alerta alerta = AlertaMapper.toEntity(request, pet, acompanhamento);
        return AlertaMapper.toResponse(alertaRepository.save(alerta));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "alertas", key = "#id")
    public AlertaResponse buscarPorId(Long id) {
        return AlertaMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<AlertaResponse> listar(Long petId, String status, String prioridade, String tipo, Pageable pageable) {
        return alertaRepository.buscarComFiltros(petId, limpar(status), limpar(prioridade), limpar(tipo), pageable)
                .map(AlertaMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = {"alertas", "dashboard"}, allEntries = true)
    public AlertaResponse resolver(Long id) {
        Alerta alerta = buscarEntidadePorId(id);
        alerta.setStatus("RESOLVIDO");
        alerta.setDataResolucao(LocalDateTime.now());
        return AlertaMapper.toResponse(alertaRepository.save(alerta));
    }

    @Transactional
    @CacheEvict(value = {"alertas", "dashboard"}, allEntries = true)
    public AlertaResponse cancelar(Long id) {
        Alerta alerta = buscarEntidadePorId(id);
        alerta.setStatus("CANCELADO");
        return AlertaMapper.toResponse(alertaRepository.save(alerta));
    }

    @Transactional(readOnly = true)
    public Alerta buscarEntidadePorId(Long id) {
        return alertaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Alerta não encontrado com id " + id + "."));
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
