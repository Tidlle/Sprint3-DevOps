package com.clyvo.vitalpet.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebLoginController {

    @GetMapping("/web/login")
    public String login(@RequestParam(name = "erro", required = false) String erro,
                         @RequestParam(name = "logout", required = false) String logout,
                         Model model) {
        model.addAttribute("erro", erro != null);
        model.addAttribute("logout", logout != null);
        return "login";
    }
}
