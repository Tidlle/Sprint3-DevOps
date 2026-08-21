package com.clyvo.vitalpet.web;

import com.clyvo.vitalpet.dto.ConsultaAtualizacaoRequest;
import com.clyvo.vitalpet.dto.ConsultaRequest;
import com.clyvo.vitalpet.dto.ConsultaResponse;
import com.clyvo.vitalpet.dto.FinalizarConsultaRequest;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import com.clyvo.vitalpet.model.Role;
import com.clyvo.vitalpet.repository.UsuarioRepository;
import com.clyvo.vitalpet.service.ConsultaService;
import com.clyvo.vitalpet.service.PetService;
import com.clyvo.vitalpet.service.VeterinarioService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Um perfil VETERINARIO só enxerga e movimenta as próprias consultas: a listagem é
 * filtrada, o campo de veterinário no formulário fica travado no próprio usuário
 * (via campo oculto) e qualquer tentativa de acessar consulta de outro veterinário
 * por link direto é barrada em {@link #verificarAcesso}.
 */
@Controller
@RequestMapping("/web/consultas")
public class WebConsultaController {

    private final ConsultaService consultaService;
    private final PetService petService;
    private final VeterinarioService veterinarioService;
    private final UsuarioRepository usuarioRepository;

    public WebConsultaController(ConsultaService consultaService, PetService petService,
                                  VeterinarioService veterinarioService, UsuarioRepository usuarioRepository) {
        this.consultaService = consultaService;
        this.petService = petService;
        this.veterinarioService = veterinarioService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String status,
                          @RequestParam(defaultValue = "0") int page,
                          Authentication authentication, Model model) {
        Long veterinarioId = veterinarioIdRestrito(authentication);
        Page<ConsultaResponse> resultado = consultaService.listar(null, veterinarioId, vazio(status), null, null,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "dataHora")));
        model.addAttribute("pagina", resultado);
        model.addAttribute("status", status);
        return "consultas-lista";
    }

    @GetMapping("/novo")
    public String novo(Authentication authentication, Model model) {
        Long veterinarioRestrito = veterinarioIdRestrito(authentication);
        model.addAttribute("form", new ConsultaRequest(null, "", "", "", "", BigDecimal.ZERO, null, veterinarioRestrito));
        model.addAttribute("id", null);
        carregarListas(model, veterinarioRestrito);
        return "consultas-form";
    }

    @PostMapping
    public String criar(@Valid @ModelAttribute("form") ConsultaRequest form, BindingResult bindingResult,
                         Authentication authentication, Model model, RedirectAttributes redirectAttributes) {
        Long veterinarioRestrito = veterinarioIdRestrito(authentication);
        form = travarVeterinario(form, veterinarioRestrito);
        if (bindingResult.hasErrors()) {
            model.addAttribute("form", form);
            model.addAttribute("id", null);
            carregarListas(model, veterinarioRestrito);
            return "consultas-form";
        }
        try {
            consultaService.criar(form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("form", form);
            model.addAttribute("id", null);
            model.addAttribute("erro", ex.getMessage());
            carregarListas(model, veterinarioRestrito);
            return "consultas-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Consulta agendada com sucesso.");
        return "redirect:/web/consultas";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Authentication authentication, Model model) {
        Long veterinarioRestrito = veterinarioIdRestrito(authentication);
        ConsultaResponse consulta = consultaService.buscarPorId(id);
        verificarAcesso(consulta, veterinarioRestrito);
        model.addAttribute("form", new ConsultaRequest(consulta.dataHora(), consulta.tipo(), consulta.sintomas(),
                consulta.diagnostico(), consulta.tratamento(), consulta.valor(), consulta.petId(), consulta.veterinarioId()));
        model.addAttribute("id", id);
        model.addAttribute("statusAtual", consulta.status());
        carregarListas(model, veterinarioRestrito);
        return "consultas-form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("form") ConsultaRequest form,
                             BindingResult bindingResult, Authentication authentication, Model model,
                             RedirectAttributes redirectAttributes) {
        Long veterinarioRestrito = veterinarioIdRestrito(authentication);
        ConsultaResponse existente = consultaService.buscarPorId(id);
        verificarAcesso(existente, veterinarioRestrito);
        form = travarVeterinario(form, veterinarioRestrito);
        if (bindingResult.hasErrors()) {
            model.addAttribute("form", form);
            model.addAttribute("id", id);
            model.addAttribute("statusAtual", existente.status());
            carregarListas(model, veterinarioRestrito);
            return "consultas-form";
        }
        ConsultaAtualizacaoRequest atualizacao = new ConsultaAtualizacaoRequest(form.dataHora(), form.tipo(),
                form.sintomas(), form.diagnostico(), form.tratamento(), form.valor(), form.petId(), form.veterinarioId());
        try {
            consultaService.atualizar(id, atualizacao);
        } catch (RegraNegocioException ex) {
            model.addAttribute("form", form);
            model.addAttribute("id", id);
            model.addAttribute("statusAtual", existente.status());
            model.addAttribute("erro", ex.getMessage());
            carregarListas(model, veterinarioRestrito);
            return "consultas-form";
        }
        redirectAttributes.addFlashAttribute("sucesso", "Consulta atualizada com sucesso.");
        return "redirect:/web/consultas";
    }

    @GetMapping("/{id}/finalizar")
    public String telaFinalizar(@PathVariable Long id, Authentication authentication, Model model) {
        ConsultaResponse consulta = consultaService.buscarPorId(id);
        verificarAcesso(consulta, veterinarioIdRestrito(authentication));
        model.addAttribute("consulta", consulta);
        model.addAttribute("form", new FinalizarConsultaRequest(consulta.diagnostico(), consulta.tratamento(), ""));
        return "consultas-finalizar";
    }

    @PostMapping("/{id}/finalizar")
    public String finalizar(@PathVariable Long id, @Valid @ModelAttribute("form") FinalizarConsultaRequest form,
                             BindingResult bindingResult, Authentication authentication, Model model,
                             RedirectAttributes redirectAttributes) {
        ConsultaResponse consulta = consultaService.buscarPorId(id);
        verificarAcesso(consulta, veterinarioIdRestrito(authentication));
        if (bindingResult.hasErrors()) {
            model.addAttribute("consulta", consulta);
            return "consultas-finalizar";
        }
        try {
            consultaService.finalizar(id, form);
        } catch (RegraNegocioException ex) {
            model.addAttribute("consulta", consulta);
            model.addAttribute("erro", ex.getMessage());
            return "consultas-finalizar";
        }
        boolean gerouAcompanhamento = form.descricaoAcompanhamento() != null && !form.descricaoAcompanhamento().isBlank();
        redirectAttributes.addFlashAttribute("sucesso", gerouAcompanhamento
                ? "Consulta finalizada. Acompanhamento criado e alerta de retorno agendado para daqui a 7 dias."
                : "Consulta finalizada com sucesso.");
        return "redirect:/web/consultas";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        ConsultaResponse consulta = consultaService.buscarPorId(id);
        verificarAcesso(consulta, veterinarioIdRestrito(authentication));
        consultaService.cancelar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Consulta cancelada.");
        return "redirect:/web/consultas";
    }

    private ConsultaRequest travarVeterinario(ConsultaRequest form, Long veterinarioRestrito) {
        if (veterinarioRestrito == null) {
            return form;
        }
        return new ConsultaRequest(form.dataHora(), form.tipo(), form.sintomas(), form.diagnostico(),
                form.tratamento(), form.valor(), form.petId(), veterinarioRestrito);
    }

    private void carregarListas(Model model, Long veterinarioRestrito) {
        model.addAttribute("pets", petService.listar(null, null, null, true, PageRequest.of(0, 500, Sort.by("nome"))).getContent());
        if (veterinarioRestrito != null) {
            model.addAttribute("veterinarios", List.of(veterinarioService.buscarPorId(veterinarioRestrito)));
            model.addAttribute("veterinarioRestrito", true);
        } else {
            model.addAttribute("veterinarios", veterinarioService.listar(null, null, null, true, PageRequest.of(0, 500, Sort.by("nome"))).getContent());
            model.addAttribute("veterinarioRestrito", false);
        }
    }

    private Long veterinarioIdRestrito(Authentication authentication) {
        var usuario = usuarioRepository.findByEmailIgnoreCase(authentication.getName()).orElseThrow();
        if (usuario.getRole() == Role.VETERINARIO && usuario.getVeterinario() != null) {
            return usuario.getVeterinario().getId();
        }
        return null;
    }

    private void verificarAcesso(ConsultaResponse consulta, Long veterinarioRestrito) {
        if (veterinarioRestrito != null && !veterinarioRestrito.equals(consulta.veterinarioId())) {
            throw new RegraNegocioException("Você só pode acessar as próprias consultas.");
        }
    }

    private String vazio(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor;
    }
}
