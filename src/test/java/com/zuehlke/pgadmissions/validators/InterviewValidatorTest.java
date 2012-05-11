package com.zuehlke.pgadmissions.validators;


import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

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
		interview.setInterviewDueDate(null);
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewDueDate");
		interviewValidator.validate(interview, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("interview.interviewDueDate.notempty", mappingResult.getFieldError("interviewDueDate").getCode());
	}

	@Test
	public void shouldRejectIfURLisNotValid() {
		interview.setLocationURL("notvvalidurl");
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "locationURL");
		interviewValidator.validate(interview, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("interview.locationURL.invalid", mappingResult.getFieldError("locationURL").getCode());
	}
	
	@Test
	public void shouldRejectIfDueDateInPast() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		interview.setInterviewDueDate(calendar.getTime());
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interview, "interviewDueDate");
		interviewValidator.validate(interview, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("interview.interviewDueDate.past", mappingResult.getFieldError("interviewDueDate").getCode());
	}
	
	@Before
	public void setup(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		
		interview = new InterviewBuilder().application(new ApplicationFormBuilder().id(2).toApplicationForm()).dueDate(calendar.getTime())
				.furtherDetails("at 9 pm").locationURL("http://www.ucl.ac.uk").toInterview();
		interviewValidator = new InterviewValidator();
	}
	
}

