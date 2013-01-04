package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApprovalRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ApprovalRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.SupervisorBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ApprovalRoundValidatorTest {
	
    @Autowired  
    private Validator validator;  
    
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
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("supervisors").getCode());
	}
	
	@Before
	public void setup(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		
		approvalRound = new ApprovalRoundBuilder().application(new ApplicationFormBuilder().id(2).build()).supervisors(new SupervisorBuilder().id(4).build()).build();
		
		approvalRoundValidator = new ApprovalRoundValidator();
		approvalRoundValidator.setValidator((javax.validation.Validator) validator);
	}
}
