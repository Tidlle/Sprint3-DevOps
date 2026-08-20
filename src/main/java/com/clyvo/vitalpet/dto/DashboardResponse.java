package com.clyvo.vitalpet.dto;

import java.math.BigDecimal;

public record DashboardResponse(
        long clinicasAtivas,
        long tutoresAtivos,
        long petsAtivos,
        long veterinariosAtivos,
        long consultasAgendadas,
        long consultasConcluidas,
        long alertasPendentes,
        long alertasAltaPrioridade,
        BigDecimal faturamentoTotal
) { }
