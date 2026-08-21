package com.clyvo.vitalpet.web;

import com.clyvo.vitalpet.dto.ClinicaResponse;
import com.clyvo.vitalpet.dto.VeterinarioRequest;
import com.clyvo.vitalpet.dto.VeterinarioResponse;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.service.ClinicaService;
import com.clyvo.vitalpet.service.VeterinarioService;
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
@RequestMapping("/web/veterinarios")
public class WebVeterinarioController {

    private final VeterinarioService veterinarioService;
    private final ClinicaService clinicaService;

    public WebVeterinarioController(VeterinarioService veterinarioService, ClinicaService clinicaService) {
        this.veterinarioService = veterinarioService;
        this.clinicaService = clinicaService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        Page<VeterinarioResponse> resultado = veterinarioService.listar(nome, null, null, null,
                PageRequest.of(page, 10, Sort.by("nome")));
        model.addAttribute("pagina", resultado);
        model.addAttribute("nome", nome);
        return "veterinarios-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new VeterinarioRequest("", "", "", "", "", null));
        model.addAttribute("id", null);
        carregarClinicas(model);
        return "veterinarios-form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") VeterinarioRequest form, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", null);
            carregarClinicas(model);
            return "veterinarios-form";
        }
        try {
            veterinarioService.criar(form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("id", null);
            model.addAttribute("erro", ex.getMessage());
            carregarClinicas(model);
            return "veterinarios-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Veterinário cadastrado com sucesso.");
        return "redirect:/web/veterinarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        VeterinarioResponse veterinario = veterinarioService.buscarPorId(id);
        model.addAttribute("form", new VeterinarioRequest(veterinario.nome(), veterinario.email(), veterinario.telefone(),
                veterinario.crmv(), veterinario.especialidade(), veterinario.clinicaId()));
        model.addAttribute("id", id);
        carregarClinicas(model);
        return "veterinarios-form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") VeterinarioRequest form,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id);
            carregarClinicas(model);
            return "veterinarios-form";
        }
        try {
            veterinarioService.atualizar(id, form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("id", id);
            model.addAttribute("erro", ex.getMessage());
            carregarClinicas(model);
            return "veterinarios-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Veterinário atualizado com sucesso.");
        return "redirect:/web/veterinarios";
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        veterinarioService.desativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Veterinário desativado.");
        return "redirect:/web/veterinarios";
    }

    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        veterinarioService.ativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Veterinário ativado.");
        return "redirect:/web/veterinarios";
    }

    private void carregarClinicas(Model model) {
        Page<ClinicaResponse> clinicas = clinicaService.listar(null, null, null, true, PageRequest.of(0, 500, Sort.by("nome")));
        model.addAttribute("clinicas", clinicas.getContent());
    }
}
