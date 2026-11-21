package com.classvoice.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class UploadExceptionAdvice {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String tooLarge() {
        return "redirect:/admin/settings?logoError=size";
    }
}
