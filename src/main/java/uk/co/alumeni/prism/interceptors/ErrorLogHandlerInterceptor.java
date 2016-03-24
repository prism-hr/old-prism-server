package uk.co.alumeni.prism.interceptors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import uk.co.alumeni.prism.domain.user.User;
import uk.co.alumeni.prism.services.UserService;
import uk.co.alumeni.prism.utils.PrismDiagnosticUtils;

public class ErrorLogHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(ErrorLogHandlerInterceptor.class);

    @Inject
    private UserService userService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            if (ex instanceof AuthenticationException || ex instanceof AccessDeniedException) {
                return;
            }

            User currentUser = userService.getCurrentUser();
            log.error(PrismDiagnosticUtils.getRequestErrorLogMessage(request, currentUser) + ", Exception: " + ex);

        }
    }
}
