package com.clyvo.vitalpet.mapper;

import com.clyvo.vitalpet.dto.AlertaRequest;
import com.clyvo.vitalpet.dto.AlertaResponse;
import com.clyvo.vitalpet.model.Acompanhamento;
import com.clyvo.vitalpet.model.Alerta;
import com.clyvo.vitalpet.model.Pet;

public final class AlertaMapper {
    private AlertaMapper() { }

    public static Alerta toEntity(AlertaRequest request, Pet pet, Acompanhamento acompanhamento) {
        Alerta alerta = new Alerta();
        alerta.setPet(pet);
        alerta.setAcompanhamento(acompanhamento);
        alerta.setTipo(request.tipo());
        alerta.setTitulo(request.titulo());
        alerta.setDescricao(request.descricao());
        alerta.setPrioridade(request.prioridade().toUpperCase());
        alerta.setDataAlerta(request.dataAlerta());
        alerta.setStatus("PENDENTE");
        return alerta;
    }

    public static AlertaResponse toResponse(Alerta alerta) {
        Pet pet = alerta.getPet();
        Acompanhamento acompanhamento = alerta.getAcompanhamento();
        return new AlertaResponse(
                alerta.getId(),
                alerta.getTipo(),
                alerta.getTitulo(),
                alerta.getDescricao(),
                alerta.getPrioridade(),
                alerta.getStatus(),
                alerta.getDataAlerta(),
                alerta.getDataResolucao(),
                alerta.getDataCadastro(),
                alerta.getDataAtualizacao(),
                pet == null ? null : pet.getId(),
                pet == null ? null : pet.getNome(),
                acompanhamento == null ? null : acompanhamento.getId()
        );
    }
}
