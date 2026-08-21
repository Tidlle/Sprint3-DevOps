package com.clyvo.vitalpet.web;

import com.clyvo.vitalpet.exception.RecursoNaoEncontradoException;
import com.clyvo.vitalpet.exception.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Cobre exceções de negócio que não são tratadas localmente em um controller
 * (ex.: acesso a um recurso inexistente por link direto, ou violação de regra
 * fora do fluxo de formulário, como a checagem de propriedade do veterinário
 * sobre as próprias consultas).
 */
@ControllerAdvice(basePackages = "com.clyvo.vitalpet.web")
public class WebExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String tratarNaoEncontrado(RecursoNaoEncontradoException ex, Model model) {
        model.addAttribute("mensagem", ex.getMessage());
        return "erro";
    }

    @ExceptionHandler(RegraNegocioException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String tratarRegraNegocio(RegraNegocioException ex, Model model) {
        model.addAttribute("mensagem", ex.getMessage());
        return "erro";
    }
}
