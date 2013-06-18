package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ProgramInstanceBuilder;

public class ProgramInstanceTest {
    
    private ProgramInstance programInstance;

    @Test
    public void shouldDateBeWithinBounds() {
        Date startDate = programInstance.getApplicationStartDate();
        assertTrue( programInstance.isDateWithinBounds(DateUtils.addDays(startDate, 8)));
    }
    
    @Test
    public void shouldStartDateBeWithinBounds() {
        Date startDate = programInstance.getApplicationStartDate();
        assertTrue( programInstance.isDateWithinBounds(startDate));
    }
    
    @Test
    public void shouldNotDeadlineBeWithinBounds() {
        Date deadline = programInstance.getApplicationDeadline();
        assertFalse( programInstance.isDateWithinBounds(deadline));
    }
    
    @Before
    public void setUp(){
        Date startDate = new Date();
        Date endDate = DateUtils.addDays(startDate, 200);
        programInstance = new ProgramInstanceBuilder().applicationStartDate(startDate).applicationDeadline(endDate).build();
    }

}
