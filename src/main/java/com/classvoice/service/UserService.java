package com.classvoice.service;

import com.classvoice.domain.Role;
import com.classvoice.domain.User;
import com.classvoice.domain.UserStatus;
import com.classvoice.repo.RoleRepository;
import com.classvoice.repo.UserRepository;
import java.time.Instant;
import java.util.Locale;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository users;
    private final RoleRepository roles;
    private final PasswordEncoder encoder;

    public UserService(UserRepository users, RoleRepository roles, PasswordEncoder encoder) {
        this.users = users;
        this.roles = roles;
        this.encoder = encoder;
    }

    public static String normalize(String username) {
        return username == null ? "" : username.trim().toLowerCase(Locale.ROOT);
    }

    @Transactional
    public User createUser(String username, String rawPassword, String displayName, String roleName) {
        String normalized = normalize(username);
        if (users.findByUsername(normalized).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        Role role = roles.findByName(roleName)
                .orElseThrow(() -> new IllegalStateException("Unknown role: " + roleName));
        return users.save(new User(normalized, encoder.encode(rawPassword), role, UserStatus.ACTIVE, displayName));
    }

    @Transactional
    public void recordLogin(String username) {
        users.findByUsername(normalize(username)).ifPresent(u -> u.setLastLoginAt(Instant.now()));
    }

    /** Verifies the current password, rejects reuse, stores the new BCrypt hash. Caller audits after commit. Never logs passwords. */
    @Transactional
    public PasswordChangeResult changePassword(String username, String currentPassword, String newPassword) {
        User user = users.findByUsername(normalize(username)).orElse(null);
        if (user == null || !encoder.matches(currentPassword, user.getPasswordHash())) {
            return PasswordChangeResult.WRONG_CURRENT;
        }
        if (encoder.matches(newPassword, user.getPasswordHash())) {
            return PasswordChangeResult.SAME_AS_CURRENT;
        }
        user.setPasswordHash(encoder.encode(newPassword));
        return PasswordChangeResult.OK;
    }
}
