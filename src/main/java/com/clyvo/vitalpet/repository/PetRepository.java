package com.clyvo.vitalpet.repository;

import com.clyvo.vitalpet.model.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends JpaRepository<Pet, Long> {

    long countByAtivoTrue();

    @Query("""
            select p from Pet p
            join p.tutor t
            where (:nome is null or lower(p.nome) like lower(concat('%', :nome, '%')))
              and (:especie is null or lower(p.especie) like lower(concat('%', :especie, '%')))
              and (:tutorId is null or t.id = :tutorId)
              and (:ativo is null or p.ativo = :ativo)
            """)
    Page<Pet> buscarComFiltros(@Param("nome") String nome,
                                @Param("especie") String especie,
                                @Param("tutorId") Long tutorId,
                                @Param("ativo") Boolean ativo,
                                Pageable pageable);
}
