package com.clyvo.vitalpet.mapper;

import com.clyvo.vitalpet.dto.AcompanhamentoRequest;
import com.clyvo.vitalpet.dto.AcompanhamentoResponse;
import com.clyvo.vitalpet.model.Acompanhamento;
import com.clyvo.vitalpet.model.Consulta;
import com.clyvo.vitalpet.model.Pet;

public final class AcompanhamentoMapper {
    private AcompanhamentoMapper() { }

    public static Acompanhamento toEntity(AcompanhamentoRequest request, Consulta consulta) {
        Acompanhamento acompanhamento = new Acompanhamento();
        acompanhamento.setConsulta(consulta);
        acompanhamento.setDescricao(request.descricao());
        acompanhamento.setStatus("ATIVO");
        return acompanhamento;
    }

    public static AcompanhamentoResponse toResponse(Acompanhamento acompanhamento) {
        Consulta consulta = acompanhamento.getConsulta();
        Pet pet = consulta == null ? null : consulta.getPet();
        return new AcompanhamentoResponse(
                acompanhamento.getId(),
                acompanhamento.getStatus(),
                acompanhamento.getDataInicio(),
                acompanhamento.getDataFim(),
                acompanhamento.getDescricao(),
                acompanhamento.getDataCadastro(),
                acompanhamento.getDataAtualizacao(),
                consulta == null ? null : consulta.getId(),
                pet == null ? null : pet.getId(),
                pet == null ? null : pet.getNome(),
                acompanhamento.getAlertas() == null ? 0 : acompanhamento.getAlertas().size()
        );
    }
}
