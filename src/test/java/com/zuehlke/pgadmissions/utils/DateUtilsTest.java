package com.zuehlke.pgadmissions.utils;

import static org.junit.Assert.*;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void shouldAddTheCorrectNumberOfWorkingDaysToADateStartingOnSunday() {
        // Sunday 3. March 2013
        DateTime saturday = new DateTime(2013, 3, 3, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDays(saturday.toDate(), 1);

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
        Date addWorkingDays = DateUtils.addWorkingDays(saturday.toDate(), 1);

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
        Date addWorkingDays = DateUtils.addWorkingDays(saturday.toDate(), 1);

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
        Date addWorkingDays = DateUtils.addWorkingDays(saturday.toDate(), 1);

        // Tuesday 29. Jan 2013
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.TUESDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.JANUARY, endDate.getMonthOfYear());
        assertEquals(2013, endDate.getYear());
    }
    
    @Test
    public void shouldAddTheCorrectNumberOfWorkingDays() {
        // Monday 28. Jan 2013
        DateTime saturday = new DateTime(2013, 1, 28, 8, 0);
        Date addWorkingDays = DateUtils.addWorkingDays(saturday.toDate(), 5);

        // Monday 4. Feb 2013
        DateTime endDate = new DateTime(addWorkingDays);
        assertEquals(DateTimeConstants.MONDAY, endDate.getDayOfWeek());
        assertEquals(DateTimeConstants.FEBRUARY, endDate.getMonthOfYear());
        assertEquals(2013, endDate.getYear());
    }
}
