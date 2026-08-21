package com.clyvo.vitalpet.web;

import com.clyvo.vitalpet.dto.PetRequest;
import com.clyvo.vitalpet.dto.PetResponse;
import com.clyvo.vitalpet.dto.TutorResponse;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.service.PetService;
import com.clyvo.vitalpet.service.TutorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/web/pets")
public class WebPetController {

    private final PetService petService;
    private final TutorService tutorService;

    public WebPetController(PetService petService, TutorService tutorService) {
        this.petService = petService;
        this.tutorService = tutorService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String nome,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        Page<PetResponse> resultado = petService.listar(nome, null, null, null,
                PageRequest.of(page, 10, Sort.by("nome")));
        model.addAttribute("pagina", resultado);
        model.addAttribute("nome", nome);
        return "pets-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("form", new PetRequest("", "", "", null, "", BigDecimal.ZERO, "", null));
        model.addAttribute("id", null);
        carregarTutores(model);
        return "pets-form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") PetRequest form, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", null);
            carregarTutores(model);
            return "pets-form";
        }
        try {
            petService.criar(form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("id", null);
            model.addAttribute("erro", ex.getMessage());
            carregarTutores(model);
            return "pets-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Pet cadastrado com sucesso.");
        return "redirect:/web/pets";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        PetResponse pet = petService.buscarPorId(id);
        model.addAttribute("form", new PetRequest(pet.nome(), pet.especie(), pet.raca(), pet.dataNascimento(),
                pet.sexo(), pet.peso(), pet.observacoes(), pet.tutorId()));
        model.addAttribute("id", id);
        carregarTutores(model);
        return "pets-form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") PetRequest form,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("id", id);
            carregarTutores(model);
            return "pets-form";
        }
        try {
            petService.atualizar(id, form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("id", id);
            model.addAttribute("erro", ex.getMessage());
            carregarTutores(model);
            return "pets-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Pet atualizado com sucesso.");
        return "redirect:/web/pets";
    }

    @PostMapping("/{id}/desativar")
    public String desativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        petService.desativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Pet desativado.");
        return "redirect:/web/pets";
    }

    @PostMapping("/{id}/ativar")
    public String ativar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        petService.ativar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Pet ativado.");
        return "redirect:/web/pets";
    }

    private void carregarTutores(Model model) {
        Page<TutorResponse> tutores = tutorService.listar(null, null, null, true, PageRequest.of(0, 500, Sort.by("nome")));
        model.addAttribute("tutores", tutores.getContent());
    }
}
