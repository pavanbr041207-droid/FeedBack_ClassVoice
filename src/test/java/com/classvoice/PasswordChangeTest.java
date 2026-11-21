package com.classvoice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.classvoice.domain.RoleName;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PasswordChangeTest {

    private static final String OLD = "Old-Password-1";
    private static final String NEW = "New-Password-22";

    @Autowired MockMvc mvc;
    @Autowired UserService userService;
    @Autowired UserRepository userRepo;
    @Autowired AuditLogRepository auditRepo;
    @Autowired LoginAttemptService attempts;
    @Autowired PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        auditRepo.deleteAll();
        userRepo.deleteAll();
        attempts.reset();
        userService.createUser("teacher1", OLD, "Teacher One", RoleName.TEACHER);
    }

    private MockHttpServletRequestBuilder change(String current, String next, String confirm) {
        return post("/account/password").with(csrf()).with(user("teacher1").roles("TEACHER"))
                .param("currentPassword", current)
                .param("newPassword", next)
                .param("confirmPassword", confirm);
    }

    private String hash() {
        return userRepo.findByUsername("teacher1").orElseThrow().getPasswordHash();
    }

    @Test
    void everyRoleCanOpenThePage() throws Exception {
        mvc.perform(get("/account/password").with(user("a").roles("COLLEGE_ADMIN"))).andExpect(status().isOk());
        mvc.perform(get("/account/password").with(user("t").roles("TEACHER"))).andExpect(status().isOk());
        mvc.perform(get("/account/password").with(user("s").roles("STUDENT"))).andExpect(status().isOk());
    }

    @Test
    void anonymousCannotChangePassword() throws Exception {
        mvc.perform(post("/account/password").with(csrf())
                        .param("currentPassword", OLD).param("newPassword", NEW).param("confirmPassword", NEW))
                .andExpect(status().is3xxRedirection());
        assertThat(encoder.matches(OLD, hash())).isTrue();
    }

    @Test
    void successChangesHashAuditsAndSignsOut() throws Exception {
        mvc.perform(change(OLD, NEW, NEW)).andExpect(redirectedUrl("/login?pwchanged"));
        assertThat(encoder.matches(NEW, hash())).isTrue();
        assertThat(encoder.matches(OLD, hash())).isFalse();
        assertThat(hash()).startsWith("$2");
        assertThat(auditRepo.countByAction("PASSWORD_CHANGED")).isEqualTo(1);
    }

    @Test
    void newPasswordWorksForLoginAndOldDoesNot() throws Exception {
        mvc.perform(change(OLD, NEW, NEW)).andExpect(redirectedUrl("/login?pwchanged"));
        mvc.perform(post("/login").with(csrf()).param("username", "teacher1").param("password", OLD))
                .andExpect(redirectedUrl("/login?error"));
        mvc.perform(post("/login").with(csrf()).param("username", "teacher1").param("password", NEW))
                .andExpect(redirectedUrl("/teacher/home"));
    }

    @Test
    void wrongCurrentPasswordIsRejected() throws Exception {
        mvc.perform(change("Wrong-Current-1", NEW, NEW)).andExpect(status().isOk());
        assertThat(encoder.matches(OLD, hash())).isTrue();
        assertThat(auditRepo.countByAction("PASSWORD_CHANGED")).isZero();
    }

    @Test
    void shortNewPasswordIsRejected() throws Exception {
        mvc.perform(change(OLD, "short", "short")).andExpect(status().isOk());
        assertThat(encoder.matches(OLD, hash())).isTrue();
    }

    @Test
    void mismatchedConfirmationIsRejected() throws Exception {
        mvc.perform(change(OLD, NEW, "Different-Password-3")).andExpect(status().isOk());
        assertThat(encoder.matches(OLD, hash())).isTrue();
    }

    @Test
    void reusingTheSamePasswordIsRejected() throws Exception {
        mvc.perform(change(OLD, OLD, OLD)).andExpect(status().isOk());
        assertThat(auditRepo.countByAction("PASSWORD_CHANGED")).isZero();
    }

    @Test
    void changeWithoutCsrfIsForbidden() throws Exception {
        mvc.perform(post("/account/password").with(user("teacher1").roles("TEACHER"))
                        .param("currentPassword", OLD).param("newPassword", NEW).param("confirmPassword", NEW))
                .andExpect(status().isForbidden());
        assertThat(encoder.matches(OLD, hash())).isTrue();
    }

    @Test
    void repeatedWrongCurrentPasswordsAreLockedOut() throws Exception {
        for (int i = 0; i < 5; i++) {
            mvc.perform(change("Wrong-Current-" + i, NEW, NEW)).andExpect(status().isOk());
        }
        mvc.perform(change(OLD, NEW, NEW)).andExpect(status().isOk());
        assertThat(encoder.matches(OLD, hash())).isTrue();
    }
}
