package com.clyvo.vitalpet.mapper;

import com.clyvo.vitalpet.dto.ClinicaRequest;
import com.clyvo.vitalpet.dto.ClinicaResponse;
import com.clyvo.vitalpet.model.Clinica;

public final class ClinicaMapper {
    private ClinicaMapper() { }

    public static Clinica toEntity(ClinicaRequest request) {
        Clinica clinica = new Clinica();
        copiarDados(request, clinica);
        return clinica;
    }

    public static void copiarDados(ClinicaRequest request, Clinica clinica) {
        clinica.setNome(request.nome());
        clinica.setEndereco(request.endereco());
        clinica.setCidade(request.cidade());
        clinica.setEstado(request.estado().toUpperCase());
        clinica.setCep(request.cep());
        clinica.setTelefone(request.telefone());
        clinica.setEmail(request.email());
        clinica.setCnpj(request.cnpj());
    }

    public static ClinicaResponse toResponse(Clinica clinica) {
        return new ClinicaResponse(
                clinica.getId(),
                clinica.getNome(),
                clinica.getEndereco(),
                clinica.getCidade(),
                clinica.getEstado(),
                clinica.getCep(),
                clinica.getTelefone(),
                clinica.getEmail(),
                clinica.getCnpj(),
                clinica.isAtiva(),
                clinica.getDataCadastro(),
                clinica.getDataAtualizacao(),
                clinica.getVeterinarios() == null ? 0 : clinica.getVeterinarios().size()
        );
    }
}
