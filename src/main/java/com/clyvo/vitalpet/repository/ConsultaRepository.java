package com.clyvo.vitalpet.repository;

import com.clyvo.vitalpet.model.Consulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    long countByStatus(String status);

    @Query("select coalesce(sum(c.valor), 0) from Consulta c where c.status = 'CONCLUIDA'")
    BigDecimal somarFaturamentoConcluido();

    @Query("""
            select c from Consulta c
            join c.pet p
            join c.veterinario v
            where (:petId is null or p.id = :petId)
              and (:veterinarioId is null or v.id = :veterinarioId)
              and (:status is null or upper(c.status) = upper(:status))
              and (:inicio is null or c.dataHora >= :inicio)
              and (:fim is null or c.dataHora <= :fim)
            """)
    Page<Consulta> buscarComFiltros(@Param("petId") Long petId,
                                     @Param("veterinarioId") Long veterinarioId,
                                     @Param("status") String status,
                                     @Param("inicio") LocalDateTime inicio,
                                     @Param("fim") LocalDateTime fim,
                                     Pageable pageable);
}
