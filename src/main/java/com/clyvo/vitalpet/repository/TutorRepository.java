package com.clyvo.vitalpet.repository;

import com.clyvo.vitalpet.model.Tutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    long countByAtivoTrue();

    @Query("""
            select t from Tutor t
            where (:nome is null or lower(t.nome) like lower(concat('%', :nome, '%')))
              and (:cpf is null or t.cpf = :cpf)
              and (:cidade is null or lower(t.cidade) like lower(concat('%', :cidade, '%')))
              and (:ativo is null or t.ativo = :ativo)
            """)
    Page<Tutor> buscarComFiltros(@Param("nome") String nome,
                                  @Param("cpf") String cpf,
                                  @Param("cidade") String cidade,
                                  @Param("ativo") Boolean ativo,
                                  Pageable pageable);
}
