package com.clyvo.vitalpet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PetResponse(
        Long id,
        String nome,
        String especie,
        String raca,
        LocalDate dataNascimento,
        String sexo,
        BigDecimal peso,
        String observacoes,
        boolean ativo,
        LocalDateTime dataCadastro,
        LocalDateTime dataAtualizacao,
        Long tutorId,
        String tutorNome,
        int quantidadeConsultas,
        int quantidadeAlertas
) { }
