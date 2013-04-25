package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

public class DateUtilsTest {

    private static final int ONE_DAY_IN_MINUTES = 1440;
    
    @Test
    public void shouldAddTheCorrectNumberOfWorkingDaysToADateStartingOnSunday() {
        // Sunday 3. March 2013
        DateTime saturday = new DateTime(2013, 3, 3, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDaysInMinutes(saturday.toDate(), 1 * ONE_DAY_IN_MINUTES);

        // Tuesday 4. March 2013
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.TUESDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.MARCH, endDate.getMonthOfYear());
        assertEquals(2013, endDate.getYear());
    }
    
    @Test
    public void shouldAddTheCorrectNumberOfWorkingDaysToADateStartingOnSaturday() {
        // Saturday 2. March 2013
        DateTime saturday = new DateTime(2013, 3, 2, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDaysInMinutes(saturday.toDate(), 1 * ONE_DAY_IN_MINUTES);

        // Tuesday 4. March 2013
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.TUESDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.MARCH, endDate.getMonthOfYear());
        assertEquals(2013, endDate.getYear());
    }
    
    @Test
    public void shouldAddTheCorrectNumberOfWorkingDaysToADateStartingOnFriday() {
        // Friday 1. March 2013
        DateTime saturday = new DateTime(2013, 3, 1, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDaysInMinutes(saturday.toDate(), 1 * ONE_DAY_IN_MINUTES);

        // Tuesday 4. March 2013
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.TUESDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.MARCH, endDate.getMonthOfYear());
        assertEquals(2013, endDate.getYear());
    }
    
    @Test
    public void shouldAddTheCorrectNumberOfWorkingDaysToADateStartingOnMonday() {
        // Monday 28. Jan 2013
        DateTime saturday = new DateTime(2013, 1, 28, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDaysInMinutes(saturday.toDate(), 1 * ONE_DAY_IN_MINUTES);

        // Wednesday 30. Jan 2013
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.WEDNESDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.JANUARY, endDate.getMonthOfYear());
        assertEquals(2013, endDate.getYear());
    }
    
    @Test
    public void shouldAddTheCorrectNumberOfWorkingDays() {
        // Monday 28. Jan 2013
        DateTime saturday = new DateTime(2013, 1, 28, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDaysInMinutes(saturday.toDate(), 5 * ONE_DAY_IN_MINUTES);

        // Monday 4. Feb 2013
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.MONDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.FEBRUARY, endDate.getMonthOfYear());
        assertEquals(2013, endDate.getYear());
    }
    
    @Test
    public void shouldAddTheCorrectNumberOfWorkingDaysToAnIntervalWhichEndsOnAWeekend() {
        // Sunday 3. March 2013
        DateTime saturday = new DateTime(2013, 3, 3, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDaysInMinutes(saturday.toDate(), 7 * ONE_DAY_IN_MINUTES);

        // Tuesday 12. March 2013
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.TUESDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.MARCH, endDate.getMonthOfYear());
        assertEquals(2013, endDate.getYear());
    }
    
    @Test
    public void shouldAddTheCorrectNumberOfWorkingDaysToATwoDayInterval() {
        // Monday 4. March 2013
        DateTime saturday = new DateTime(2013, 3, 4, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDaysInMinutes(saturday.toDate(), 2 * ONE_DAY_IN_MINUTES);

        // Wednesday 6. March 2013
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.WEDNESDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.MARCH, endDate.getMonthOfYear());
        assertEquals(2013, endDate.getYear());
    }
    
    @Test
    public void shouldAddTheCorrectNumberOfWorkingDaysToA5MinuteInterval() {
        // Sunday 4. March 2013 8am
        DateTime saturday = new DateTime(2013, 3, 4, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDaysInMinutes(saturday.toDate(), 5);

        // Monday 3. March 2013 8:05am
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.SUNDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.MARCH, endDate.getMonthOfYear());
        assertEquals(8, endDate.getHourOfDay());
        assertEquals(485, endDate.getMinuteOfDay());
        assertEquals(2013, endDate.getYear());
    }
}
