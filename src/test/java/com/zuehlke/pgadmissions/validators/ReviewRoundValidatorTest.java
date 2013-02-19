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

import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testValidatorContext.xml")
public class ReviewRoundValidatorTest {
	
    @Autowired
    private Validator validator;
    
    private ReviewRound reviewRound;
	
	private ReviewRoundValidator reviewRoundValidator;
	
	@Test
	public void shouldSupportReviewRound() {
		assertTrue(reviewRoundValidator.supports(ReviewRound.class));
	}
	
	@Test
	public void shouldRejectIfReviewersListIsEmpty() {
		reviewRound.getReviewers().clear();
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewRound, "reviewers");
		reviewRoundValidator.validate(reviewRound, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("reviewers").getCode());
	}
	@Before
	public void setup(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		reviewRound = new ReviewRoundBuilder().application(new ApplicationFormBuilder().id(2).build()).reviewers(new ReviewerBuilder().id(4).build()).build();
		reviewRoundValidator = new ReviewRoundValidator();
		reviewRoundValidator.setValidator((javax.validation.Validator) validator);
	}
}
