package com.classvoice.web;

import com.classvoice.security.LoginAttemptService;
import com.classvoice.service.SetupService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

/** One-time First-Admin onboarding. Returns 404 whenever setup is closed or disabled. */
@Controller
public class SetupController {

    private final SetupService setup;
    private final LoginAttemptService attempts;

    public SetupController(SetupService setup, LoginAttemptService attempts) {
        this.setup = setup;
        this.attempts = attempts;
    }

    @GetMapping("/setup")
    public String form(Model model) {
        requireOpen();
        model.addAttribute("form", new SetupForm());
        return "setup";
    }

    @PostMapping("/setup")
    public String submit(@Valid @ModelAttribute("form") SetupForm form,
                         BindingResult result,
                         HttpServletRequest request) {
        requireOpen();
        String ip = request.getRemoteAddr();
        String key = ip + "|setup";
        if (attempts.isLocked(key)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        }
        if (!result.hasErrors() && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
        }
        if (result.hasErrors()) {
            return "setup";
        }
        if (!setup.tokenMatches(form.getSetupToken())) {
            attempts.failed(key);
            result.rejectValue("setupToken", "invalid", "Invalid setup token");
            return "setup";
        }
        try {
            setup.createAdmin(form.getUsername(), form.getPassword(), form.getDisplayName().trim(), ip);
        } catch (IllegalStateException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException ex) {
            result.rejectValue("username", "taken", "Username is not available");
            return "setup";
        }
        attempts.succeeded(key);
        return "redirect:/login?setup";
    }

    private void requireOpen() {
        if (!setup.isAvailable()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
