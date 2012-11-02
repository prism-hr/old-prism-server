package com.zuehlke.pgadmissions.services.exporters;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class GMonthAdapter {
    
    private static final String GMONTH_FORMAT = "--MM";

    public static DateTime parse(String s) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(GMONTH_FORMAT);
        return fmt.parseDateTime(s);
    }

    public static String print(DateTime dt) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(GMONTH_FORMAT);
        return fmt.print(dt);
    }
}
