package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Rejection;
import com.zuehlke.pgadmissions.domain.builders.RejectReasonBuilder;
import com.zuehlke.pgadmissions.domain.builders.RejectionBuilder;

public class RejectionValidatorTest {
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
		
		rejection = new RejectionBuilder().rejectionReason(new RejectReasonBuilder().id(1).toRejectReason()).toRejection();
		rejectionValidator = new RejectionValidator();
	}
}
