package com.classvoice.service;

import com.classvoice.domain.RoleName;
import com.classvoice.domain.User;
import com.classvoice.repo.UserRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** First-Admin onboarding. Open only while no COLLEGE_ADMIN exists and a setup token is configured. */
@Service
public class SetupService {

    private final UserRepository users;
    private final UserService userService;
    private final AuditService audit;
    private final String configuredToken;

    public SetupService(UserRepository users,
                        UserService userService,
                        AuditService audit,
                        @Value("${classvoice.setup-token:}") String configuredToken) {
        this.users = users;
        this.userService = userService;
        this.audit = audit;
        this.configuredToken = configuredToken == null ? "" : configuredToken;
    }

    public boolean isAvailable() {
        return !configuredToken.isBlank() && !users.existsByRole_Name(RoleName.COLLEGE_ADMIN);
    }

    public boolean tokenMatches(String supplied) {
        if (configuredToken.isBlank() || supplied == null) {
            return false;
        }
        return MessageDigest.isEqual(
                configuredToken.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8));
    }

    /** Not transactional on purpose: the user must be committed before the audit row (separate transaction) references it. */
    public void createAdmin(String username, String password, String displayName, String ip) {
        if (!isAvailable()) {
            throw new IllegalStateException("Setup is closed");
        }
        User admin = userService.createUser(username, password, displayName, RoleName.COLLEGE_ADMIN);
        audit.log(admin.getId(), "SETUP_ADMIN_CREATED", "USER", String.valueOf(admin.getId()), "ip=" + ip);
    }
}
