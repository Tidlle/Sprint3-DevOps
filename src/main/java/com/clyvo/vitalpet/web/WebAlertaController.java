package com.clyvo.vitalpet.web;

import com.clyvo.vitalpet.dto.AlertaResponse;
import com.clyvo.vitalpet.service.AlertaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/alertas")
public class WebAlertaController {

    private final AlertaService alertaService;

    public WebAlertaController(AlertaService alertaService) {
        this.alertaService = alertaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String status,
                          @RequestParam(required = false) String prioridade,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        Page<AlertaResponse> resultado = alertaService.listar(null, vazio(status), vazio(prioridade), null,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "dataAlerta")));
        model.addAttribute("pagina", resultado);
        model.addAttribute("status", status);
        model.addAttribute("prioridade", prioridade);
        return "alertas-lista";
    }

    @PostMapping("/{id}/resolver")
    public String resolver(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        AlertaResponse alerta = alertaService.resolver(id);
        String mensagem = alerta.acompanhamentoId() != null
                ? "Alerta resolvido. O acompanhamento vinculado também foi concluído automaticamente."
                : "Alerta resolvido.";
        redirectAttributes.addFlashAttribute("sucesso", mensagem);
        return "redirect:/web/alertas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        alertaService.cancelar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Alerta cancelado.");
        return "redirect:/web/alertas";
    }

    private String vazio(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }
}
