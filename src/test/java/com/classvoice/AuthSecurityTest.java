package com.classvoice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.classvoice.domain.RoleName;
import com.classvoice.domain.User;
import com.classvoice.domain.UserStatus;
import com.classvoice.repo.AuditLogRepository;
import com.classvoice.repo.UserRepository;
import com.classvoice.security.LoginAttemptService;
import com.classvoice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSecurityTest {

    private static final String PW = "Correct-Horse-9";

    @Autowired MockMvc mvc;
    @Autowired UserService userService;
    @Autowired UserRepository userRepo;
    @Autowired AuditLogRepository auditRepo;
    @Autowired LoginAttemptService attempts;
    @Autowired PasswordEncoder encoder;

    @BeforeEach
    void clean() {
        auditRepo.deleteAll();
        userRepo.deleteAll();
        attempts.reset();
    }

    @Test
    void anonymousIsRedirectedToLogin() throws Exception {
        mvc.perform(get("/admin/home"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void adminLoginSucceedsAndLandsOnAdminHome() throws Exception {
        userService.createUser("admin1", PW, "Admin One", RoleName.COLLEGE_ADMIN);
        mvc.perform(post("/login").with(csrf()).param("username", "admin1").param("password", PW))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/home"));
        assertThat(auditRepo.countByAction("LOGIN_SUCCESS")).isEqualTo(1);
        assertThat(userRepo.findByUsername("admin1").orElseThrow().getLastLoginAt()).isNotNull();
    }

    @Test
    void usernameIsCaseInsensitive() throws Exception {
        userService.createUser("teacher1", PW, "Teacher One", RoleName.TEACHER);
        mvc.perform(post("/login").with(csrf()).param("username", "  Teacher1 ").param("password", PW))
                .andExpect(redirectedUrl("/teacher/home"));
    }

    @Test
    void wrongPasswordIsRejectedAndAudited() throws Exception {
        userService.createUser("admin1", PW, "Admin One", RoleName.COLLEGE_ADMIN);
        mvc.perform(post("/login").with(csrf()).param("username", "admin1").param("password", "nope-nope-nope"))
                .andExpect(redirectedUrl("/login?error"));
        assertThat(auditRepo.countByAction("LOGIN_FAILURE")).isEqualTo(1);
    }

    @Test
    void unknownUserGetsSameErrorAsWrongPassword() throws Exception {
        mvc.perform(post("/login").with(csrf()).param("username", "ghost").param("password", PW))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void disabledUserCannotLogin() throws Exception {
        User u = userService.createUser("student1", PW, "Student One", RoleName.STUDENT);
        u.setStatus(UserStatus.DISABLED);
        userRepo.save(u);
        mvc.perform(post("/login").with(csrf()).param("username", "student1").param("password", PW))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void loginWithoutCsrfTokenIsForbidden() throws Exception {
        userService.createUser("admin1", PW, "Admin One", RoleName.COLLEGE_ADMIN);
        mvc.perform(post("/login").param("username", "admin1").param("password", PW))
                .andExpect(status().isForbidden());
    }

    @Test
    void passwordIsStoredAsBcryptHash() {
        userService.createUser("admin1", PW, "Admin One", RoleName.COLLEGE_ADMIN);
        String hash = userRepo.findByUsername("admin1").orElseThrow().getPasswordHash();
        assertThat(hash).isNotEqualTo(PW).startsWith("$2");
        assertThat(encoder.matches(PW, hash)).isTrue();
    }

    @Test
    void roleGuardsBlockWrongRoles() throws Exception {
        mvc.perform(get("/admin/home").with(user("t").roles("TEACHER"))).andExpect(status().isForbidden());
        mvc.perform(get("/admin/home").with(user("s").roles("STUDENT"))).andExpect(status().isForbidden());
        mvc.perform(get("/teacher/home").with(user("a").roles("COLLEGE_ADMIN"))).andExpect(status().isForbidden());
        mvc.perform(get("/teacher/home").with(user("s").roles("STUDENT"))).andExpect(status().isForbidden());
        mvc.perform(get("/student/home").with(user("t").roles("TEACHER"))).andExpect(status().isForbidden());
    }

    @Test
    void eachRoleSeesOwnHomeWithCollegeNameFromDatabase() throws Exception {
        mvc.perform(get("/admin/home").with(user("a").roles("COLLEGE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Your College")));
        mvc.perform(get("/teacher/home").with(user("t").roles("TEACHER"))).andExpect(status().isOk());
        mvc.perform(get("/student/home").with(user("s").roles("STUDENT"))).andExpect(status().isOk());
    }

    @Test
    void loginIsLockedAfterFiveFailures() throws Exception {
        userService.createUser("admin1", PW, "Admin One", RoleName.COLLEGE_ADMIN);
        for (int i = 0; i < 5; i++) {
            mvc.perform(post("/login").with(csrf()).param("username", "admin1").param("password", "bad-password-" + i))
                    .andExpect(redirectedUrl("/login?error"));
        }
        mvc.perform(post("/login").with(csrf()).param("username", "admin1").param("password", PW))
                .andExpect(redirectedUrl("/login?locked"));
    }

    @Test
    void healthEndpointIsPublic() throws Exception {
        mvc.perform(get("/healthz")).andExpect(status().isOk()).andExpect(content().string("ok"));
    }
}
