package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

public class ProgramInstanceTest {
    
    private ProgramInstance programInstance;

    @Test
    public void shouldDateBeWithinBounds() {
        LocalDate startDate = programInstance.getApplicationStartDate();
        assertTrue( programInstance.isDateWithinBounds(startDate.plusDays(8)));
    }
    
    @Test
    public void shouldStartDateBeWithinBounds() {
        LocalDate startDate = programInstance.getApplicationStartDate();
        assertTrue( programInstance.isDateWithinBounds(startDate));
    }
    
    @Test
    public void shouldNotDeadlineBeWithinBounds() {
        LocalDate deadline = programInstance.getApplicationDeadline();
        assertFalse( programInstance.isDateWithinBounds(deadline));
    }
    
    @Before
    public void setUp(){
        LocalDate startDate = new LocalDate();
        LocalDate endDate = startDate.plusDays(200);
        programInstance = new ProgramInstance().withApplicationStartDate(startDate).withApplicationDeadline(endDate);
    }

}
