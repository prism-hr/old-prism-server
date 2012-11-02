package com.zuehlke.pgadmissions.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private DateUtils() {
    }
    
    public static boolean isToday(final Date date) {
        return isSameDay(date, Calendar.getInstance().getTime());
    }
    
    private static boolean isSameDay(final Date date1, final Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return isSameDay(cal1, cal2);
    }
    
    private static boolean isSameDay(final Calendar cal1, final Calendar cal2) {
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }
}
