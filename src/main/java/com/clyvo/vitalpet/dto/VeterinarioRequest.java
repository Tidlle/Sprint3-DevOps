package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record VeterinarioRequest(
        @NotBlank @Size(min = 3, max = 120) String nome,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 20) String telefone,
        @NotBlank @Pattern(regexp = "[A-Z]{2}-?\\d{4,6}", message = "CRMV deve seguir o formato UF-0000") String crmv,
        @NotBlank @Size(max = 80) String especialidade,
        @NotNull Long clinicaId
) { }
