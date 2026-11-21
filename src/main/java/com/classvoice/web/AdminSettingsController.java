package com.classvoice.web;

import com.classvoice.repo.UserRepository;
import com.classvoice.service.AuditService;
import com.classvoice.service.CollegeSettingsService;
import com.classvoice.service.InvalidLogoException;
import com.classvoice.service.LogoService;
import com.classvoice.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/** College branding and settings. Whole /admin/** tree is restricted to COLLEGE_ADMIN in SecurityConfig. */
@Controller
@RequestMapping("/admin/settings")
public class AdminSettingsController {

    private final CollegeSettingsService settings;
    private final LogoService logos;
    private final AuditService audit;
    private final UserRepository users;

    public AdminSettingsController(CollegeSettingsService settings,
                                   LogoService logos,
                                   AuditService audit,
                                   UserRepository users) {
        this.settings = settings;
        this.logos = logos;
        this.audit = audit;
        this.users = users;
    }

    @GetMapping
    public String view(Model model) {
        model.addAttribute("form", new SettingsForm(settings.displayName()));
        return "admin/settings";
    }

    @PostMapping("/name")
    public String saveName(@Valid @ModelAttribute("form") SettingsForm form,
                           BindingResult result,
                           Principal principal) {
        if (result.hasErrors()) {
            return "admin/settings";
        }
        settings.updateDisplayName(actorId(principal), form.getDisplayName());
        return "redirect:/admin/settings?saved";
    }

    @PostMapping("/logo")
    public String saveLogo(@RequestParam("logo") MultipartFile file, Principal principal) {
        try {
            long size = file.getSize();
            String type = logos.replace(file);
            audit.log(actorId(principal), "LOGO_CHANGED", "SETTING", "college.logo", "type=" + type + ";bytes=" + size);
            return "redirect:/admin/settings?logoSaved";
        } catch (InvalidLogoException e) {
            return "redirect:/admin/settings?logoError=" + e.getCode();
        }
    }

    private Long actorId(Principal principal) {
        return users.findByUsername(UserService.normalize(principal.getName()))
                .map(u -> u.getId())
                .orElse(null);
    }
}
