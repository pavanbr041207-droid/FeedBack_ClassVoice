package com.classvoice.web;

import com.classvoice.repo.UserRepository;
import com.classvoice.security.LoginAttemptService;
import com.classvoice.security.RoleRoutes;
import com.classvoice.service.AuditService;
import com.classvoice.service.PasswordChangeResult;
import com.classvoice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Change own password. Any signed-in role. A successful change signs the user out of the session. */
@Controller
@RequestMapping("/account/password")
public class AccountController {

    private static final String VIEW = "account/password";

    private final UserService userService;
    private final UserRepository users;
    private final AuditService audit;
    private final LoginAttemptService attempts;

    public AccountController(UserService userService,
                             UserRepository users,
                             AuditService audit,
                             LoginAttemptService attempts) {
        this.userService = userService;
        this.users = users;
        this.audit = audit;
        this.attempts = attempts;
    }

    @GetMapping
    public String form(Model model, Authentication auth) {
        model.addAttribute("form", new PasswordChangeForm());
        model.addAttribute("homeUrl", RoleRoutes.homeFor(auth.getAuthorities()));
        return VIEW;
    }

    @PostMapping
    public String submit(@Valid @ModelAttribute("form") PasswordChangeForm form,
                         BindingResult result,
                         Authentication auth,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model) {
        model.addAttribute("homeUrl", RoleRoutes.homeFor(auth.getAuthorities()));
        String key = request.getRemoteAddr() + "|pwchange|" + UserService.normalize(auth.getName());

        if (attempts.isLocked(key)) {
            result.rejectValue("currentPassword", "locked", "Too many attempts. Try again later.");
            return VIEW;
        }
        if (!result.hasErrors() && !form.getNewPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
        }
        if (result.hasErrors()) {
            return VIEW;
        }

        PasswordChangeResult outcome =
                userService.changePassword(auth.getName(), form.getCurrentPassword(), form.getNewPassword());
        if (outcome == PasswordChangeResult.WRONG_CURRENT) {
            attempts.failed(key);
            result.rejectValue("currentPassword", "wrong", "Current password is incorrect");
            return VIEW;
        }
        if (outcome == PasswordChangeResult.SAME_AS_CURRENT) {
            result.rejectValue("newPassword", "same", "New password must be different");
            return VIEW;
        }

        attempts.succeeded(key);
        users.findByUsername(UserService.normalize(auth.getName())).ifPresent(u ->
                audit.log(u.getId(), "PASSWORD_CHANGED", "USER", String.valueOf(u.getId()), null));
        new SecurityContextLogoutHandler().logout(request, response, auth);
        return "redirect:/login?pwchanged";
    }
}
