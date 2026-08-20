package com.clyvo.vitalpet.web;

import com.clyvo.vitalpet.repository.UsuarioRepository;
import com.clyvo.vitalpet.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebDashboardController {

    private final DashboardService dashboardService;
    private final UsuarioRepository usuarioRepository;

    public WebDashboardController(DashboardService dashboardService, UsuarioRepository usuarioRepository) {
        this.dashboardService = dashboardService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/web/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        model.addAttribute("resumo", dashboardService.resumo());
        usuarioRepository.findByEmailIgnoreCase(authentication.getName())
                .ifPresent(usuario -> {
                    model.addAttribute("usuarioNome", usuario.getNome());
                    model.addAttribute("usuarioRole", usuario.getRole().name());
                });
        return "dashboard";
    }
}
