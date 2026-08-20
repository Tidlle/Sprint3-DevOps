package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConsultaRequest(
        @NotNull @FutureOrPresent(message = "A data da consulta deve ser atual ou futura") LocalDateTime dataHora,
        @NotBlank @Size(max = 60) String tipo,
        @Size(max = 1000) String sintomas,
        @Size(max = 1000) String diagnostico,
        @Size(max = 1000) String tratamento,
        @NotNull @PositiveOrZero BigDecimal valor,
        @NotNull Long petId,
        @NotNull Long veterinarioId
) { }
