package com.zuehlke.pgadmissions.rest.validation;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.common.collect.Lists;

@ControllerAdvice
public class PrismExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<FieldErrorResource> fieldErrorResources = Lists.newArrayListWithCapacity(fieldErrors.size());
        for (FieldError fieldError : fieldErrors) {
            FieldErrorResource fieldErrorResource = new FieldErrorResource();
            fieldErrorResource.setResource(fieldError.getObjectName());
            fieldErrorResource.setField(fieldError.getField());
            fieldErrorResource.setCode(fieldError.getCode());
            fieldErrorResource.setMessage(fieldError.getDefaultMessage());
            fieldErrorResources.add(fieldErrorResource);
        }

        ErrorResource error = new ErrorResource("InvalidRequest", ex.getMessage());
        error.setFieldErrors(fieldErrorResources);

        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(ex, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

    //    @ExceptionHandler({ MethodArgumentNotValidException.class })
//    protected ResponseEntity<Object> handleInvalidRequest(Exception e, WebRequest request) {
//        MethodArgumentNotValidException ire = (MethodArgumentNotValidException) e;
//
//        List<FieldError> fieldErrors = ire.getBindingResult().getFieldErrors();
//        List<FieldErrorResource> fieldErrorResources = Lists.newArrayListWithCapacity(fieldErrors.size());
//        for (FieldError fieldError : fieldErrors) {
//            FieldErrorResource fieldErrorResource = new FieldErrorResource();
//            fieldErrorResource.setResource(fieldError.getObjectName());
//            fieldErrorResource.setField(fieldError.getField());
//            fieldErrorResource.setCode(fieldError.getCode());
//            fieldErrorResource.setMessage(fieldError.getDefaultMessage());
//            fieldErrorResources.add(fieldErrorResource);
//        }
//
//        ErrorResource error = new ErrorResource("InvalidRequest", ire.getMessage());
//        error.setFieldErrors(fieldErrorResources);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        return handleExceptionInternal(e, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
//    }

}