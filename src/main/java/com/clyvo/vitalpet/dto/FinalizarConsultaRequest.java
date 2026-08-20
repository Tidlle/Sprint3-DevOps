package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.Size;

public record FinalizarConsultaRequest(
        @Size(max = 1000) String diagnostico,
        @Size(max = 1000) String tratamento,
        @Size(max = 1000) String descricaoAcompanhamento
) { }
