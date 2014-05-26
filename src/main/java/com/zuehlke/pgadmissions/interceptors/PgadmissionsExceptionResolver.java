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

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.exceptions.CannotApplyException;
import com.zuehlke.pgadmissions.exceptions.CannotExecuteActionException;
import com.zuehlke.pgadmissions.exceptions.PrismException;
import com.zuehlke.pgadmissions.interceptors.AlertDefinition.AlertType;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DiagnosticInfoPrintUtils;

public class PgadmissionsExceptionResolver extends AbstractHandlerExceptionResolver {

    private final Logger log = LoggerFactory.getLogger(PgadmissionsExceptionResolver.class);

    private final Map<Class<? extends PrismException>, PgadmissionExceptionHandler<? extends PrismException>> handlerMap = new LinkedHashMap<Class<? extends PrismException>, PgadmissionsExceptionResolver.PgadmissionExceptionHandler<? extends PrismException>>();;

    @Autowired
    private UserService userService;

    public PgadmissionsExceptionResolver() {
        initializeHandlerMap();
    }

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex instanceof PrismException) {
            log.debug("Exception catched during request processing ", ex);
            return handlePgadmissionsException((PrismException) ex, request);
        }
        User currentUser = null;
        try {
            currentUser = userService.getCurrentUser();
        } catch (Exception e) {
            log.error("Couldn't read current user because of " + e.getClass() + ": " + e.getMessage());
        }
        log.error(DiagnosticInfoPrintUtils.getRequestErrorLogMessage(request, currentUser), ex);
        return new ModelAndView("redirect:error");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected ModelAndView handlePgadmissionsException(PrismException ex, HttpServletRequest request) {
        String view = "redirect:/applications";
        ModelAndView modelAndView = new ModelAndView(view);

        if (handlerMap.containsKey(ex.getClass())) {
            PgadmissionExceptionHandler handler = handlerMap.get(ex.getClass());
            AlertDefinition alertDefinition = handler.handlePgadmissionsException(ex, request);
            request.getSession().setAttribute("alertDefinition", alertDefinition);
        }

        return modelAndView;
    }

    private interface PgadmissionExceptionHandler<T extends PrismException> {
        AlertDefinition handlePgadmissionsException(T ex, HttpServletRequest request);
    }

    protected <T extends PrismException> void addHandler(Class<T> clazz, PgadmissionExceptionHandler<T> handler) {
        handlerMap.put(clazz, handler);
    }

    protected void initializeHandlerMap() {
        addHandler(CannotExecuteActionException.class, new PgadmissionExceptionHandler<CannotExecuteActionException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(CannotExecuteActionException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot perform action", "You do not have sufficient privileges on this "
                        + ex.getPrismScope().getResourceType() + ".");
            }
        });
        addHandler(CannotApplyException.class, new PgadmissionExceptionHandler<CannotApplyException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(CannotApplyException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Cannot apply",
                        "The opportunity that you attempted to apply for is no longer accepting applications.");
            }
        });
    }

}