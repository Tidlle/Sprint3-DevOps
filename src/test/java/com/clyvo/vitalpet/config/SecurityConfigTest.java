package com.clyvo.vitalpet.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void rotaWebSemLoginRedirecionaParaLogin() throws Exception {
        mockMvc.perform(get("/web/dashboard"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminAcessaDashboard() throws Exception {
        mockMvc.perform(get("/web/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VETERINARIO")
    void veterinarioAcessaDashboard() throws Exception {
        mockMvc.perform(get("/web/dashboard"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "VETERINARIO")
    void veterinarioNaoAcessaAreaAdministrativa() throws Exception {
        mockMvc.perform(get("/web/clinicas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminPassaPelaAutorizacaoDaAreaAdministrativa() throws Exception {
        // Sem controller de /web/clinicas ainda (chega no dispatcher e não acha handler) -
        // o importante aqui é confirmar que NÃO é bloqueado com 403 como o veterinário.
        mockMvc.perform(get("/web/clinicas"))
                .andExpect(status().isNotFound());
    }

    @Test
    void apiRestContinuaAbertaSemAutenticacao() throws Exception {
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk());
    }
}
