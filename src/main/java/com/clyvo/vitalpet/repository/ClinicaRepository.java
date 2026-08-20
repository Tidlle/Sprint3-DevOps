package com.clyvo.vitalpet.repository;

import com.clyvo.vitalpet.model.Clinica;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClinicaRepository extends JpaRepository<Clinica, Long> {

    Optional<Clinica> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    boolean existsByCnpjAndIdNot(String cnpj, Long id);

    long countByAtivaTrue();

    @Query("""
            select c from Clinica c
            where (:nome is null or lower(c.nome) like lower(concat('%', :nome, '%')))
              and (:cidade is null or lower(c.cidade) like lower(concat('%', :cidade, '%')))
              and (:estado is null or upper(c.estado) = upper(:estado))
              and (:ativa is null or c.ativa = :ativa)
            """)
    Page<Clinica> buscarComFiltros(@Param("nome") String nome,
                                    @Param("cidade") String cidade,
                                    @Param("estado") String estado,
                                    @Param("ativa") Boolean ativa,
                                    Pageable pageable);
}
