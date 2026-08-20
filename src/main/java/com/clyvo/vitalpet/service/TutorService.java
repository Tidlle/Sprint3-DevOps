package com.clyvo.vitalpet.service;

import com.clyvo.vitalpet.dto.TutorRequest;
import com.clyvo.vitalpet.dto.TutorResponse;
import com.clyvo.vitalpet.exception.RecursoNaoEncontradoException;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.mapper.TutorMapper;
import com.clyvo.vitalpet.model.Tutor;
import com.clyvo.vitalpet.repository.TutorRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;

    public TutorService(TutorRepository tutorRepository) {
        this.tutorRepository = tutorRepository;
    }

    @Transactional
    @CacheEvict(value = {"tutores", "dashboard"}, allEntries = true)
    public TutorResponse criar(TutorRequest request) {
        validarUnicidade(request, null);
        Tutor tutor = TutorMapper.toEntity(request);
        return TutorMapper.toResponse(tutorRepository.save(tutor));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "tutores", key = "#id")
    public TutorResponse buscarPorId(Long id) {
        return TutorMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<TutorResponse> listar(String nome, String cpf, String cidade, Boolean ativo, Pageable pageable) {
        return tutorRepository.buscarComFiltros(limpar(nome), limpar(cpf), limpar(cidade), ativo, pageable)
                .map(TutorMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = {"tutores", "dashboard"}, allEntries = true)
    public TutorResponse atualizar(Long id, TutorRequest request) {
        Tutor tutor = buscarEntidadePorId(id);
        validarUnicidade(request, id);
        TutorMapper.copiarDados(request, tutor);
        return TutorMapper.toResponse(tutorRepository.save(tutor));
    }

    @Transactional
    @CacheEvict(value = {"tutores", "dashboard"}, allEntries = true)
    public void desativar(Long id) {
        Tutor tutor = buscarEntidadePorId(id);
        tutor.setAtivo(false);
        tutorRepository.save(tutor);
    }

    @Transactional
    @CacheEvict(value = {"tutores", "dashboard"}, allEntries = true)
    public TutorResponse ativar(Long id) {
        Tutor tutor = buscarEntidadePorId(id);
        tutor.setAtivo(true);
        return TutorMapper.toResponse(tutorRepository.save(tutor));
    }

    @Transactional(readOnly = true)
    public Tutor buscarEntidadePorId(Long id) {
        return tutorRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Tutor não encontrado com id " + id + "."));
    }

    private void validarUnicidade(TutorRequest request, Long idAtual) {
        boolean cpfExiste = idAtual == null ? tutorRepository.existsByCpf(request.cpf()) : tutorRepository.existsByCpfAndIdNot(request.cpf(), idAtual);
        boolean emailExiste = idAtual == null ? tutorRepository.existsByEmail(request.email()) : tutorRepository.existsByEmailAndIdNot(request.email(), idAtual);
        if (cpfExiste) {
            throw new RegraNegocioException("Já existe tutor cadastrado com esse CPF.");
        }
        if (emailExiste) {
            throw new RegraNegocioException("Já existe tutor cadastrado com esse e-mail.");
        }
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
