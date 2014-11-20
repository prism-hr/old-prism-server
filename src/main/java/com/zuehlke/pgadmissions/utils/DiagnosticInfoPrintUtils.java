package com.zuehlke.pgadmissions.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.user.User;

public final class DiagnosticInfoPrintUtils {

    private DiagnosticInfoPrintUtils() {
    }

    public static String printRootCauseStackTrace(Throwable t) {
        StringWriter buf = new StringWriter(5000);
        PrintWriter pw = new PrintWriter(buf);
        ExceptionUtils.printRootCauseStackTrace(t, pw);
        pw.close();
        return buf.toString();
    }

    public static String getRequestErrorLogMessage(HttpServletRequest request, User currentUser) {
        Map<String, String> parameterMap = Maps.transformValues(request.getParameterMap(), new Function<String[], String>() {
            public String apply(String[] input) {
                return Joiner.on(",").join(input);
            }
        });
        String params = Joiner.on("\n").withKeyValueSeparator(" -> ").join(parameterMap);

        String userString = currentUser == null ? "<none>" : currentUser.toString();
        return "Request handling error for: " + request.getMethod() + " " + request.getRequestURI() + ", user: " +
                userString + ", params:\n" + params;
    }

}
