package com.zuehlke.pgadmissions.validators;


import static org.junit.Assert.assertTrue;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.Country;
import com.zuehlke.pgadmissions.domain.Interview;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.InterviewBuilder;

public class InterviewValidatorTest {

	
	private Interview interview;
	private InterviewValidator interviewValidator;
	
	@Test
	public void shouldSupportRefereeValidator() {
		assertTrue(interviewValidator.supports(Interview.class));
	}
	
	@Test
	public void shouldRejectIfFurtherDetailsIsEmpty() {
		interview.setFurtherDetails(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "furtherDetails");
		interviewValidator.validate(interview, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("interview.furtherDetails.notempty", mappingResult.getFieldError("furtherDetails").getCode());
	}
	
	@Test
	public void shouldRejectIfDueDateIsEmpty() {
		interview.setDueDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "dueDate");
		interviewValidator.validate(interview, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("interview.dueDate.notempty", mappingResult.getFieldError("dueDate").getCode());
	}

	@Test
	public void shouldRejectIfURLisNotValid() {
		interview.setLocationURL("notvvalidurl");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "locationURL");
		interviewValidator.validate(interview, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("interview.locationURL.invalid", mappingResult.getFieldError("locationURL").getCode());
	}
	
	@Before
	public void setup(){
		
		interview = new InterviewBuilder().application(new ApplicationFormBuilder().id(2).toApplicationForm()).dueDate(new Date())
				.furtherDetails("at 9 pm").locationURL("http://www.ucl.ac.uk").toInterview();
		interviewValidator = new InterviewValidator();
	}
	
}

