package com.zuehlke.pgadmissions.rest.validation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.resource.Resource;
import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.exceptions.PrismBadRequestException;
import com.zuehlke.pgadmissions.exceptions.PrismConflictException;
import com.zuehlke.pgadmissions.exceptions.PrismForbiddenException;
import com.zuehlke.pgadmissions.exceptions.PrismValidationException;
import com.zuehlke.pgadmissions.exceptions.ResourceNotFoundException;
import com.zuehlke.pgadmissions.exceptions.WorkflowPermissionException;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DiagnosticInfoPrintUtils;

@ControllerAdvice
public class PrismControllerExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(PrismControllerExceptionHandler.class);

    @Inject
    private UserService userService;

    @ExceptionHandler(value = WorkflowPermissionException.class)
    public final ResponseEntity<Object> handleWorkflowPermissionsException(WorkflowPermissionException ex, ServletWebRequest request) {
        User currentUser = userService.getCurrentUser();

        log.error("Problem", ex);
        Resource fallbackResource = ex.getFallbackResource();

        Map<String, Object> body = Maps.newHashMap();
        body.put("fallbackAction", ex.getFallbackAction().getId());
        body.put("fallbackResource", ImmutableMap.of("id", fallbackResource.getId(), "resourceScope", fallbackResource.getResourceScope()));
        if (ex.getMessage() != null) {
            body.put("message", ex.getMessage());
        }
        log.error(DiagnosticInfoPrintUtils.getRequestErrorLogMessage(request.getRequest(), currentUser) + ", Exception: " + ex);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = { PrismBadRequestException.class, ResourceNotFoundException.class, AccessDeniedException.class, PrismConflictException.class,
            BadCredentialsException.class, PrismForbiddenException.class})
    public final ResponseEntity<Object> handleResourceNotFoundException(Exception ex, WebRequest request) {
        request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = Collections.singletonMap("reason", ex.getMessage());

        HttpStatus status;
        if (ex instanceof AccessDeniedException || ex instanceof BadCredentialsException) {
            status = HttpStatus.UNAUTHORIZED;
        } else {
            status = ex.getClass().getAnnotation(ResponseStatus.class).value();
        }
        return handleExceptionInternal(ex, body, headers, status, request);
    }

    @ExceptionHandler(value = PrismValidationException.class)
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
        List<ValidationErrorRepresentation> validationErrorRepresentations = Lists.newLinkedList();
        for (FieldError fieldError : fieldErrors) {
            ValidationErrorRepresentation errorRepresentation = new ValidationErrorRepresentation();
            errorRepresentation.setErrorCode(fieldError.getCode());
            errorRepresentation.setErrorMessage(fieldError.getDefaultMessage());
            errorRepresentation.setFieldNames(new String[] { fieldError.getField() });
            validationErrorRepresentations.add(errorRepresentation);
        }

        ErrorResponseRepresentation error = new ErrorResponseRepresentation(ex.getMessage(), validationErrorRepresentations);

        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(ex, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

}
