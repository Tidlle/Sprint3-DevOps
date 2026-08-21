package com.clyvo.vitalpet.web;

import com.clyvo.vitalpet.dto.ClinicaRequest;
import com.clyvo.vitalpet.dto.ClinicaResponse;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.service.ClinicaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/clinicas")
public class WebClinicaController {

    private final ClinicaService clinicaService;

    public WebClinicaController(ClinicaService clinicaService) {
        this.clinicaService = clinicaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        Page<ClinicaResponse> resultado = clinicaService.listar(nome, null, null, null,
                PageRequest.of(page, 10, Sort.by("nome")));
        model.addAttribute("pagina", resultado);
        model.addAttribute("nome", nome);
        return "clinicas-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new ClinicaRequest("", "", "", "", "", "", "", ""));
        model.addAttribute("id", null);
        return "clinicas-form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ClinicaRequest form, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", null);
            return "clinicas-form";
        }
        try {
            clinicaService.criar(form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("id", null);
            model.addAttribute("erro", ex.getMessage());
            return "clinicas-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Clínica cadastrada com sucesso.");
        return "redirect:/web/clinicas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        ClinicaResponse clinica = clinicaService.buscarPorId(id);
        model.addAttribute("form", new ClinicaRequest(clinica.nome(), clinica.endereco(), clinica.cidade(),
                clinica.estado(), clinica.cep(), clinica.telefone(), clinica.email(), clinica.cnpj()));
        model.addAttribute("id", id);
        return "clinicas-form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") ClinicaRequest form,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id);
            return "clinicas-form";
        }
        try {
            clinicaService.atualizar(id, form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("id", id);
            model.addAttribute("erro", ex.getMessage());
            return "clinicas-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Clínica atualizada com sucesso.");
        return "redirect:/web/clinicas";
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clinicaService.desativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Clínica desativada.");
        return "redirect:/web/clinicas";
    }

    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        clinicaService.ativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Clínica ativada.");
        return "redirect:/web/clinicas";
    }
}
