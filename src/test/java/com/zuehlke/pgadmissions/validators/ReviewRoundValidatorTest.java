package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ReviewRound;
import com.zuehlke.pgadmissions.domain.builders.ApplicationFormBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewRoundBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewerBuilder;

public class ReviewRoundValidatorTest {
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
		Assert.assertEquals("reviewround.reviewers.notempty", mappingResult.getFieldError("reviewers").getCode());
	}
	@Before
	public void setup(){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_YEAR, 1);
		
		reviewRound = new ReviewRoundBuilder().application(new ApplicationFormBuilder().id(2).toApplicationForm()).reviewers(new ReviewerBuilder().id(4).toReviewer()).toReviewRound();
		reviewRoundValidator = new ReviewRoundValidator();
	}
}
