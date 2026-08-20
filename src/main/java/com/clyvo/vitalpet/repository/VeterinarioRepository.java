package com.clyvo.vitalpet.repository;

import com.clyvo.vitalpet.model.Veterinario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VeterinarioRepository extends JpaRepository<Veterinario, Long> {

    Optional<Veterinario> findByCrmv(String crmv);

    boolean existsByCrmv(String crmv);

    boolean existsByCrmvAndIdNot(String crmv, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    long countByAtivoTrue();

    @Query("""
            select v from Veterinario v
            join v.clinica c
            where (:nome is null or lower(v.nome) like lower(concat('%', :nome, '%')))
              and (:especialidade is null or lower(v.especialidade) like lower(concat('%', :especialidade, '%')))
              and (:clinicaId is null or c.id = :clinicaId)
              and (:ativo is null or v.ativo = :ativo)
            """)
    Page<Veterinario> buscarComFiltros(@Param("nome") String nome,
                                        @Param("especialidade") String especialidade,
                                        @Param("clinicaId") Long clinicaId,
                                        @Param("ativo") Boolean ativo,
                                        Pageable pageable);
}
