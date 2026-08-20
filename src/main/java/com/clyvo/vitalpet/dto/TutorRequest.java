package com.clyvo.vitalpet.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record TutorRequest(
        @NotBlank @Size(min = 3, max = 120) String nome,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 20) String telefone,
        @NotBlank @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos, sem pontuação") String cpf,
        @Size(max = 180) String endereco,
        @Size(max = 80) String cidade,
        @Size(min = 2, max = 2) String estado,
        @Pattern(regexp = "^$|\\d{5}-?\\d{3}", message = "CEP deve ter 8 dígitos") String cep
) { }
