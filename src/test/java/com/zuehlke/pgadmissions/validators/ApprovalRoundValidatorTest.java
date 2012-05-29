package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;

public class ApprovalRoundValidatorTest {
	private ApprovalRound approvalRound;
	private ApprovalRoundValidator approvalRoundValidator;
	
	@Test
	public void shouldSupportReviewRound() {
		assertTrue(approvalRoundValidator.supports(ApprovalRound.class));
	}
	
	@Test
	public void shouldRejectIfSupervisorListIsEmpty() {
		approvalRound.getSupervisors().clear();
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(approvalRound, "supervisors");
		approvalRoundValidator.validate(approvalRound, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("approvalround.supervisors.notempty", mappingResult.getFieldError("supervisors").getCode());
	}
	@Before
	public void setup(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		
		approvalRound = new ApprovalRoundBuilder().application(new ApplicationFormBuilder().id(2).toApplicationForm()).supervisors(new SupervisorBuilder().id(4).toSupervisor()).toApprovalRound();
		approvalRoundValidator = new ApprovalRoundValidator();
	}
}
