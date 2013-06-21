package com.zuehlke.pgadmissions.utils;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Days;

public final class DateUtils {
    
    private static final int ONE_DAY_IN_MINUTES = 1440;

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
    
    public static Date addWorkingDaysInMinutes(final Date startDate, final int numberOfMinutes) {
        return addWorkingDaysInMinutes(new DateTime(startDate), numberOfMinutes).toDate();
    }
    
    public static DateTime addWorkingDaysInMinutes(final DateTime date, final int numberOfMinutes) {
        DateTime startDate = new DateTime(date);
        
        if (numberOfMinutes < ONE_DAY_IN_MINUTES) {
            return startDate.plusMinutes(numberOfMinutes);
        }
        
        switch (startDate.getDayOfWeek()) {
        case DateTimeConstants.FRIDAY:
            startDate = startDate.plusDays(3);
            break;
        case DateTimeConstants.SATURDAY:
            startDate = startDate.plusDays(2);
            break;
        case DateTimeConstants.SUNDAY:
            startDate = startDate.plusDays(1);
            break;
        default:
            startDate = startDate.plusDays(1);
            break;
        }
        
        DateTime endDate = new DateTime(startDate).plusDays(1);
        int numberOfWorkingDaysToAdd = (numberOfMinutes / 1400);
        while (workingDaysBetween(startDate, endDate) < numberOfWorkingDaysToAdd) {
            endDate = endDate.plusDays(1);
        }
        return endDate;
    }
    
    public static Date truncateToDay(Date date) {
        return org.apache.commons.lang.time.DateUtils.truncate(date, Calendar.DAY_OF_MONTH);
    }
    
    public static int workingDaysBetween(final DateTime startDate, final DateTime endDate) {
        Days days = Days.daysBetween(startDate, endDate);
        int businessDays  = days.getDays() + 1;
        int fullWeekCount = businessDays / 7;
        
        // find out if there are weekends during the time exceeding the full weeks
        if (businessDays > fullWeekCount * 7) {
            // we are here to find out if there is a 1-day or 2-days weekend
            // in the time interval remaining after subtracting the complete weeks
            int firstDayOfWeek = (int) startDate.getDayOfWeek();
            int lastDayOfWeek = (int) endDate.getDayOfWeek();
            
            if (lastDayOfWeek < firstDayOfWeek) {
                lastDayOfWeek += 7;
            }
            
            if (firstDayOfWeek <= 6) {
                if (lastDayOfWeek >= 7) {
                    // Both Saturday and Sunday are in the remaining time interval
                    businessDays -= 2;
                } else if (lastDayOfWeek >= 6) {
                    // Only Saturday is in the remaining time interval
                    businessDays -= 1;
                }
            } else if (firstDayOfWeek <= 7 && lastDayOfWeek >= 7) {
                // Only Sunday is in the remaining time interval
                businessDays -= 1;
            }
        }

        // subtract the weekends during the full weeks in the interval
        businessDays -= fullWeekCount + fullWeekCount;

        return businessDays;
    }
}
