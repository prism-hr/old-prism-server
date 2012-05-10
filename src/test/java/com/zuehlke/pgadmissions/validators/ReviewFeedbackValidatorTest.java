package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;

public class ReviewFeedbackValidatorTest {
	private ReviewFeedbackValidator validator;
	private ReviewComment reviewComment;

	@Test
	public void shouldSupportComment() {
		assertTrue(validator.supports(ReviewComment.class));
	}

	@Test
	public void shouldRejectIfNotDeclinedAndCommentIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "comment");
		reviewComment.setComment("");
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("reviewComment.comment.notempty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfNotDeclinedAndWillingToSuperviseIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "willingToSupervice");
		reviewComment.setWillingToSupervice(null);
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("reviewComment.willingToSupervice.notempty", mappingResult.getFieldError("willingToSupervice").getCode());
	}
	
	@Test
	public void shouldRejectIfNotDeclinedAndSuitableCandidateIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "willingToSupervice");
		reviewComment.setWillingToSupervice(null);
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("reviewComment.willingToSupervice.notempty", mappingResult.getFieldError("willingToSupervice").getCode());
	}

	
	@Test
	public void shouldNotRejectAnyEmptyFieldIfItIsDeclined() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
		reviewComment.setDecline(CheckedStatus.YES);
		reviewComment.setComment(null);
		reviewComment.setSuitableCandidate(null);
		reviewComment.setWillingToSupervice(null);
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup() {
		validator = new ReviewFeedbackValidator();
		reviewComment = new ReviewCommentBuilder().comment("review comment").suitableCandidate(CheckedStatus.NO).willingToSupervice(CheckedStatus.YES).decline(CheckedStatus.NO).toReviewComment();
	}
}
