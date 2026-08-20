package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PetRequest(
        @NotBlank @Size(min = 2, max = 80) String nome,
        @NotBlank @Size(max = 50) String especie,
        @Size(max = 80) String raca,
        LocalDate dataNascimento,
        @NotBlank @Size(max = 20) String sexo,
        @NotNull @Positive BigDecimal peso,
        @Size(max = 500) String observacoes,
        @NotNull Long tutorId
) { }
