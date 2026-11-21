package com.classvoice.security;

import com.classvoice.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

/** Rejects login POSTs from a locked ip|username before authentication runs. Not a Spring bean on purpose. */
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final LoginAttemptService attempts;

    public LoginRateLimitFilter(LoginAttemptService attempts) {
        this.attempts = attempts;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return !("POST".equalsIgnoreCase(request.getMethod()) && "/login".equals(path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String key = request.getRemoteAddr() + "|" + UserService.normalize(request.getParameter("username"));
        if (attempts.isLocked(key)) {
            response.sendRedirect(request.getContextPath() + "/login?locked");
            return;
        }
        chain.doFilter(request, response);
    }
}
