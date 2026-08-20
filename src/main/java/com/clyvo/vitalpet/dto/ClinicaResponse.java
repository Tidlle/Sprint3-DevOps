package com.clyvo.vitalpet.dto;

import java.time.LocalDateTime;

public record ClinicaResponse(
        Long id,
        String nome,
        String endereco,
        String cidade,
        String estado,
        String cep,
        String telefone,
        String email,
        String cnpj,
        boolean ativa,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao,
        int quantidadeVeterinarios
) { }
