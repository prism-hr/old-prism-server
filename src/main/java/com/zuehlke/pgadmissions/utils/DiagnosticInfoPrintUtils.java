package com.zuehlke.pgadmissions.utils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.user.User;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public final class DiagnosticInfoPrintUtils {

    private DiagnosticInfoPrintUtils() {
    }

    public static String getRequestErrorLogMessage(HttpServletRequest request, User currentUser) {
        Map<String, String> parameterMap = Maps.newLinkedHashMap(Maps.transformValues(request.getParameterMap(), new Function<String[], String>() {
            public String apply(String[] input) {
                return Joiner.on(",").join(input);
            }
        }));
        parameterMap.remove("password");
        String params = Joiner.on("\n").withKeyValueSeparator(" -> ").join(parameterMap);

        String userString = currentUser == null ? "<none>" : currentUser.toString();
        return "Request handling error for: " + request.getMethod() + " " + request.getRequestURI() + ", user: " +
                userString + ", params:\n" + params;
    }

}
