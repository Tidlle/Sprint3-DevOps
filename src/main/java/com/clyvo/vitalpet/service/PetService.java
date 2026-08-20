package com.clyvo.vitalpet.service;

import com.clyvo.vitalpet.dto.PetRequest;
import com.clyvo.vitalpet.dto.PetResponse;
import com.clyvo.vitalpet.exception.RecursoNaoEncontradoException;
import com.clyvo.vitalpet.mapper.PetMapper;
import com.clyvo.vitalpet.model.Pet;
import com.clyvo.vitalpet.model.Tutor;
import com.clyvo.vitalpet.repository.PetRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final TutorService tutorService;

    public PetService(PetRepository petRepository, TutorService tutorService) {
        this.petRepository = petRepository;
        this.tutorService = tutorService;
    }

    @Transactional
    @CacheEvict(value = {"pets", "tutores", "dashboard"}, allEntries = true)
    public PetResponse criar(PetRequest request) {
        Tutor tutor = tutorService.buscarEntidadePorId(request.tutorId());
        Pet pet = PetMapper.toEntity(request, tutor);
        return PetMapper.toResponse(petRepository.save(pet));
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "pets", key = "#id")
    public PetResponse buscarPorId(Long id) {
        return PetMapper.toResponse(buscarEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public Page<PetResponse> listar(String nome, String especie, Long tutorId, Boolean ativo, Pageable pageable) {
        return petRepository.buscarComFiltros(limpar(nome), limpar(especie), tutorId, ativo, pageable)
                .map(PetMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = {"pets", "tutores", "dashboard"}, allEntries = true)
    public PetResponse atualizar(Long id, PetRequest request) {
        Pet pet = buscarEntidadePorId(id);
        Tutor tutor = tutorService.buscarEntidadePorId(request.tutorId());
        PetMapper.copiarDados(request, pet, tutor);
        return PetMapper.toResponse(petRepository.save(pet));
    }

    @Transactional
    @CacheEvict(value = {"pets", "dashboard"}, allEntries = true)
    public void desativar(Long id) {
        Pet pet = buscarEntidadePorId(id);
        pet.setAtivo(false);
        petRepository.save(pet);
    }

    @Transactional
    @CacheEvict(value = {"pets", "dashboard"}, allEntries = true)
    public PetResponse ativar(Long id) {
        Pet pet = buscarEntidadePorId(id);
        pet.setAtivo(true);
        return PetMapper.toResponse(petRepository.save(pet));
    }

    @Transactional(readOnly = true)
    public Pet buscarEntidadePorId(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pet não encontrado com id " + id + "."));
    }

    private String limpar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
