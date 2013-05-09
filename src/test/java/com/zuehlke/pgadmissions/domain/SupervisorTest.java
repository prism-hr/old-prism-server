package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;

public class SupervisorTest {

    @Test
    public void shouldHasDeclinedSupervisionReturnTrue() {
        Supervisor supervisor = new SupervisorBuilder().declinedSupervisionReason("Ten kraj jest nasz i Wasz, nie damy bic sie w twarz").build();
        assertTrue(supervisor.hasDeclinedSupervision());
    }
    
    @Test
    public void shouldHasDeclinedSupervisionReturnFalse() {
        Supervisor supervisor = new SupervisorBuilder().build();
        assertFalse(supervisor.hasDeclinedSupervision());
    }

    @Test
    public void shouledHasRespondedReturnTrueIfSupervisorConfirmed() {
        Supervisor supervisor = new SupervisorBuilder().confirmedSupervision(true).build();
        assertTrue(supervisor.hasResponded());
    }
    
    @Test
    public void shouledHasRespondedReturnTrueIfSupervisorDeclined() {
        Supervisor supervisor = new SupervisorBuilder().declinedSupervisionReason("Declined").build();
        assertTrue(supervisor.hasResponded());
    }
    
    @Test
    public void shouledHasRespondedReturnFalse() {
        Supervisor supervisor = new SupervisorBuilder().build();
        assertFalse(supervisor.hasResponded());
    }

}
