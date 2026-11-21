package com.classvoice.security;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

/**
 * In-memory login/setup attempt limiter. State is per application instance and resets on restart
 * (see KNOWN_ISSUES.md). Key format: "ip|username".
 */
@Service
public class LoginAttemptService {

    static final int MAX_FAILURES = 5;
    static final Duration LOCK_DURATION = Duration.ofMinutes(15);

    private record State(int failures, Instant lockedUntil) {
    }

    private final ConcurrentHashMap<String, State> states = new ConcurrentHashMap<>();

    public boolean isLocked(String key) {
        State s = states.get(key);
        if (s == null || s.lockedUntil() == null) {
            return false;
        }
        if (Instant.now().isAfter(s.lockedUntil())) {
            states.remove(key);
            return false;
        }
        return true;
    }

    public void failed(String key) {
        states.compute(key, (k, old) -> {
            int failures = (old == null ? 0 : old.failures()) + 1;
            Instant until = failures >= MAX_FAILURES ? Instant.now().plus(LOCK_DURATION) : null;
            return new State(failures, until);
        });
    }

    public void succeeded(String key) {
        states.remove(key);
    }

    public void reset() {
        states.clear();
    }
}
