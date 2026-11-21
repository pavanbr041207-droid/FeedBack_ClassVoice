package com.classvoice.web;

import com.classvoice.security.RoleRoutes;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        return "redirect:" + RoleRoutes.homeFor(authentication.getAuthorities());
    }

    @GetMapping(value = "/healthz", produces = "text/plain")
    @ResponseBody
    public String health() {
        return "ok";
    }
}
