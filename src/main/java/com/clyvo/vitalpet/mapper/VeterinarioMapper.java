package com.clyvo.vitalpet.mapper;

import com.clyvo.vitalpet.dto.VeterinarioRequest;
import com.clyvo.vitalpet.dto.VeterinarioResponse;
import com.clyvo.vitalpet.model.Clinica;
import com.clyvo.vitalpet.model.Veterinario;

public final class VeterinarioMapper {
    private VeterinarioMapper() { }

    public static Veterinario toEntity(VeterinarioRequest request, Clinica clinica) {
        Veterinario veterinario = new Veterinario();
        copiarDados(request, veterinario, clinica);
        return veterinario;
    }

    public static void copiarDados(VeterinarioRequest request, Veterinario veterinario, Clinica clinica) {
        veterinario.setNome(request.nome());
        veterinario.setEmail(request.email());
        veterinario.setTelefone(request.telefone());
        veterinario.setCrmv(request.crmv().toUpperCase());
        veterinario.setEspecialidade(request.especialidade());
        veterinario.setClinica(clinica);
    }

    public static VeterinarioResponse toResponse(Veterinario veterinario) {
        Clinica clinica = veterinario.getClinica();
        return new VeterinarioResponse(
                veterinario.getId(),
                veterinario.getNome(),
                veterinario.getEmail(),
                veterinario.getTelefone(),
                veterinario.getCrmv(),
                veterinario.getEspecialidade(),
                veterinario.isAtivo(),
                veterinario.getDataCadastro(),
                veterinario.getDataAtualizacao(),
                clinica == null ? null : clinica.getId(),
                clinica == null ? null : clinica.getNome(),
                veterinario.getConsultas() == null ? 0 : veterinario.getConsultas().size()
        );
    }
}
