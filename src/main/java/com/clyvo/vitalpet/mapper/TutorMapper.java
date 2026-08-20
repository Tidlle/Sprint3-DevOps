package com.clyvo.vitalpet.mapper;

import com.clyvo.vitalpet.dto.TutorRequest;
import com.clyvo.vitalpet.dto.TutorResponse;
import com.clyvo.vitalpet.model.Tutor;

public final class TutorMapper {
    private TutorMapper() { }

    public static Tutor toEntity(TutorRequest request) {
        Tutor tutor = new Tutor();
        copiarDados(request, tutor);
        return tutor;
    }

    public static void copiarDados(TutorRequest request, Tutor tutor) {
        tutor.setNome(request.nome());
        tutor.setEmail(request.email());
        tutor.setTelefone(request.telefone());
        tutor.setCpf(request.cpf());
        tutor.setEndereco(request.endereco());
        tutor.setCidade(request.cidade());
        tutor.setEstado(request.estado() == null ? null : request.estado().toUpperCase());
        tutor.setCep(request.cep());
    }

    public static TutorResponse toResponse(Tutor tutor) {
        return new TutorResponse(
                tutor.getId(),
                tutor.getNome(),
                tutor.getEmail(),
                tutor.getTelefone(),
                tutor.getCpf(),
                tutor.getEndereco(),
                tutor.getCidade(),
                tutor.getEstado(),
                tutor.getCep(),
                tutor.isAtivo(),
                tutor.getDataCadastro(),
                tutor.getDataAtualizacao(),
                tutor.getPets() == null ? 0 : tutor.getPets().size()
        );
    }
}
