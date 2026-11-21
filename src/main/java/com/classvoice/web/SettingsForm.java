package com.classvoice.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SettingsForm {

    @NotBlank(message = "College name is required")
    @Size(max = 120, message = "Max 120 characters")
    private String displayName;

    public SettingsForm() {
    }

    public SettingsForm(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
