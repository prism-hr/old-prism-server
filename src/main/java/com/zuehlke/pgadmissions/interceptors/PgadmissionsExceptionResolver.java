package com.zuehlke.pgadmissions.interceptors;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.zuehlke.pgadmissions.exceptions.CannotApplyToProgramException;
import com.zuehlke.pgadmissions.exceptions.CannotApplyToProjectException;
import com.zuehlke.pgadmissions.exceptions.PgadmissionsException;
import com.zuehlke.pgadmissions.exceptions.application.ActionNoLongerRequiredException;
import com.zuehlke.pgadmissions.exceptions.application.CannotTerminateApplicationException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.exceptions.application.PrimarySupervisorNotDefinedException;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition.AlertType;

public class PgadmissionsExceptionResolver extends AbstractHandlerExceptionResolver {

    private final Logger log = LoggerFactory.getLogger(PgadmissionsExceptionResolver.class);

    private final Map<Class<? extends PgadmissionsException>, PgadmissionExceptionHandler<? extends PgadmissionsException>> handlerMap = new LinkedHashMap<Class<? extends PgadmissionsException>, PgadmissionsExceptionResolver.PgadmissionExceptionHandler<? extends PgadmissionsException>>();;

    public PgadmissionsExceptionResolver() {
        initializeHandlerMap();
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof PgadmissionsException) {
            log.debug("Exception catched during request processing ", ex);
            return handlePgadmissionsException((PgadmissionsException) ex, request);
        }

        log.error("Unexpected exception catched during request processing ", ex);
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
                return new AlertDefinition(AlertType.INFO, "Missing application", "Application does not exist: " + ex.getApplicationNumber());
            }
        });
        addHandler(InsufficientApplicationFormPrivilegesException.class, new PgadmissionExceptionHandler<InsufficientApplicationFormPrivilegesException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(InsufficientApplicationFormPrivilegesException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot perform action", "You do not have sufficient privileges on application form: "
                        + ex.getApplicationNumber());
            }
        });
        addHandler(ActionNoLongerRequiredException.class, new PgadmissionExceptionHandler<ActionNoLongerRequiredException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(ActionNoLongerRequiredException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot perform action", "Your action upon application form " + ex.getApplicationNumber()
                        + " is no longer required.");
            }
        });
        addHandler(PrimarySupervisorNotDefinedException.class, new PgadmissionExceptionHandler<PrimarySupervisorNotDefinedException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(PrimarySupervisorNotDefinedException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "No primary supervisor", "No primary supervisor has been defined for application: "
                        + ex.getApplicationNumber());
            }
        });
        addHandler(CannotUpdateApplicationException.class, new PgadmissionExceptionHandler<CannotUpdateApplicationException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(CannotUpdateApplicationException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot update application", "Application cannot be updated: " + ex.getApplicationNumber());
            }
        });
        addHandler(CannotTerminateApplicationException.class, new PgadmissionExceptionHandler<CannotTerminateApplicationException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(CannotTerminateApplicationException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot perform action", "Application cannot be withdrawn/rejected: " + ex.getApplicationNumber());
            }
        });
        addHandler(CannotApplyToProgramException.class, new PgadmissionExceptionHandler<CannotApplyToProgramException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(CannotApplyToProgramException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, ex.getProgram().getTitle() + " is no longer accepting applications" , null);
            }
        });
        addHandler(CannotApplyToProjectException.class, new PgadmissionExceptionHandler<CannotApplyToProjectException>() {
        	@Override
        	public AlertDefinition handlePgadmissionsException(CannotApplyToProjectException ex, HttpServletRequest request) {
        		return new AlertDefinition(AlertType.INFO, ex.getProject().getAdvert().getTitle() + " is no longer accepting applications" , null);
        	}
        });

    }
}
