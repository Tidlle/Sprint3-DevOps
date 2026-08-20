package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsultaAtualizacaoRequest(
        LocalDateTime dataHora,
        @Size(max = 60) String tipo,
        @Size(max = 1000) String sintomas,
        @Size(max = 1000) String diagnostico,
        @Size(max = 1000) String tratamento,
        @PositiveOrZero BigDecimal valor,
        Long petId,
        Long veterinarioId
) { }
