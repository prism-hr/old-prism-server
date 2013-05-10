package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.InterviewParticipant;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class InterviewParticipantValidatorTest {

    @Autowired  
    private Validator validator;  
    
	private InterviewParticipantValidator interviewParticipantValidator;
    
	@Test
	public void shouldSupportFunding() {
		assertTrue(interviewParticipantValidator.supports(InterviewParticipant.class));
	}
		
	@Before
    public void setup() throws ParseException{
		interviewParticipantValidator = new InterviewParticipantValidator();
		interviewParticipantValidator.setValidator((javax.validation.Validator) validator);
	}
}
