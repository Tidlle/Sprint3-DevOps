package com.clyvo.vitalpet.service;

import com.clyvo.vitalpet.dto.DashboardResponse;
import com.clyvo.vitalpet.repository.*;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DashboardService {

    private final ClinicaRepository clinicaRepository;
    private final TutorRepository tutorRepository;
    private final PetRepository petRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final ConsultaRepository consultaRepository;
    private final AlertaRepository alertaRepository;

    public DashboardService(ClinicaRepository clinicaRepository,
                            TutorRepository tutorRepository,
                            PetRepository petRepository,
                            VeterinarioRepository veterinarioRepository,
                            ConsultaRepository consultaRepository,
                            AlertaRepository alertaRepository) {
        this.clinicaRepository = clinicaRepository;
        this.tutorRepository = tutorRepository;
        this.petRepository = petRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.consultaRepository = consultaRepository;
        this.alertaRepository = alertaRepository;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "dashboard", key = "'resumo'")
    public DashboardResponse resumo() {
        BigDecimal faturamento = consultaRepository.somarFaturamentoConcluido();
        return new DashboardResponse(
                clinicaRepository.countByAtivaTrue(),
                tutorRepository.countByAtivoTrue(),
                petRepository.countByAtivoTrue(),
                veterinarioRepository.countByAtivoTrue(),
                consultaRepository.countByStatus("AGENDADA"),
                consultaRepository.countByStatus("CONCLUIDA"),
                alertaRepository.countByStatus("PENDENTE"),
                alertaRepository.countByStatusAndPrioridadeIn("PENDENTE", List.of("ALTA", "CRITICA")),
                faturamento == null ? BigDecimal.ZERO : faturamento
        );
    }
}
