package com.zuehlke.pgadmissions.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StacktraceDump {

     public static String forException(Throwable t) {
        StringWriter buf = new StringWriter(5000);
        PrintWriter pw = new PrintWriter(buf);
        t.printStackTrace(pw);
        pw.close();
        return buf.toString();
    }

}
