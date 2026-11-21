package com.classvoice.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SetupForm {

    @NotBlank(message = "Setup token is required")
    private String setupToken;

    @NotBlank(message = "Display name is required")
    @Size(max = 120, message = "Max 120 characters")
    private String displayName;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 64, message = "3 to 64 characters")
    @Pattern(regexp = "[A-Za-z0-9._-]*", message = "Letters, digits, dot, dash, underscore only")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 10, max = 100, message = "10 to 100 characters")
    private String password;

    @NotBlank(message = "Confirm the password")
    private String confirmPassword;

    public String getSetupToken() {
        return setupToken;
    }

    public void setSetupToken(String setupToken) {
        this.setupToken = setupToken;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
