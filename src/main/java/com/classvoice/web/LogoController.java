package com.classvoice.web;

import com.classvoice.service.LogoService;
import java.time.Duration;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** Public: the login page shows the logo. Content type comes from the server-side check at upload time. */
@Controller
public class LogoController {

    private final LogoService logos;

    public LogoController(LogoService logos) {
        this.logos = logos;
    }

    @GetMapping("/logo")
    public ResponseEntity<byte[]> logo() {
        return logos.current()
                .map(l -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(l.getContentType()))
                        .cacheControl(CacheControl.maxAge(Duration.ofMinutes(5)).cachePublic())
                        .body(l.getData()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
