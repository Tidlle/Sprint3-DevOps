package com.clyvo.vitalpet.service;

import com.clyvo.vitalpet.dto.ConsultaAtualizacaoRequest;
import com.clyvo.vitalpet.dto.ConsultaRequest;
import com.clyvo.vitalpet.dto.ConsultaResponse;
import com.clyvo.vitalpet.dto.FinalizarConsultaRequest;
import com.clyvo.vitalpet.exception.RecursoNaoEncontradoException;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.mapper.ConsultaMapper;
import com.clyvo.vitalpet.model.Acompanhamento;
import com.clyvo.vitalpet.model.Alerta;
import com.clyvo.vitalpet.model.Consulta;
import com.clyvo.vitalpet.model.Pet;
import com.clyvo.vitalpet.model.Veterinario;
import com.clyvo.vitalpet.repository.AcompanhamentoRepository;
import com.clyvo.vitalpet.repository.AlertaRepository;
import com.clyvo.vitalpet.repository.ConsultaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final AcompanhamentoRepository acompanhamentoRepository;
    private final AlertaRepository alertaRepository;
    private final PetService petService;
    private final VeterinarioService veterinarioService;

    public ConsultaService(ConsultaRepository consultaRepository,
                           AcompanhamentoRepository acompanhamentoRepository,
                           AlertaRepository alertaRepository,
                           PetService petService,
                           VeterinarioService veterinarioService) {
        this.consultaRepository = consultaRepository;
        this.acompanhamentoRepository = acompanhamentoRepository;
        this.alertaRepository = alertaRepository;
        this.petService = petService;
        this.veterinarioService = veterinarioService;
    }

    @Transactional
    @CacheEvict(value = {"consultas", "pets", "veterinarios", "dashboard"}, allEntries = true)
    public ConsultaResponse criar(ConsultaRequest request) {
        Pet pet = petService.buscarEntidadePorId(request.petId());
        Veterinario veterinario = veterinarioService.buscarEntidadePorId(request.veterinarioId());
        if (!pet.isAtivo()) {
            throw new RegraNegocioException("Não é possível agendar consulta para pet inativo.");
        }
        if (!veterinario.isAtivo()) {
            throw new RegraNegocioException("Não é possível agendar consulta com veterinário inativo.");
        }
        Consulta consulta = ConsultaMapper.toEntity(request, pet, veterinario);
        return ConsultaMapper.toResponse(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "consultas", key = "#id")
    public ConsultaResponse buscarPorId(Long id) {
        return ConsultaMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<ConsultaResponse> listar(Long petId, Long veterinarioId, String status, LocalDateTime inicio, LocalDateTime fim, Pageable pageable) {
        return consultaRepository.buscarComFiltros(petId, veterinarioId, limpar(status), inicio, fim, pageable)
                .map(ConsultaMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = {"consultas", "pets", "veterinarios", "dashboard"}, allEntries = true)
    public ConsultaResponse atualizar(Long id, ConsultaAtualizacaoRequest request) {
        Consulta consulta = buscarEntidadePorId(id);
        if (request.petId() != null) {
            consulta.setPet(petService.buscarEntidadePorId(request.petId()));
        }
        if (request.veterinarioId() != null) {
            consulta.setVeterinario(veterinarioService.buscarEntidadePorId(request.veterinarioId()));
        }
        if (request.dataHora() != null) {
            consulta.setDataHora(request.dataHora());
        }
        if (request.tipo() != null) {
            consulta.setTipo(request.tipo());
        }
        if (request.sintomas() != null) {
            consulta.setSintomas(request.sintomas());
        }
        if (request.diagnostico() != null) {
            consulta.setDiagnostico(request.diagnostico());
        }
        if (request.tratamento() != null) {
            consulta.setTratamento(request.tratamento());
        }
        if (request.valor() != null) {
            consulta.setValor(request.valor());
        }
        return ConsultaMapper.toResponse(consultaRepository.save(consulta));
    }

    @Transactional
    @CacheEvict(value = {"consultas", "acompanhamentos", "alertas", "dashboard"}, allEntries = true)
    public ConsultaResponse finalizar(Long id, FinalizarConsultaRequest request) {
        Consulta consulta = buscarEntidadePorId(id);
        if ("CANCELADA".equalsIgnoreCase(consulta.getStatus())) {
            throw new RegraNegocioException("Consulta cancelada não pode ser finalizada.");
        }
        consulta.setStatus("CONCLUIDA");
        consulta.setDiagnostico(request.diagnostico());
        consulta.setTratamento(request.tratamento());

        if (request.descricaoAcompanhamento() != null && !request.descricaoAcompanhamento().isBlank()
                && !acompanhamentoRepository.existsByConsultaId(consulta.getId())) {
            Acompanhamento acompanhamento = new Acompanhamento();
            acompanhamento.setConsulta(consulta);
            acompanhamento.setDescricao(request.descricaoAcompanhamento());
            acompanhamento.setStatus("ATIVO");
            acompanhamentoRepository.save(acompanhamento);
            consulta.setAcompanhamento(acompanhamento);

            Alerta alerta = new Alerta();
            alerta.setPet(consulta.getPet());
            alerta.setAcompanhamento(acompanhamento);
            alerta.setTipo("RETORNO");
            alerta.setTitulo("Retorno pós-consulta de " + consulta.getPet().getNome());
            alerta.setDescricao("Entrar em contato com o tutor para acompanhar a evolução do pet após a consulta.");
            alerta.setPrioridade("MEDIA");
            alerta.setStatus("PENDENTE");
            alerta.setDataAlerta(LocalDateTime.now().plusDays(7));
            alertaRepository.save(alerta);
        }

        return ConsultaMapper.toResponse(consultaRepository.save(consulta));
    }

    @Transactional
    @CacheEvict(value = {"consultas", "dashboard"}, allEntries = true)
    public ConsultaResponse cancelar(Long id) {
        Consulta consulta = buscarEntidadePorId(id);
        consulta.setStatus("CANCELADA");
        return ConsultaMapper.toResponse(consultaRepository.save(consulta));
    }

    @Transactional(readOnly = true)
    public Consulta buscarEntidadePorId(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta não encontrada com id " + id + "."));
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
