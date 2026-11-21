package com.classvoice.service;

/** Code is one of: empty, size, type, dimensions. Shown to the admin as a fixed message, never raw input. */
public class InvalidLogoException extends RuntimeException {

    private final String code;

    public InvalidLogoException(String code) {
        super(code);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
