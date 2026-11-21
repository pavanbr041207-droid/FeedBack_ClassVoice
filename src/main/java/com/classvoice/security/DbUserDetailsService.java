package com.classvoice.security;

import com.classvoice.domain.UserStatus;
import com.classvoice.repo.UserRepository;
import com.classvoice.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public DbUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.classvoice.domain.User user = users.findByUsername(UserService.normalize(username))
                .orElseThrow(() -> new UsernameNotFoundException("Bad credentials"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .roles(user.getRole().getName())
                .disabled(user.getStatus() != UserStatus.ACTIVE)
                .build();
    }
}
