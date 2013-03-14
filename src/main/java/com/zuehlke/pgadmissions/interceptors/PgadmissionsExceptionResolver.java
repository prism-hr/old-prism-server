package com.zuehlke.pgadmissions.interceptors;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.zuehlke.pgadmissions.exceptions.PgadmissionsException;
import com.zuehlke.pgadmissions.exceptions.application.CannotUpdateApplicationException;
import com.zuehlke.pgadmissions.exceptions.application.CannotWithdrawApplicationException;
import com.zuehlke.pgadmissions.exceptions.application.IncorrectApplicationFormStateException;
import com.zuehlke.pgadmissions.exceptions.application.InsufficientApplicationFormPrivilegesException;
import com.zuehlke.pgadmissions.exceptions.application.MissingApplicationFormException;
import com.zuehlke.pgadmissions.exceptions.application.PrimarySupervisorNotDefinedException;
import com.zuehlke.pgadmissions.exceptions.application.RefereeAlreadyRespondedException;
import com.zuehlke.pgadmissions.exceptions.application.ReviewerAlreadyRespondedException;
import com.zuehlke.pgadmissions.exceptions.application.SupervisorAlreadyRespondedException;
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
                return new AlertDefinition(AlertType.INFO, "Missing application", "Given application does not exist: " + ex.getApplicationNumber());
            }
        });
        addHandler(InsufficientApplicationFormPrivilegesException.class, new PgadmissionExceptionHandler<InsufficientApplicationFormPrivilegesException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(InsufficientApplicationFormPrivilegesException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot perform action", "You have no sufficient privileges on given application form: "
                        + ex.getApplicationNumber());
            }
        });
        addHandler(RefereeAlreadyRespondedException.class, new PgadmissionExceptionHandler<RefereeAlreadyRespondedException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(RefereeAlreadyRespondedException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot post reference", "You have already posted a reference for application: "
                        + ex.getApplicationNumber());
            }
        });
        addHandler(IncorrectApplicationFormStateException.class, new PgadmissionExceptionHandler<IncorrectApplicationFormStateException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(IncorrectApplicationFormStateException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot perform action", "Application " + ex.getApplicationNumber() + " is no longer in \""
                        + ex.getExpectedState().displayValue() + "\" state.");
            }
        });
        addHandler(PrimarySupervisorNotDefinedException.class, new PgadmissionExceptionHandler<PrimarySupervisorNotDefinedException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(PrimarySupervisorNotDefinedException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "No primary supervisor", "No primary supervisor has been defined for application: "
                        + ex.getApplicationNumber());
            }
        });
        addHandler(SupervisorAlreadyRespondedException.class, new PgadmissionExceptionHandler<SupervisorAlreadyRespondedException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(SupervisorAlreadyRespondedException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot confirm supervision", "You have already responded to supervision request: "
                        + ex.getApplicationNumber());
            }
        });
        addHandler(ReviewerAlreadyRespondedException.class, new PgadmissionExceptionHandler<ReviewerAlreadyRespondedException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(ReviewerAlreadyRespondedException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot provide review",
                        "You have already provided or declined to provide a review for application: " + ex.getApplicationNumber());
            }
        });
        addHandler(CannotUpdateApplicationException.class, new PgadmissionExceptionHandler<CannotUpdateApplicationException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(CannotUpdateApplicationException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot update application", "Following application cannot be updated: " + ex.getApplicationNumber());
            }
        });
        addHandler(CannotWithdrawApplicationException.class, new PgadmissionExceptionHandler<CannotWithdrawApplicationException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(CannotWithdrawApplicationException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot withdraw application", "Following application cannot be withdrawn: "
                        + ex.getApplicationNumber());
            }
        });

    }
}
