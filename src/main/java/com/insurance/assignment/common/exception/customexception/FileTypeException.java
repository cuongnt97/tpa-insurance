package com.insurance.assignment.common.exception.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class FileTypeException extends RuntimeException {
    public FileTypeException(String message) {
        super(message);
    }
}
