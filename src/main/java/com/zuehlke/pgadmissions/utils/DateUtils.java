package com.zuehlke.pgadmissions.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

public final class DateUtils {

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
    
    public static Date addWorkingDays(final Date startDate, final int numberOfWorkingDays) {
        WorkdayIterator it = new WorkdayIterator(new DateTime(startDate), numberOfWorkingDays);
        DateTime endDate = null;
        while (it.hasNext()) {
            endDate = it.next();
        }
        return endDate.toDate();
    }
    
    private static class WorkdayIterator implements Iterator<DateTime> {
        private final DateTime startDate;
        private final DateTime endDate;
        private DateTime currentDate;
        
        public WorkdayIterator(final DateTime startDate, final int numberOfDays) {
            this.startDate = startDate.plusDays(numberOfDaysToSkip(startDate, true));
            this.endDate = this.startDate.plusDays(numberOfDays);
            this.currentDate = this.startDate;
        }
        
        @Override
        public boolean hasNext() {
            return currentDate.isBefore(endDate);
        }

        @Override
        public DateTime next() {
            currentDate = currentDate.plusDays(numberOfDaysToSkip(currentDate, false));
            return currentDate;
        }

        @Override
        public void remove() {
            throw new NotImplementedException();
        }
        
        private int numberOfDaysToSkip(final DateTime date, final boolean offsetForStartDate) {
            switch (date.getDayOfWeek()) {
            case DateTimeConstants.FRIDAY:
                return 3;
            case DateTimeConstants.SATURDAY:
                return 2;
            case DateTimeConstants.SUNDAY:
                return 1;
            default:
                if (offsetForStartDate) {
                    return 0;
                }
                return 1;
            }
        }
    }
}
