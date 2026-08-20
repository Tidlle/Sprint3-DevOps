package com.clyvo.vitalpet.mapper;

import com.clyvo.vitalpet.dto.ConsultaRequest;
import com.clyvo.vitalpet.dto.ConsultaResponse;
import com.clyvo.vitalpet.model.Acompanhamento;
import com.clyvo.vitalpet.model.Consulta;
import com.clyvo.vitalpet.model.Pet;
import com.clyvo.vitalpet.model.Veterinario;

public final class ConsultaMapper {
    private ConsultaMapper() { }

    public static Consulta toEntity(ConsultaRequest request, Pet pet, Veterinario veterinario) {
        Consulta consulta = new Consulta();
        consulta.setDataHora(request.dataHora());
        consulta.setTipo(request.tipo());
        consulta.setSintomas(request.sintomas());
        consulta.setDiagnostico(request.diagnostico());
        consulta.setTratamento(request.tratamento());
        consulta.setValor(request.valor());
        consulta.setPet(pet);
        consulta.setVeterinario(veterinario);
        consulta.setStatus("AGENDADA");
        return consulta;
    }

    public static ConsultaResponse toResponse(Consulta consulta) {
        Pet pet = consulta.getPet();
        Veterinario veterinario = consulta.getVeterinario();
        Acompanhamento acompanhamento = consulta.getAcompanhamento();
        return new ConsultaResponse(
                consulta.getId(),
                consulta.getDataHora(),
                consulta.getTipo(),
                consulta.getSintomas(),
                consulta.getDiagnostico(),
                consulta.getTratamento(),
                consulta.getStatus(),
                consulta.getValor(),
                consulta.getDataCadastro(),
                consulta.getDataAtualizacao(),
                pet == null ? null : pet.getId(),
                pet == null ? null : pet.getNome(),
                veterinario == null ? null : veterinario.getId(),
                veterinario == null ? null : veterinario.getNome(),
                acompanhamento == null ? null : acompanhamento.getId()
        );
    }
}
