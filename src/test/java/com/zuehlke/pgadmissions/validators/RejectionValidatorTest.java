package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class RejectionValidatorTest {
	
    @Autowired
    private Validator validator;
    
    private Rejection rejection;

	private RejectionValidator rejectionValidator;
	
	@Test
	public void shouldSupportRejection() {
		assertTrue(rejectionValidator.supports(Rejection.class));
	}
	
	@Test
	public void shouldRejectIfRejectReasonIsEmpty() {
		rejection.setRejectionReason(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(rejection, "rejectionReason");
		rejectionValidator.validate(rejection, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("rejectionReason").getCode());
	}
	
	@Before
	public void setup(){
		rejection = new RejectionBuilder().rejectionReason(new RejectReasonBuilder().id(1).build()).build();
		rejectionValidator = new RejectionValidator();
		rejectionValidator.setValidator((javax.validation.Validator) validator);
	}
}
