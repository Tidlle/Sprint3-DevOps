package com.clyvo.vitalpet.dto;

import java.time.LocalDateTime;

public record TutorResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        String endereco,
        String cidade,
        String estado,
        String cep,
        boolean ativo,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao,
        int quantidadePets
) { }
