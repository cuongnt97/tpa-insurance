package com.insurance.assignment.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.google.common.base.Throwables;
import com.insurance.assignment.common.CONSTANTS;
import com.insurance.assignment.common.config.I18N;
import com.insurance.assignment.common.exception.customexception.BusinessException;
import com.insurance.assignment.common.exception.customexception.DuplicatedException;
import com.insurance.assignment.common.exception.customexception.InactiveException;
import com.insurance.assignment.common.exception.customexception.RecordNotFoundException;
import com.insurance.assignment.common.response.MessageResponse;
import jakarta.validation.ConstraintDeclarationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public final ResponseEntity<MessageResponse> handleBusinessException(BusinessException ex) {
        String message = ex.getMessage();
        MessageResponse response = new MessageResponse(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL, message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InactiveException.class)
    public final ResponseEntity<MessageResponse> handleInactiveException(InactiveException ex) {
        String message = ex.getMessage();
        MessageResponse response = new MessageResponse(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL, message);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DuplicatedException.class)
    public final ResponseEntity<MessageResponse> handleDuplicatedException(DuplicatedException ex) {
        log.error(Throwables.getStackTraceAsString(ex));
        String message = ex.getMessage();
        MessageResponse response = new MessageResponse(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL, message);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<MessageResponse> handleRecordNotFoundException(RecordNotFoundException ex) {
        log.error(Throwables.getStackTraceAsString(ex));
        String message = ex.getMessage();
        MessageResponse response = new MessageResponse(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL, message);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageResponse> handleValidationException(
            MethodArgumentNotValidException ex) throws BusinessException {

        List<String> messages = ex.getBindingResult().getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();

        MessageResponse response = new MessageResponse(messages, CONSTANTS.HTTP_RESPONSE.STATUS_FAIL);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageResponse> handleInvalidEnum(
            HttpMessageNotReadableException ex) {

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            if (ife.getTargetType().isEnum()) {
                String fieldName = ife.getPath().get(0).getFieldName();
                String allowedValues = Arrays.stream(ife.getTargetType().getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.joining(", "));

                String message = I18N.getMessage("error.enum.invalid", fieldName, allowedValues);

                return new ResponseEntity<>(new MessageResponse(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL, message), HttpStatus.BAD_REQUEST);
            }
        }
        String message = ex.getMessage();
        MessageResponse response = new MessageResponse(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL, message);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<MessageResponse> handleMethodValidation(
            HandlerMethodValidationException ex) {

        List<String> messages = new ArrayList<>();

        ex.getParameterValidationResults().forEach(result ->
                result.getResolvableErrors()
                        .forEach(err -> messages.add(err.getDefaultMessage()))
        );

        ex.getBeanResults().forEach(result ->
                result.getResolvableErrors()
                        .forEach(err -> messages.add(err.getDefaultMessage()))
        );

        return new ResponseEntity<>(new MessageResponse(messages, CONSTANTS.HTTP_RESPONSE.STATUS_FAIL), HttpStatus.BAD_REQUEST);
    }

    /// Global exception
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<MessageResponse> handleException(Exception ex) {
        log.error(ex.getMessage(), ex);
        String message = I18N.getMessage("error.app.internal");
        MessageResponse response = new MessageResponse(CONSTANTS.HTTP_RESPONSE.STATUS_FAIL, message);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
