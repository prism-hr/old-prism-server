package uk.co.alumeni.prism.rest.validation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
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
import uk.co.alumeni.prism.domain.resource.Resource;
import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.exceptions.*;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.utils.PrismDiagnosticUtils;

import javax.inject.Inject;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
        log.error(PrismDiagnosticUtils.getRequestErrorLogMessage(request.getRequest(), currentUser) + ", Exception: " + ex);
        return handleExceptionInternal(ex, body, new HttpHeaders(), HttpStatus.FORBIDDEN, request);
    }

    @ExceptionHandler(value = {PrismBadRequestException.class, ResourceNotFoundException.class, AccessDeniedException.class,
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

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Temporary exception handle", ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    protected ResponseEntity<Object> handleValidationErrors(Exception ex, HttpHeaders headers, WebRequest request, Errors errors) {
        List<ValidationErrorRepresentation> validationErrorRepresentations = null;
        if (errors != null) {
            validationErrorRepresentations = new LinkedList<>();
            List<FieldError> fieldErrors = errors.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                ValidationErrorRepresentation errorRepresentation = new ValidationErrorRepresentation();
                errorRepresentation.setErrorCode(fieldError.getCode());
                errorRepresentation.setErrorMessage(fieldError.getDefaultMessage());
                errorRepresentation.setFieldNames(new String[]{fieldError.getField()});
                validationErrorRepresentations.add(errorRepresentation);
            }
        }

        ErrorResponseRepresentation error = new ErrorResponseRepresentation(ex.getMessage(), validationErrorRepresentations);

        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(ex, error, headers, HttpStatus.UNPROCESSABLE_ENTITY, request);
    }

}
