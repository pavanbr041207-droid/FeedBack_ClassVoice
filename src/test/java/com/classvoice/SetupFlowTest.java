package com.classvoice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.classvoice.domain.RoleName;
import com.classvoice.repo.AuditLogRepository;
import com.classvoice.repo.UserRepository;
import com.classvoice.security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SetupFlowTest {

    private static final String PW = "Correct-Horse-9";

    @Autowired MockMvc mvc;
    @Autowired UserRepository userRepo;
    @Autowired AuditLogRepository auditRepo;
    @Autowired LoginAttemptService attempts;

    @BeforeEach
    void clean() {
        auditRepo.deleteAll();
        userRepo.deleteAll();
        attempts.reset();
    }

    private MockHttpServletRequestBuilder submit(String token, String username, String pw, String confirm) {
        return post("/setup").with(csrf())
                .param("setupToken", token)
                .param("displayName", "Principal")
                .param("username", username)
                .param("password", pw)
                .param("confirmPassword", confirm);
    }

    @Test
    void formIsOpenWhileNoAdminExists() throws Exception {
        mvc.perform(get("/setup")).andExpect(status().isOk());
    }

    @Test
    void validSetupCreatesExactlyOneAdminThenCloses() throws Exception {
        mvc.perform(submit("test-setup-token", "principal", PW, PW))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?setup"));
        assertThat(userRepo.existsByRole_Name(RoleName.COLLEGE_ADMIN)).isTrue();
        assertThat(auditRepo.countByAction("SETUP_ADMIN_CREATED")).isEqualTo(1);

        mvc.perform(get("/setup")).andExpect(status().isNotFound());
        mvc.perform(submit("test-setup-token", "second", PW, PW)).andExpect(status().isNotFound());
        assertThat(userRepo.count()).isEqualTo(1);
    }

    @Test
    void wrongTokenIsRejected() throws Exception {
        mvc.perform(submit("wrong-token", "principal", PW, PW)).andExpect(status().isOk());
        assertThat(userRepo.count()).isZero();
    }

    @Test
    void passwordMismatchIsRejected() throws Exception {
        mvc.perform(submit("test-setup-token", "principal", PW, "Different-Pass-1")).andExpect(status().isOk());
        assertThat(userRepo.count()).isZero();
    }

    @Test
    void shortPasswordIsRejected() throws Exception {
        mvc.perform(submit("test-setup-token", "principal", "short", "short")).andExpect(status().isOk());
        assertThat(userRepo.count()).isZero();
    }

    @Test
    void tokenGuessingIsLockedAfterFiveFailures() throws Exception {
        for (int i = 0; i < 5; i++) {
            mvc.perform(submit("guess-" + i, "principal", PW, PW)).andExpect(status().isOk());
        }
        mvc.perform(submit("test-setup-token", "principal", PW, PW)).andExpect(status().isTooManyRequests());
        assertThat(userRepo.count()).isZero();
    }
}
