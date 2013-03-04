package com.zuehlke.pgadmissions.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.lang.exception.ExceptionUtils;

public final class StacktraceDump {

    private StacktraceDump() {
    }
    
     public static String forException(Throwable t) {
        StringWriter buf = new StringWriter(5000);
        PrintWriter pw = new PrintWriter(buf);
        t.printStackTrace(pw);
        pw.close();
        return buf.toString();
    }

     public static String printRootCauseStackTrace(Throwable t) {
         StringWriter buf = new StringWriter(5000);
         PrintWriter pw = new PrintWriter(buf);
         ExceptionUtils.printRootCauseStackTrace(t, pw);
         pw.close();
         return buf.toString();
     }
     
     public static String getFullStackTrace(Throwable t) {
         return ExceptionUtils.getFullStackTrace(t);
     }
}
