package com.zuehlke.pgadmissions.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.zuehlke.pgadmissions.domain.user.User;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DiagnosticInfoPrintUtils;

public class PrismExceptionResolver extends AbstractHandlerExceptionResolver {

    private final Logger log = LoggerFactory.getLogger(PrismExceptionResolver.class);

    @Autowired
    private UserService userService;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        view.setExtractValueFromSingleKeyModel(true);
        ModelAndView modelAndView = new ModelAndView(view);
        if (ex instanceof AuthenticationException) {
            // should be handled by Spring Security filters
            return null;
        }

        User currentUser = null;
        try {
            currentUser = userService.getCurrentUser();
        } catch (Exception e) {
            log.error("Couldn't get current user because of " + e.getClass() + ": " + e.getMessage());
        }
        log.error(DiagnosticInfoPrintUtils.getRequestErrorLogMessage(request, currentUser), ex);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return modelAndView;
    }

}