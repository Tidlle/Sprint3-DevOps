package com.clyvo.vitalpet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsultaResponse(
        Long id,
        LocalDateTime dataHora,
        String tipo,
        String sintomas,
        String diagnostico,
        String tratamento,
        String status,
        BigDecimal valor,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao,
        Long petId,
        String petNome,
        Long veterinarioId,
        String veterinarioNome,
        Long acompanhamentoId
) { }
