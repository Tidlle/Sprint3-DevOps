package com.clyvo.vitalpet.repository;

import com.clyvo.vitalpet.model.Acompanhamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AcompanhamentoRepository extends JpaRepository<Acompanhamento, Long> {

    boolean existsByConsultaId(Long consultaId);

    @Query("""
            select a from Acompanhamento a
            join a.consulta c
            join c.pet p
            where (:consultaId is null or c.id = :consultaId)
              and (:petId is null or p.id = :petId)
              and (:status is null or upper(a.status) = upper(:status))
            """)
    Page<Acompanhamento> buscarComFiltros(@Param("consultaId") Long consultaId,
                                           @Param("petId") Long petId,
                                           @Param("status") String status,
                                           Pageable pageable);
}
