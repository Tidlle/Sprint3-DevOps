package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClinicaRequest(
        @NotBlank @Size(min = 3, max = 120) String nome,
        @NotBlank @Size(max = 180) String endereco,
        @NotBlank @Size(max = 80) String cidade,
        @NotBlank @Size(min = 2, max = 2) String estado,
        @NotBlank @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter 8 dígitos") String cep,
        @NotBlank @Size(max = 20) String telefone,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos, sem pontuação") String cnpj
) { }
