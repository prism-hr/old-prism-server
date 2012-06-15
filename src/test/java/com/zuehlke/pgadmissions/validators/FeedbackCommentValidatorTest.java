package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.DirectFieldBindingResult;

import com.zuehlke.pgadmissions.domain.InterviewComment;
import com.zuehlke.pgadmissions.domain.ReviewComment;
import com.zuehlke.pgadmissions.domain.builders.InterviewCommentBuilder;
import com.zuehlke.pgadmissions.domain.builders.ReviewCommentBuilder;

public class FeedbackCommentValidatorTest {
	private FeedbackCommentValidator validator;
	private ReviewComment reviewComment;
	private InterviewComment interviewComment;

	@Test
	public void shouldSupportComment() {
		assertTrue(validator.supports(ReviewComment.class));
		assertTrue(validator.supports(InterviewComment.class));
	}

	@Test
	public void shouldRejectIfNotDeclinedAndCommentIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "comment");
		reviewComment.setComment("");
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectIfNotDeclinedAndWillingToInterviewIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "willingToInterview");
		reviewComment.setWillingToInterview(null);
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToInterview").getCode());
	}
	
	@Test
	public void shouldRejectIfNotDeclinedAndSuitableCandidateIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "willingToInterview");
		reviewComment.setWillingToInterview(null);
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToInterview").getCode());
	}

	
	@Test
	public void shouldNotRejectAnyEmptyFieldIfItIsDeclined() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(reviewComment, "reviewComment");
		reviewComment.setDecline(true);
		reviewComment.setComment(null);
		reviewComment.setSuitableCandidate(null);
		reviewComment.setWillingToInterview(null);
		validator.validate(reviewComment, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Test
	public void shouldRejectIfNotDeclinedAndInterviewCommentIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "comment");
		interviewComment.setComment("");
		validator.validate(interviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("text.field.empty", mappingResult.getFieldError("comment").getCode());
	}
	
	@Test
	public void shouldRejectInterviewCommentIfNotDeclinedAndWillingToSuperviseIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "willingToSupervise");
		interviewComment.setWillingToSupervise(null);
		validator.validate(interviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToSupervise").getCode());
	}
	
	@Test
	public void shouldRejectInterviewCommentIfNotDeclinedAndSuitableCandidateIsEmpty() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "willingToSupervise");
		interviewComment.setWillingToSupervise(null);
		validator.validate(interviewComment, mappingResult);
		Assert.assertEquals(1, mappingResult.getErrorCount());
		Assert.assertEquals("dropdown.radio.select.none", mappingResult.getFieldError("willingToSupervise").getCode());
	}
	
	
	@Test
	public void shouldNotRejectInterviewCommentAnyEmptyFieldIfItIsDeclined() {
		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(interviewComment, "interviewComment");
		interviewComment.setDecline(true);
		interviewComment.setComment(null);
		interviewComment.setSuitableCandidate(null);
		interviewComment.setWillingToSupervise(null);
		validator.validate(interviewComment, mappingResult);
		Assert.assertEquals(0, mappingResult.getErrorCount());
	}
	
	@Before
	public void setup() {
		validator = new FeedbackCommentValidator();
		reviewComment = new ReviewCommentBuilder().comment("review comment").suitableCandidate(false).willingToInterview(true).decline(false).toReviewComment();
		interviewComment = new InterviewCommentBuilder().comment("interview comment").suitableCandidate(false).willingToSupervice(true).decline(false).toInterviewComment();
	}
}
