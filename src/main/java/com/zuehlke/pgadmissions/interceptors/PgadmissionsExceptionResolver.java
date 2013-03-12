package com.zuehlke.pgadmissions.interceptors;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

import com.zuehlke.pgadmissions.exceptions.MissingApplicationFormException;
import com.zuehlke.pgadmissions.exceptions.PgadmissionsException;
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
        
        if(handlerMap.containsKey(ex.getClass())) {
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
        handlerMap.put(MissingApplicationFormException.class, new PgadmissionExceptionHandler<MissingApplicationFormException>() {
            @Override
            public AlertDefinition handlePgadmissionsException(MissingApplicationFormException ex, HttpServletRequest request) {
                return new AlertDefinition(AlertType.INFO, "Missing application", "Given application does not exist: " + ex.getApplicationNumber());
            }
        });
    }
}
