package com.zuehlke.pgadmissions.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;

public class ApprovalRoundTest {

    @Test
    public void shouldReturnNullIfNoPrimarySupervisor() {
        Supervisor supervisor1 = new SupervisorBuilder().build();
        Supervisor supervisor2 = new SupervisorBuilder().build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build();

        assertNull(approvalRound.getPrimarySupervisor());
    }

    @Test
    public void shouldReturnPrimarySupervisor() {
        Supervisor supervisor1 = new SupervisorBuilder().build();
        Supervisor supervisor2 = new SupervisorBuilder().isPrimary(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build();

        assertSame(supervisor2, approvalRound.getPrimarySupervisor());
    }

    @Test
    public void shouldReturnFalseIfPrimarySupervisorNotResponded() {
        Supervisor supervisor1 = new SupervisorBuilder().build();
        Supervisor supervisor2 = new SupervisorBuilder().isPrimary(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build();

        assertFalse(approvalRound.hasPrimarySupervisorResponded());
    }

    @Test
    public void shouldReturnFalseIfPrimarySupervisorConfirmedSupervision() {
        Supervisor supervisor1 = new SupervisorBuilder().build();
        Supervisor supervisor2 = new SupervisorBuilder().isPrimary(true).confirmedSupervision(true).build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build();

        assertTrue(approvalRound.hasPrimarySupervisorResponded());
    }

    @Test
    public void shouldReturnFalseIfPrimarySupervisorDeclinedSupervision() {
        Supervisor supervisor1 = new SupervisorBuilder().build();
        Supervisor supervisor2 = new SupervisorBuilder().isPrimary(true).declinedSupervisionReason("sss").build();
        ApprovalRound approvalRound = new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build();

        assertTrue(approvalRound.hasPrimarySupervisorResponded());
    }

}
