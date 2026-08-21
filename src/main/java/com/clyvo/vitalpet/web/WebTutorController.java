package com.clyvo.vitalpet.web;

import com.clyvo.vitalpet.dto.TutorRequest;
import com.clyvo.vitalpet.dto.TutorResponse;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/tutores")
public class WebTutorController {

    private final TutorService tutorService;

    public WebTutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Campos opcionais (endereco, cidade, estado, cep) chegam como "" quando o
        // formulário HTML é enviado em branco; @Size(min=2) em "estado" rejeitaria
        // string vazia, então convertemos para null, que o Bean Validation ignora.
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        Page<TutorResponse> resultado = tutorService.listar(nome, null, null, null,
                PageRequest.of(page, 10, Sort.by("nome")));
        model.addAttribute("pagina", resultado);
        model.addAttribute("nome", nome);
        return "tutores-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new TutorRequest("", "", "", "", "", "", "", ""));
        model.addAttribute("id", null);
        return "tutores-form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") TutorRequest form, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", null);
            return "tutores-form";
        }
        try {
            tutorService.criar(form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("id", null);
            model.addAttribute("erro", ex.getMessage());
            return "tutores-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Tutor cadastrado com sucesso.");
        return "redirect:/web/tutores";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        TutorResponse tutor = tutorService.buscarPorId(id);
        model.addAttribute("form", new TutorRequest(tutor.nome(), tutor.email(), tutor.telefone(), tutor.cpf(),
                tutor.endereco(), tutor.cidade(), tutor.estado(), tutor.cep()));
        model.addAttribute("id", id);
        return "tutores-form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") TutorRequest form,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id);
            return "tutores-form";
        }
        try {
            tutorService.atualizar(id, form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("id", id);
            model.addAttribute("erro", ex.getMessage());
            return "tutores-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Tutor atualizado com sucesso.");
        return "redirect:/web/tutores";
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tutorService.desativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Tutor desativado.");
        return "redirect:/web/tutores";
    }

    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tutorService.ativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Tutor ativado.");
        return "redirect:/web/tutores";
    }
}
