package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Collections;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.Supervisor;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;

public class SupervisorsValidatorTest {

    private ApprovalRound approvalRound;

    private SupervisorsValidator supervisorsValidator;

    @Test
    public void shouldSupportReviewRound() {
        assertTrue(supervisorsValidator.supports(ApprovalRound.class));
    }

    @Test
    public void shouldValidateIfDataIsCorrect() {
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        supervisorsValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(0, mappingResult.getErrorCount());
    }

    @Test
    public void shouldRejectIfSupervisorListIsEmpty() {
        approvalRound.getSupervisors().clear();
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        supervisorsValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("approvalround.supervisors.incomplete", mappingResult.getFieldError("supervisors").getCode());
    }

    @Test
    public void shouldRejectIfOnlyOneSupervisor() {
        Supervisor supervisor = new SupervisorBuilder().id(4).build();

        approvalRound.setSupervisors(Collections.singletonList(supervisor));
        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        supervisorsValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("approvalround.supervisors.incomplete", mappingResult.getFieldError("supervisors").getCode());
    }

    @Test
    public void shouldRejectIfNoSupervisorSetAsPrimary() {
        Supervisor supervisor2 = approvalRound.getSupervisors().get(1);
        supervisor2.setIsPrimary(false);

        DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "approvalRound");
        supervisorsValidator.validate(approvalRound, mappingResult);
        Assert.assertEquals(1, mappingResult.getErrorCount());
        Assert.assertEquals("approvalround.supervisors.noprimary", mappingResult.getFieldError("supervisors").getCode());
    }

    @Before
    public void setup() {

        Supervisor supervisor1 = new SupervisorBuilder().id(4).build();
        Supervisor supervisor2 = new SupervisorBuilder().id(5).isPrimary(true).build();
        approvalRound = new ApprovalRoundBuilder().supervisors(supervisor1, supervisor2).build();

        supervisorsValidator = new SupervisorsValidator();
    }

}
