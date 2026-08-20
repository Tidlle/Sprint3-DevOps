package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record AlertaRequest(
        @NotNull Long petId,
        Long acompanhamentoId,
        @NotBlank @Size(max = 60) String tipo,
        @NotBlank @Size(max = 120) String titulo,
        @NotBlank @Size(max = 1000) String descricao,
        @NotBlank @Size(max = 20) String prioridade,
        @NotNull @FutureOrPresent(message = "A data do alerta deve ser atual ou futura") LocalDateTime dataAlerta
) { }
