package com.clyvo.vitalpet.dto;

import java.time.LocalDateTime;

public record VeterinarioResponse(
        Long id,
        String nome,
        String email,
        String telefone,
        String crmv,
        String especialidade,
        boolean ativo,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao,
        Long clinicaId,
        String clinicaNome,
        int quantidadeConsultas
) { }
