package com.zuehlke.pgadmissions.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.zuehlke.pgadmissions.domain.User;

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

        return "Request handling error for: " + request.getMethod() + " " + request.getRequestURI() + ", user: " + Objects.firstNonNull(currentUser, "<none>")
                + ", params:\n" + params;
    }

}
