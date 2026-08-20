package com.clyvo.vitalpet.mapper;

import com.clyvo.vitalpet.dto.PetRequest;
import com.clyvo.vitalpet.dto.PetResponse;
import com.clyvo.vitalpet.model.Pet;
import com.clyvo.vitalpet.model.Tutor;

public final class PetMapper {
    private PetMapper() { }

    public static Pet toEntity(PetRequest request, Tutor tutor) {
        Pet pet = new Pet();
        copiarDados(request, pet, tutor);
        return pet;
    }

    public static void copiarDados(PetRequest request, Pet pet, Tutor tutor) {
        pet.setNome(request.nome());
        pet.setEspecie(request.especie());
        pet.setRaca(request.raca());
        pet.setDataNascimento(request.dataNascimento());
        pet.setSexo(request.sexo());
        pet.setPeso(request.peso());
        pet.setObservacoes(request.observacoes());
        pet.setTutor(tutor);
    }

    public static PetResponse toResponse(Pet pet) {
        Tutor tutor = pet.getTutor();
        return new PetResponse(
                pet.getId(),
                pet.getNome(),
                pet.getEspecie(),
                pet.getRaca(),
                pet.getDataNascimento(),
                pet.getSexo(),
                pet.getPeso(),
                pet.getObservacoes(),
                pet.isAtivo(),
                pet.getDataCadastro(),
                pet.getDataAtualizacao(),
                tutor == null ? null : tutor.getId(),
                tutor == null ? null : tutor.getNome(),
                pet.getConsultas() == null ? 0 : pet.getConsultas().size(),
                pet.getAlertas() == null ? 0 : pet.getAlertas().size()
        );
    }
}
