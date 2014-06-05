package com.zuehlke.pgadmissions.interceptors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.DiagnosticInfoPrintUtils;

public class ErrorLogHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(ErrorLogHandlerInterceptor.class);

    @Inject
    private UserService userService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            User currentUser = userService.getCurrentUser();

            log.error(DiagnosticInfoPrintUtils.getRequestErrorLogMessage(request, currentUser));

        }
    }
}
