package com.clyvo.vitalpet.service;

import com.clyvo.vitalpet.dto.AcompanhamentoRequest;
import com.clyvo.vitalpet.dto.AcompanhamentoResponse;
import com.clyvo.vitalpet.exception.RecursoNaoEncontradoException;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.mapper.AcompanhamentoMapper;
import com.clyvo.vitalpet.model.Acompanhamento;
import com.clyvo.vitalpet.model.Consulta;
import com.clyvo.vitalpet.repository.AcompanhamentoRepository;
import com.clyvo.vitalpet.repository.ConsultaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AcompanhamentoService {

    private final AcompanhamentoRepository acompanhamentoRepository;
    private final ConsultaRepository consultaRepository;

    public AcompanhamentoService(AcompanhamentoRepository acompanhamentoRepository, ConsultaRepository consultaRepository) {
        this.acompanhamentoRepository = acompanhamentoRepository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional
    @CacheEvict(value = {"acompanhamentos", "consultas", "dashboard"}, allEntries = true)
    public AcompanhamentoResponse criar(AcompanhamentoRequest request) {
        if (acompanhamentoRepository.existsByConsultaId(request.consultaId())) {
            throw new RegraNegocioException("Essa consulta já possui acompanhamento cadastrado.");
        }
        Consulta consulta = consultaRepository.findById(request.consultaId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada com id " + request.consultaId() + "."));
        Acompanhamento acompanhamento = AcompanhamentoMapper.toEntity(request, consulta);
        consulta.setAcompanhamento(acompanhamento);
        return AcompanhamentoMapper.toResponse(acompanhamentoRepository.save(acompanhamento));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "acompanhamentos", key = "#id")
    public AcompanhamentoResponse buscarPorId(Long id) {
        return AcompanhamentoMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<AcompanhamentoResponse> listar(Long consultaId, Long petId, String status, Pageable pageable) {
        return acompanhamentoRepository.buscarComFiltros(consultaId, petId, limpar(status), pageable)
                .map(AcompanhamentoMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = {"acompanhamentos", "dashboard"}, allEntries = true)
    public AcompanhamentoResponse concluir(Long id) {
        Acompanhamento acompanhamento = buscarEntidadePorId(id);
        acompanhamento.setStatus("CONCLUIDO");
        acompanhamento.setDataFim(LocalDateTime.now());
        return AcompanhamentoMapper.toResponse(acompanhamentoRepository.save(acompanhamento));
    }

    @Transactional
    @CacheEvict(value = {"acompanhamentos", "dashboard"}, allEntries = true)
    public AcompanhamentoResponse cancelar(Long id) {
        Acompanhamento acompanhamento = buscarEntidadePorId(id);
        acompanhamento.setStatus("CANCELADO");
        acompanhamento.setDataFim(LocalDateTime.now());
        return AcompanhamentoMapper.toResponse(acompanhamentoRepository.save(acompanhamento));
    }

    @Transactional(readOnly = true)
    public Acompanhamento buscarEntidadePorId(Long id) {
        return acompanhamentoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Acompanhamento não encontrado com id " + id + "."));
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
