package com.zuehlke.pgadmissions.rest.validation;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.common.collect.Lists;

@ControllerAdvice
public class PrismControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value= PrismValidationException.class)
    public final ResponseEntity<Object> handlePrismValidationException(PrismValidationException ex, WebRequest request) {
        return handleValidationErrors(ex, new HttpHeaders(), request, ex.getErrors());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult bindingResult = ex.getBindingResult();
        return handleValidationErrors(ex, headers, request, bindingResult);
    }

    protected ResponseEntity<Object> handleValidationErrors(Exception ex, HttpHeaders headers, WebRequest request, Errors errors) {
        List<FieldError> fieldErrors = errors.getFieldErrors();
        Map<String, ValidationErrorRepresentation> validationErrorRepresentations = Maps.newLinkedHashMap();
        for (FieldError fieldError : fieldErrors) {
            ValidationErrorRepresentation errorRepresentation = new ValidationErrorRepresentation();
            errorRepresentation.setCode(fieldError.getCode());
            errorRepresentation.setMessage(fieldError.getDefaultMessage());
            errorRepresentation.setArguments(fieldError.getArguments());
            validationErrorRepresentations.put(fieldError.getField(), errorRepresentation);
        }

        ErrorResponseRepresentation error = new ErrorResponseRepresentation(ex.getMessage(), validationErrorRepresentations);

        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(ex, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

}