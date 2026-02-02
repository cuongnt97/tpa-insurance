package com.insurance.assignment.common.exception.customexception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InactiveException extends RuntimeException {
    public InactiveException(String message) {
        super(message);
    }
}
