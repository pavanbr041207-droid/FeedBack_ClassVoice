package com.classvoice.security;

import com.classvoice.repo.UserRepository;
import com.classvoice.service.AuditService;
import com.classvoice.service.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

/** Counts failures, records last login and writes audit entries. Never records the submitted username or password. */
@Component
public class AuthEventListener {

    private final LoginAttemptService attempts;
    private final UserService userService;
    private final UserRepository users;
    private final AuditService audit;

    public AuthEventListener(LoginAttemptService attempts,
                             UserService userService,
                             UserRepository users,
                             AuditService audit) {
        this.attempts = attempts;
        this.userService = userService;
        this.users = users;
        this.audit = audit;
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        Authentication auth = event.getAuthentication();
        String ip = clientIp(auth);
        attempts.failed(ip + "|" + UserService.normalize(auth.getName()));
        audit.log(null, "LOGIN_FAILURE", "USER", null, "ip=" + ip);
    }

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        Authentication auth = event.getAuthentication();
        String ip = clientIp(auth);
        String name = auth.getName();
        attempts.succeeded(ip + "|" + UserService.normalize(name));
        userService.recordLogin(name);
        users.findByUsername(UserService.normalize(name)).ifPresent(u ->
                audit.log(u.getId(), "LOGIN_SUCCESS", "USER", String.valueOf(u.getId()), "ip=" + ip));
    }

    private static String clientIp(Authentication auth) {
        if (auth.getDetails() instanceof WebAuthenticationDetails details) {
            return details.getRemoteAddress();
        }
        return "unknown";
    }
}
