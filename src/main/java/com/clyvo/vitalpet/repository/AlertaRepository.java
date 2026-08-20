package com.clyvo.vitalpet.repository;

import com.clyvo.vitalpet.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlertaRepository extends JpaRepository<Alerta, Long> {

    long countByStatus(String status);

    long countByStatusAndPrioridadeIn(String status, java.util.Collection<String> prioridades);

    @Query("""
            select a from Alerta a
            join a.pet p
            where (:petId is null or p.id = :petId)
              and (:status is null or upper(a.status) = upper(:status))
              and (:prioridade is null or upper(a.prioridade) = upper(:prioridade))
              and (:tipo is null or lower(a.tipo) like lower(concat('%', :tipo, '%')))
            """)
    Page<Alerta> buscarComFiltros(@Param("petId") Long petId,
                                   @Param("status") String status,
                                   @Param("prioridade") String prioridade,
                                   @Param("tipo") String tipo,
                                   Pageable pageable);
}
