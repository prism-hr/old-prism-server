package com.zuehlke.pgadmissions.interceptors;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.exceptions.PgadmissionsException;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition.AlertType;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DiagnosticInfoPrintUtils;

public class PgadmissionsExceptionResolver extends AbstractHandlerExceptionResolver {

    private final Logger log = LoggerFactory.getLogger(PgadmissionsExceptionResolver.class);

    private final Map<Class<? extends PgadmissionsException>, PgadmissionExceptionHandler<? extends PgadmissionsException>> handlerMap = new LinkedHashMap<Class<? extends PgadmissionsException>, PgadmissionsExceptionResolver.PgadmissionExceptionHandler<? extends PgadmissionsException>>();;

    @Autowired
    private UserService userService;

    public PgadmissionsExceptionResolver() {
        initializeHandlerMap();
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof PgadmissionsException) {
            log.debug("Exception catched during request processing ", ex);
            return handlePgadmissionsException((PgadmissionsException) ex, request);
        }
        RegisteredUser currentUser = null;
        try {
            currentUser = userService.getCurrentUser();
        } catch (Exception e) {
            log.error("Couldn't read current user because of " + e.getClass() + ": " + e.getMessage());
        }
        log.error(DiagnosticInfoPrintUtils.getRequestErrorLogMessage(request, currentUser), ex);
        return new ModelAndView("redirect:error");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected ModelAndView handlePgadmissionsException(PgadmissionsException ex, HttpServletRequest request) {
        String view = "redirect:/applications";
        ModelAndView modelAndView = new ModelAndView(view);

        if (handlerMap.containsKey(ex.getClass())) {
            PgadmissionExceptionHandler handler = handlerMap.get(ex.getClass());
            AlertDefinition alertDefinition = handler.handlePgadmissionsException(ex, request);
            request.getSession().setAttribute("alertDefinition", alertDefinition);
        }

        return modelAndView;
    }

    private interface PgadmissionExceptionHandler<T extends PgadmissionsException> {
        AlertDefinition handlePgadmissionsException(T ex, HttpServletRequest request);
    }

    protected <T extends PgadmissionsException> void addHandler(Class<T> clazz, PgadmissionExceptionHandler<T> handler) {
        handlerMap.put(clazz, handler);
    }

    protected void initializeHandlerMap() {
        addHandler(MissingApplicationFormException.class, new PgadmissionExceptionHandler<MissingApplicationFormException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(MissingApplicationFormException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Missing application", "The application does not exist: " + ex.getApplicationNumber());
            }
        });
        addHandler(InsufficientApplicationFormPrivilegesException.class, new PgadmissionExceptionHandler<InsufficientApplicationFormPrivilegesException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(InsufficientApplicationFormPrivilegesException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot perform action", "You do not have sufficient privileges on this application form: "
                        + ex.getApplicationNumber());
            }
        });
        addHandler(ActionNoLongerRequiredException.class, new PgadmissionExceptionHandler<ActionNoLongerRequiredException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(ActionNoLongerRequiredException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "You cannot perform this action", "Check that the action has not been performed already");
            }
        });
        addHandler(CannotUpdateApplicationException.class, new PgadmissionExceptionHandler<CannotUpdateApplicationException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(CannotUpdateApplicationException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot update application", "The application can no longer be updated: "
                        + ex.getApplicationNumber());
            }
        });
        addHandler(CannotApplyException.class, new PgadmissionExceptionHandler<CannotApplyException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(CannotApplyException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot apply",
                        "The opportunity that you attempted to apply for is no longer accepting applications");
            }
        });
    }

}