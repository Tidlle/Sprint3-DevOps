package com.clyvo.vitalpet.dto;

import java.time.LocalDateTime;

public record AcompanhamentoResponse(
        Long id,
        String status,
        LocalDateTime dataInicio,
        LocalDateTime dataFim,
        String descricao,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao,
        Long consultaId,
        Long petId,
        String petNome,
        int quantidadeAlertas
) { }
