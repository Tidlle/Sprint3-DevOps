package com.clyvo.vitalpet.dto;

import java.time.LocalDateTime;

public record AlertaResponse(
        Long id,
        String tipo,
        String titulo,
        String descricao,
        String prioridade,
        String status,
        LocalDateTime dataAlerta,
        LocalDateTime dataResolucao,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao,
        Long petId,
        String petNome,
        Long acompanhamentoId
) { }
