package com.classvoice.web;

import com.classvoice.service.CollegeSettingsService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final CollegeSettingsService settings;

    public GlobalModelAdvice(CollegeSettingsService settings) {
        this.settings = settings;
    }

    @ModelAttribute("collegeName")
    public String collegeName() {
        return settings.displayName();
    }

    /** Null when no logo has been uploaded. */
    @ModelAttribute("logoUrl")
    public String logoUrl() {
        return settings.logoUrl();
    }
}
