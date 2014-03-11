package com.zuehlke.pgadmissions.interceptors;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;

public class ErrorLogHandlerInterceptor extends HandlerInterceptorAdapter {

    private static final Logger log = LoggerFactory.getLogger(ErrorLogHandlerInterceptor.class);

    @Inject
    private UserService userService;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            RegisteredUser currentUser = userService.getCurrentUser();

            Map<String, String> parameterMap = Maps.transformValues(request.getParameterMap(), new Function<String[], String>() {
                public String apply(String[] input) {
                    return Joiner.on(",").join(input);
                }
            });
            String params = Joiner.on("\n").withKeyValueSeparator(" -> ").join(parameterMap);

            log.error("Request handling error for: " + request.getMethod() + " " + request.getRequestURI() + ", user: "
                    + Objects.firstNonNull(currentUser, "<none>") + ", params:\n" + params);

        }
    }
}
