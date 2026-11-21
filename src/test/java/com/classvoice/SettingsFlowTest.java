package com.classvoice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.classvoice.repo.AuditLogRepository;
import com.classvoice.repo.CollegeLogoRepository;
import com.classvoice.service.CollegeSettingsService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SettingsFlowTest {

    private static final String DEFAULT_NAME = "Your College";

    @Autowired MockMvc mvc;
    @Autowired AuditLogRepository auditRepo;
    @Autowired CollegeLogoRepository logoRepo;
    @Autowired CollegeSettingsService settings;

    @BeforeEach
    void clean() {
        auditRepo.deleteAll();
        logoRepo.deleteAll();
    }

    @AfterEach
    void restore() {
        logoRepo.deleteAll();
        settings.updateDisplayName(null, DEFAULT_NAME);
    }

    private static byte[] image(int w, int h, String format) throws IOException {
        int type = format.equals("gif") ? BufferedImage.TYPE_BYTE_INDEXED : BufferedImage.TYPE_INT_RGB;
        BufferedImage img = new BufferedImage(w, h, type);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, format, out);
        return out.toByteArray();
    }

    private RequestBuilder upload(String fileName, String contentType, byte[] bytes) {
        return upload(fileName, contentType, bytes, user("a").roles("COLLEGE_ADMIN"));
    }

    private RequestBuilder upload(String fileName, String contentType, byte[] bytes, RequestPostProcessor auth) {
        return multipart("/admin/settings/logo")
                .file(new MockMultipartFile("logo", fileName, contentType, bytes))
                .with(csrf())
                .with(auth);
    }

    // ---- page access ----

    @Test
    void adminSeesSettingsPage() throws Exception {
        mvc.perform(get("/admin/settings").with(user("a").roles("COLLEGE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(DEFAULT_NAME)));
    }

    @Test
    void teacherAndStudentCannotUseSettings() throws Exception {
        mvc.perform(get("/admin/settings").with(user("t").roles("TEACHER"))).andExpect(status().isForbidden());
        mvc.perform(get("/admin/settings").with(user("s").roles("STUDENT"))).andExpect(status().isForbidden());
        mvc.perform(post("/admin/settings/name").with(csrf()).with(user("t").roles("TEACHER"))
                .param("displayName", "Hacked")).andExpect(status().isForbidden());
        mvc.perform(upload("logo.png", "image/png", image(10, 10, "png"), user("t").roles("TEACHER")))
                .andExpect(status().isForbidden());
        assertThat(logoRepo.count()).isZero();
        assertThat(settings.displayName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void anonymousIsSentToLogin() throws Exception {
        mvc.perform(get("/admin/settings")).andExpect(status().is3xxRedirection());
    }

    // ---- college name ----

    @Test
    void adminCanRenameCollegeAndItShowsOnPagesAndIsAudited() throws Exception {
        mvc.perform(post("/admin/settings/name").with(csrf()).with(user("a").roles("COLLEGE_ADMIN"))
                        .param("displayName", "  Riverside College  "))
                .andExpect(redirectedUrl("/admin/settings?saved"));
        assertThat(settings.displayName()).isEqualTo("Riverside College");
        assertThat(auditRepo.countByAction("SETTINGS_DISPLAY_NAME_CHANGED")).isEqualTo(1);
        mvc.perform(get("/teacher/home").with(user("t").roles("TEACHER")))
                .andExpect(content().string(containsString("Riverside College")));
    }

    @Test
    void blankNameIsRejected() throws Exception {
        mvc.perform(post("/admin/settings/name").with(csrf()).with(user("a").roles("COLLEGE_ADMIN"))
                        .param("displayName", "   "))
                .andExpect(status().isOk());
        assertThat(settings.displayName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void tooLongNameIsRejected() throws Exception {
        mvc.perform(post("/admin/settings/name").with(csrf()).with(user("a").roles("COLLEGE_ADMIN"))
                        .param("displayName", "x".repeat(121)))
                .andExpect(status().isOk());
        assertThat(settings.displayName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    void nameIsHtmlEscapedOnOutput() throws Exception {
        mvc.perform(post("/admin/settings/name").with(csrf()).with(user("a").roles("COLLEGE_ADMIN"))
                .param("displayName", "<b>Bold</b>"));
        mvc.perform(get("/admin/home").with(user("a").roles("COLLEGE_ADMIN")))
                .andExpect(content().string(not(containsString("<b>Bold</b>"))))
                .andExpect(content().string(containsString("&lt;b&gt;Bold&lt;/b&gt;")));
    }

    @Test
    void renameWithoutCsrfIsForbidden() throws Exception {
        mvc.perform(post("/admin/settings/name").with(user("a").roles("COLLEGE_ADMIN"))
                .param("displayName", "No Csrf")).andExpect(status().isForbidden());
        assertThat(settings.displayName()).isEqualTo(DEFAULT_NAME);
    }

    // ---- logo ----

    @Test
    void noLogoMeansNotFound() throws Exception {
        mvc.perform(get("/logo")).andExpect(status().isNotFound());
    }

    @Test
    void validPngIsStoredAndServedPublicly() throws Exception {
        byte[] png = image(40, 20, "png");
        mvc.perform(upload("anything.bin", "application/octet-stream", png))
                .andExpect(redirectedUrl("/admin/settings?logoSaved"));
        assertThat(auditRepo.countByAction("LOGO_CHANGED")).isEqualTo(1);
        mvc.perform(get("/logo"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(png));
        mvc.perform(get("/login")).andExpect(content().string(containsString("/logo?v=")));
    }

    @Test
    void validJpegIsAccepted() throws Exception {
        byte[] jpg = image(40, 20, "jpg");
        mvc.perform(upload("logo.jpg", "image/jpeg", jpg)).andExpect(redirectedUrl("/admin/settings?logoSaved"));
        mvc.perform(get("/logo")).andExpect(content().contentType("image/jpeg"));
    }

    @Test
    void newLogoReplacesOldOne() throws Exception {
        mvc.perform(upload("a.png", "image/png", image(10, 10, "png")));
        byte[] second = image(30, 30, "png");
        mvc.perform(upload("b.png", "image/png", second));
        assertThat(logoRepo.count()).isEqualTo(1);
        mvc.perform(get("/logo")).andExpect(content().bytes(second));
    }

    @Test
    void textFileDisguisedAsPngIsRejected() throws Exception {
        mvc.perform(upload("evil.png", "image/png", "<script>alert(1)</script>".getBytes()))
                .andExpect(redirectedUrl("/admin/settings?logoError=type"));
        assertThat(logoRepo.count()).isZero();
    }

    @Test
    void otherImageFormatsAreRejected() throws Exception {
        mvc.perform(upload("logo.gif", "image/gif", image(10, 10, "gif")))
                .andExpect(redirectedUrl("/admin/settings?logoError=type"));
        assertThat(logoRepo.count()).isZero();
    }

    @Test
    void emptyUploadIsRejected() throws Exception {
        mvc.perform(upload("logo.png", "image/png", new byte[0]))
                .andExpect(redirectedUrl("/admin/settings?logoError=empty"));
    }

    @Test
    void oversizedUploadIsRejected() throws Exception {
        mvc.perform(upload("logo.png", "image/png", new byte[600 * 1024]))
                .andExpect(redirectedUrl("/admin/settings?logoError=size"));
        assertThat(logoRepo.count()).isZero();
    }

    @Test
    void hugeDimensionsAreRejected() throws Exception {
        mvc.perform(upload("wide.png", "image/png", image(2100, 10, "png")))
                .andExpect(redirectedUrl("/admin/settings?logoError=dimensions"));
        assertThat(logoRepo.count()).isZero();
    }

    @Test
    void failedUploadKeepsExistingLogo() throws Exception {
        byte[] good = image(20, 20, "png");
        mvc.perform(upload("good.png", "image/png", good));
        mvc.perform(upload("bad.png", "image/png", "not an image".getBytes()));
        mvc.perform(get("/logo")).andExpect(content().bytes(good));
    }
}
