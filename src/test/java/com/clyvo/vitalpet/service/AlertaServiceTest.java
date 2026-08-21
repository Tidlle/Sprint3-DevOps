package com.clyvo.vitalpet.service;

import com.clyvo.vitalpet.model.Acompanhamento;
import com.clyvo.vitalpet.repository.AcompanhamentoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class AlertaServiceTest {

    @Autowired
    private AlertaService alertaService;

    @Autowired
    private AcompanhamentoRepository acompanhamentoRepository;

    @Test
    void resolverAlertaConcluiAcompanhamentoAtivoVinculado() {
        // Massa do V2__seed_data.sql: alerta 1 (RETORNO do Rex) está vinculado ao
        // acompanhamento 1, que nasce com status ATIVO.
        Acompanhamento antesDoAlerta = acompanhamentoRepository.findById(1L).orElseThrow();
        assertThat(antesDoAlerta.getStatus()).isEqualTo("ATIVO");

        alertaService.resolver(1L);

        Acompanhamento depois = acompanhamentoRepository.findById(1L).orElseThrow();
        assertThat(depois.getStatus()).isEqualTo("CONCLUIDO");
        assertThat(depois.getDataFim()).isNotNull();
    }

    @Test
    void resolverAlertaSemAcompanhamentoNaoQuebra() {
        // Alerta 3 do seed (vacinação do Thor) não tem acompanhamento vinculado.
        var resposta = alertaService.resolver(3L);

        assertThat(resposta.status()).isEqualTo("RESOLVIDO");
        assertThat(resposta.dataResolucao()).isNotNull();
    }
}
