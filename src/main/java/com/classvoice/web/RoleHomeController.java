package com.classvoice.web;

import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Placeholder landing pages for each role. Real features arrive in later sessions. */
@Controller
public class RoleHomeController {

    @GetMapping("/admin/home")
    public String admin(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "admin/home";
    }

    @GetMapping("/teacher/home")
    public String teacher(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "teacher/home";
    }

    @GetMapping("/student/home")
    public String student(Principal principal, Model model) {
        model.addAttribute("username", principal.getName());
        return "student/home";
    }
}
