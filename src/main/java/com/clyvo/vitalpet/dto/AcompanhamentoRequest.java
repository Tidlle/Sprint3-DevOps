package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AcompanhamentoRequest(
        @NotNull Long consultaId,
        @NotBlank @Size(max = 1000) String descricao
) { }
